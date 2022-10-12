package it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.response.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.util.UriComponentsBuilder;

@Getter
@RequiredArgsConstructor
public enum ErrorType {

    VALIDATION("/msg/validation","Errore di validazione"),
    RESOURCE("/msg/resource", "Errore risorsa"),
    SERVER("/msg/server", "Errore interno"),
    IO("/msg/io", "Errore IO");

    private final String type;
    private final String title;

    public String toInstance(String instance) {
        return UriComponentsBuilder
            .fromUriString(instance)
            .build()
            .toUriString();
    }

    public String toInstance(String instance, String ...members) {
        return UriComponentsBuilder
            .fromUriString(instance)
            .pathSegment(members)
            .build()
            .toUriString();
    }


}
