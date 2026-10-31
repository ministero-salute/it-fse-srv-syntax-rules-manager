package it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.isolation;

import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.*;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.exceptions.*;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.repository.entity.SchemaETY;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.validators.impl.*;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.validators.schema.*;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.utility.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXParseException;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.isolation.Fixtures.*;

@Tag("ISOLATION")
class ValidationIsolationTest {
    @TempDir Path temp;
    @Test void schemaCompilationResolvesIncludesAndRejectsMissingOrInvalidRoots() throws Exception {
        String include = "<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\"><xs:include schemaLocation=\"child.xsd\"/></xs:schema>";
        SchemaValidator validator = new SchemaValidator("root.xsd");
        validator.verify(new MultipartFile[]{file("root.xsd", include), file("child.xsd", XSD)});
        assertThat(validator.resolveResource(null, null, "public", "child.xsd", "base").getSystemId()).isEqualTo("child.xsd");
        assertThat(validator.resolveResource(null, null, null, "missing.xsd", null)).isNull();
        assertThatThrownBy(() -> new SchemaValidator("root.xsd").verify(new MultipartFile[]{file("root.xsd", include)})).isInstanceOf(SchemaValidatorException.class);
        assertThatThrownBy(() -> new SchemaValidator("missing.xsd").verify(files())).isInstanceOf(SchemaValidatorException.class).hasCauseInstanceOf(RootNotValidException.class);
        assertThatThrownBy(() -> new SchemaValidator("root.xsd").verify(new MultipartFile[]{file("root.xsd", "invalid")})).isInstanceOf(SchemaValidatorException.class);
        MultipartFile broken = mock(MultipartFile.class);
        when(broken.getBytes()).thenThrow(new IOException("read failed"));
        assertThatThrownBy(() -> validator.verify(new MultipartFile[]{broken})).isInstanceOf(SchemaValidatorException.class).hasCauseInstanceOf(IOException.class);
        assertThatThrownBy(() -> validator.verify(new HashMap<>(), new MultipartFile[]{broken})).isInstanceOf(SchemaValidatorException.class);
        Map<String, byte[]> current = new HashMap<>(); current.put("root.xsd", XSD.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        validator.verify(current, files());
        SchemaErrorHandler handler = new SchemaErrorHandler();
        SAXParseException failure = new SAXParseException("bad", null);
        assertThatThrownBy(() -> handler.error(failure)).isSameAs(failure);
        assertThatThrownBy(() -> handler.warning(failure)).isSameAs(failure);
        assertThatThrownBy(() -> handler.fatalError(failure)).isSameAs(failure);
    }

    @Test void entityConversionHandlesFilesAndReadFailures() throws Exception {
        Path path = temp.resolve("root.xsd"); Files.write(path, new byte[]{1, 2, 3});
        SchemaETY e = SchemaETY.fromPath(path, "ext", true);
        assertThat(e.getContentSchema().getData()).containsExactly(1, 2, 3);
        assertThat(e.getNameSchema()).isEqualTo("root.xsd");
        assertThat(e.getInsertionDate()).isEqualTo(e.getLastUpdateDate());
        assertThatThrownBy(() -> SchemaETY.fromPath(temp.resolve("missing"), "ext", false)).isInstanceOf(DataProcessingException.class);
        MultipartFile broken = mock(MultipartFile.class); when(broken.getBytes()).thenThrow(new IOException("failed"));
        assertThatThrownBy(() -> SchemaETY.fromMultipart(broken, "ext", false)).isInstanceOf(DataProcessingException.class);
    }

    @Test void schemaMetadataUsesEarliestInsertionLatestUpdateAndActiveRoot() throws Exception {
        SchemaETY old = entity(); old.setDeleted(true); old.setInsertionDate(new Date(1)); old.setLastUpdateDate(new Date(5));
        SchemaETY active = entity(); active.setNameSchema("new.xsd"); active.setInsertionDate(new Date(2)); active.setLastUpdateDate(new Date(10));
        List<SchemaDocumentDTO> items = new ArrayList<>(Arrays.asList(SchemaDocumentDTO.fromEntity(old), SchemaDocumentDTO.fromEntity(active)));
        SchemaDTO schema = SchemaDTO.fromItems("ext", items, new SchemaDocumentDTO.Options(true));
        assertThat(schema.getNameRoot()).isEqualTo("new.xsd");
        assertThat(schema.isDeleted()).isFalse();
        assertThat(schema.getInsertionDate().toInstant().toEpochMilli()).isEqualTo(1);
        assertThat(schema.getLastUpdateDate().toInstant().toEpochMilli()).isEqualTo(10);
        active.setDeleted(true);
        schema = SchemaDTO.fromItems("ext", new ArrayList<>(Collections.singletonList(SchemaDocumentDTO.fromEntity(active))), new SchemaDocumentDTO.Options(false));
        assertThat(schema.isDeleted()).isTrue();
        active.setRootSchema(false);
        assertThatThrownBy(() -> SchemaDTO.fromItems("ext", new ArrayList<>(Collections.singletonList(SchemaDocumentDTO.fromEntity(active))), new SchemaDocumentDTO.Options(true))).isInstanceOf(IllegalStateException.class);
        active.setDeleted(false);
        assertThatThrownBy(() -> SchemaDTO.fromItems("ext", new ArrayList<>(Collections.singletonList(SchemaDocumentDTO.fromEntity(active))), new SchemaDocumentDTO.Options(true))).isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> SchemaDTO.fromItems("ext", new ArrayList<>(), new SchemaDocumentDTO.Options(true))).isInstanceOf(IllegalStateException.class);
        assertThat(MiscUtility.getYesterday()).isBefore(new Date());
    }

    @Test void inputValidatorsCoverNullValidAndInvalidValues() {
        ObjectIdValidator ids = new ObjectIdValidator(); ids.initialize(null);
        assertThat(ids.isValid(null, null)).isFalse(); assertThat(ids.isValid("bad", null)).isFalse(); assertThat(ids.isValid(ID, null)).isTrue();
        NoFutureDateValidator dates = new NoFutureDateValidator(); dates.initialize(null);
        assertThat(dates.isValid(null, null)).isTrue(); assertThat(dates.isValid(new Date(1), null)).isTrue(); assertThat(dates.isValid(new Date(Long.MAX_VALUE), null)).isFalse();
        UniqueMultipartValidator unique = new UniqueMultipartValidator(); unique.initialize(null);
        assertThat(unique.isValid(files(), null)).isTrue();
        assertThat(unique.isValid(new MultipartFile[]{files()[0], files()[0]}, null)).isFalse();
        assertThat(new ProfileUtility().isTestProfile()).isFalse();
    }
}
