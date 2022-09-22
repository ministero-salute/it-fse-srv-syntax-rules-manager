package it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.response.changes;

import lombok.Value;
import org.bson.types.ObjectId;

import javax.validation.constraints.Size;

import static it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.utils.UtilsOA.OA_ANY_STRING_MAX;
import static it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.utils.UtilsOA.OA_ANY_STRING_MIN;

/**
 * The changeset item DTO
 */
@Value
public class ChangeSetDTO {

    /**
     * Identifier, usually an {@link ObjectId}
     */
    @Size(min = OA_ANY_STRING_MIN, max = OA_ANY_STRING_MAX)
    String id;
    /**
     * Holds human-readable information, differ by each microservice
     */
    Payload description;

    /**
     * The changeset item payload DTO
     */
    @Value
    public static class Payload {
        /**
         * The resource extension identifier
         */
        @Size(min = OA_ANY_STRING_MIN, max = OA_ANY_STRING_MAX)
        String extension;
        /**
         * The resource filename
         */
        @Size(min = OA_ANY_STRING_MIN, max = OA_ANY_STRING_MAX)
        String filename;
    }
}
