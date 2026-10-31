package it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.isolation;

import brave.Tracer;
import brave.Span;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.controller.handler.ExceptionCTL;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.response.error.base.ErrorResponseDTO;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.exceptions.*;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.validators.ValidObjectId;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.core.MethodParameter;
import org.springframework.http.*;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import javax.validation.*;
import java.util.*;
import static org.assertj.core.api.Assertions.*;

@Tag("ISOLATION")
@org.springframework.test.context.ActiveProfiles("ISOLATION")
@SpringJUnitConfig(classes = {IsolationContext.class, ErrorIsolationTest.Config.class})
class ErrorIsolationTest {
    @org.springframework.context.annotation.Profile("ISOLATION")
    @org.springframework.context.annotation.Configuration static class Config { @Bean Handler handler() { return new Handler(); } }
    static class Handler extends ExceptionCTL {
        ResponseEntity<?> handle(Exception ex) {
            if (ex instanceof DocumentNotFoundException) return handleDocumentNotFoundException((DocumentNotFoundException) ex);
            if (ex instanceof RootNotValidException) return handleRootNotValidException((RootNotValidException) ex);
            if (ex instanceof DataIntegrityException) return handleDataIntegrityException((DataIntegrityException) ex);
            if (ex instanceof OperationException) return handleOperationException((OperationException) ex);
            if (ex instanceof DataProcessingException) return handleDataProcessingException((DataProcessingException) ex);
            if (ex instanceof ExtensionAlreadyExistsException) return handleResourceAlreadyExistsException((ExtensionAlreadyExistsException) ex);
            if (ex instanceof ExtensionNotFoundException) return handleResourceNotFoundException((ExtensionNotFoundException) ex);
            if (ex instanceof InvalidContentException) return handleInvalidContentException((InvalidContentException) ex);
            if (ex instanceof SchemaValidatorException) return handleSchemaValidatorException((SchemaValidatorException) ex);
            if (ex instanceof ConstraintViolationException) return handleConstraintViolationException((ConstraintViolationException) ex);
            if (ex instanceof MethodArgumentTypeMismatchException) return handleMethodArgumentTypeMismatchException((MethodArgumentTypeMismatchException) ex);
            if (ex instanceof MissingServletRequestPartException) return handleMissingServletRequestPart((MissingServletRequestPartException) ex, new HttpHeaders(), HttpStatus.BAD_REQUEST, null);
            if (ex instanceof MissingServletRequestParameterException) return handleMissingServletRequestParameter((MissingServletRequestParameterException) ex, new HttpHeaders(), HttpStatus.BAD_REQUEST, null);
            return handleGenericException(ex);
        }
    }
    static class Input { @javax.validation.constraints.NotNull String id; }
    public void dateParameter(Date lastUpdate) { }
    @Autowired Handler handler;
    @Autowired Tracer tracer;

    @Test void errorsHaveConsistentProblemDetailsAndStatus() throws Exception {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            List<Exception> errors = Arrays.asList(
                new DocumentNotFoundException("missing"), new RootNotValidException("root invalid", "root"),
                new DataIntegrityException("mismatch"), new OperationException("offline", new com.mongodb.MongoException("offline")),
                new DataProcessingException("read failed", new RuntimeException()), new ExtensionAlreadyExistsException("duplicate"),
                new ExtensionNotFoundException("missing ext"), new InvalidContentException("invalid"),
                new SchemaValidatorException("invalid xsd", new RuntimeException()),
                new ConstraintViolationException(factory.getValidator().validate(new Input())),
                new MethodArgumentTypeMismatchException("bad", Date.class, "lastUpdate", new MethodParameter(getClass().getMethod("dateParameter", Date.class), 0), new IllegalArgumentException()),
                new MissingServletRequestPartException("files"), new MissingServletRequestParameterException("root", "String"),
                new Exception("unexpected"));
            int[] statuses = {404, 400, 500, 500, 422, 409, 404, 400, 422, 400, 400, 400, 400, 500};
            for (int i = 0; i < errors.size(); i++) {
                ResponseEntity<?> response = handler.handle(errors.get(i));
                assertThat(response.getStatusCodeValue()).isEqualTo(statuses[i]);
                assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_PROBLEM_JSON);
                ErrorResponseDTO body = (ErrorResponseDTO) response.getBody();
                assertThat(body.getStatus()).isEqualTo(statuses[i]);
                assertThat(body.getType()).startsWith("/msg/");
                assertThat(body.getTitle()).isNotBlank();
                assertThat(body.getDetail()).isNotBlank();
                assertThat(body.getInstance()).startsWith("/");
                assertThat(body.getTraceID()).isNull();
            }
        }
    }

    @Test void errorResponseCarriesCurrentTrace() {
        Span span = tracer.nextSpan().start();
        try (Tracer.SpanInScope scope = tracer.withSpanInScope(span)) {
            ErrorResponseDTO body = (ErrorResponseDTO) handler.handle(new Exception("failure")).getBody();
            assertThat(body.getTraceID()).isEqualTo(span.context().traceIdString());
            assertThat(body.getSpanID()).isEqualTo(span.context().spanIdString());
        } finally { span.finish(); }
    }
}
