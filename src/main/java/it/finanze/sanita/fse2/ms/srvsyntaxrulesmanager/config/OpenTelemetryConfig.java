package it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.config;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Tracer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.config.Constants.Microservice.MS_NAME;

@Configuration
public class OpenTelemetryConfig {

    @Bean
    public Tracer tracer() {
        return GlobalOpenTelemetry.getTracer(MS_NAME);
    }
}