package it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.response.changes;

import lombok.Value;

import javax.validation.constraints.Size;

import static it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.utils.UtilsOA.OA_ANY_STRING_MAX;
import static it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.utils.UtilsOA.OA_ANY_STRING_MIN;

@Value
public class ChangeSetDTO {

    @Size(min = OA_ANY_STRING_MIN, max = OA_ANY_STRING_MAX)
    String id;
    Payload description;
    @Value
    public static class Payload {
        @Size(min = OA_ANY_STRING_MIN, max = OA_ANY_STRING_MAX)
        String extension;
        @Size(min = OA_ANY_STRING_MIN, max = OA_ANY_STRING_MAX)
        String filename;
    }
}
