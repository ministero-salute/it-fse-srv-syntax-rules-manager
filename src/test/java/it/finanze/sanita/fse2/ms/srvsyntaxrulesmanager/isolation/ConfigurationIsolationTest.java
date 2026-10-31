package it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.isolation;

import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.*;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.servers.Server;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.config.*;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.repository.CollectionNaming;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.repository.entity.SchemaETY;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.repository.mongo.IDocumentRepo;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.runner.TestFileLoader;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.utility.ProfileUtility;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.exceptions.OperationException;
import org.junit.jupiter.api.*;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.MapPropertySource;
import java.util.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@Tag("ISOLATION")
class ConfigurationIsolationTest {
    @Test void profileControlsCollectionName() {
        for (String profile : Arrays.asList("", "dev", "TEST_local")) {
            try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
                if (!profile.isEmpty()) context.getEnvironment().setActiveProfiles(profile);
                context.register(ProfileUtility.class, CollectionNaming.class); context.refresh();
                assertThat(context.getBean(ProfileUtility.class).isTestProfile()).isEqualTo(profile.startsWith("TEST"));
                assertThat(context.getBean("schemaBean")).isEqualTo(profile.startsWith("TEST") ? "test_schema_eds" : "schema_eds");
            }
        }
    }

    @Test void openApiCustomizersSetMetadataAndCloseSchemas() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            Map<String, Object> properties = new HashMap<>();
            for (String key : Arrays.asList("api-id", "summary", "title", "description", "termsOfService", "contact.name", "contact.url", "contact.mail"))
                properties.put("docs.info." + key, key);
            properties.put("info.app.version", "1.2"); properties.put("server.port", "8080"); properties.put("validation.file-max-size", "1024");
            context.getEnvironment().getPropertySources().addFirst(new MapPropertySource("isolation", properties));
            context.register(CustomSwaggerCFG.class, OpenApiCFG.class); context.refresh();
            Schema<?> response = new ObjectSchema(); Schema<?> body = new ObjectSchema();
            OpenAPI api = new OpenAPI().info(new Info()).components(new Components().addSchemas("response", response))
                .servers(Arrays.asList(new Server().url("https://example.org"), new Server().url("http://localhost")))
                .paths(new Paths().addPathItem("/schema", new PathItem()
                    .get(new Operation())
                    .post(new Operation().requestBody(new RequestBody().content(new Content()
                        .addMediaType("application/xml", new MediaType().schema(body))
                        .addMediaType("text/plain", new MediaType()))))));
            OpenApiCFG config = context.getBean(OpenApiCFG.class);
            config.disableAdditionalRequestProperties().customise(api);
            config.disableAdditionalResponseProperties().customise(api);
            assertThat(api.getInfo().getTitle()).isEqualTo("title");
            assertThat(api.getInfo().getVersion()).isEqualTo("1.2");
            assertThat(api.getInfo().getContact().getName()).isEqualTo("contact.name");
            assertThat(api.getInfo().getExtensions()).containsEntry("x-api-id", "api-id");
            assertThat(api.getServers().get(0).getExtensions()).isNull();
            assertThat(api.getServers().get(1).getExtensions()).containsEntry("x-sandbox", true);
            assertThat(response.getAdditionalProperties()).isEqualTo(false);
            assertThat(body.getAdditionalProperties()).isEqualTo(false);
        }
    }

    @Test void runnerLoadsRealFixtureFilesOnlyWhenAbsentAndHandlesDatabaseFailure() throws Exception {
        IDocumentRepo repository = mock(IDocumentRepo.class);
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.getEnvironment().setActiveProfiles("runner");
            context.registerBean(IDocumentRepo.class, () -> repository);
            context.register(TestFileLoader.class); context.refresh();
            TestFileLoader runner = context.getBean(TestFileLoader.class);
            runner.run();
            org.mockito.ArgumentCaptor<List<SchemaETY>> entities = org.mockito.ArgumentCaptor.forClass(List.class);
            verify(repository).insertDocsByExtensionId(entities.capture());
            assertThat(entities.getValue()).hasSize(10);
            assertThat(entities.getValue().stream().filter(SchemaETY::getRootSchema).map(SchemaETY::getNameSchema)).containsExactly("CDA.xsd");
            assertThat(entities.getValue()).allSatisfy(e -> {
                assertThat(e.getTypeIdExtension()).isEqualTo(TestFileLoader.SCHEMA_TEST_ID);
                assertThat(e.getContentSchema().getData()).isNotEmpty();
            });
            when(repository.isExtensionInserted(TestFileLoader.SCHEMA_TEST_ID)).thenReturn(true);
            runner.run(); verify(repository, times(1)).insertDocsByExtensionId(anyList());
            when(repository.isExtensionInserted(TestFileLoader.SCHEMA_TEST_ID)).thenThrow(new OperationException("offline", new com.mongodb.MongoException("offline")));
            assertThatCode(() -> runner.run()).doesNotThrowAnyException();
            doReturn(false).when(repository).isExtensionInserted(TestFileLoader.SCHEMA_TEST_ID);
            when(repository.insertDocsByExtensionId(anyList())).thenThrow(new OperationException("write failure", new com.mongodb.MongoException("offline")));
            assertThatCode(() -> runner.run()).doesNotThrowAnyException();
        }
    }
}
