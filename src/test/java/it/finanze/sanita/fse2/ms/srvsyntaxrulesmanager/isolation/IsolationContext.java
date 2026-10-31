package it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.isolation;

import brave.Tracer;
import brave.Tracing;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.controller.impl.*;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.controller.handler.ExceptionCTL;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.repository.mongo.impl.*;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.service.impl.*;
import org.springframework.context.annotation.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import static org.mockito.Mockito.mock;

/** Explicit graph: no Boot auto-configuration, server, database, or network. */
@Configuration
@org.springframework.context.annotation.Profile("ISOLATION")
@Import({DocumentSRV.class, ChangeSetSRV.class, DocumentRepo.class, ChangeSetRepo.class,
    EdsDocumentsCTL.class, ChangeSetCTL.class, ExceptionCTL.class})
class IsolationContext {
    @Bean MongoTemplate mongo() { return mock(MongoTemplate.class); }
    @Bean(destroyMethod = "close") Tracing tracing() { return Tracing.newBuilder().build(); }
    @Bean Tracer tracer(Tracing tracing) { return tracing.tracer(); }
}
