package it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto;

import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.repository.entity.ExtensionETY;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.config.Constants.Logs.ERR_DTO_EMPTY_ITEMS;
import static it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.config.Constants.Logs.ERR_DTO_NO_ROOT_FOUND;
import static java.util.Comparator.comparing;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SchemaDTO {

    private String nameRoot;
    private String typeIdExtension;
    private OffsetDateTime insertionDate;
    private OffsetDateTime lastUpdateDate;
    private boolean deleted;
    private List<SchemaDocumentDTO> content;

    public static SchemaDTO fromExtension(ExtensionETY ext, SchemaDocumentDTO.Options options) {
        return fromItems(
            ext.getId(),
            ext.getItems().stream().map(SchemaDocumentDTO::fromEntity).collect(Collectors.toList()),
            options
        );
    }

    public static SchemaDTO fromItems(
        String typeIdExtension,
        List<SchemaDocumentDTO> items,
        SchemaDocumentDTO.Options options
    ) {
        SchemaDTO schema = new SchemaDTO();
        schema.setTypeIdExtension(typeIdExtension);
        schema.setNameRoot(extractRoot(typeIdExtension, items));
        schema.setInsertionDate(extractInsertionDate(typeIdExtension, items));
        schema.setLastUpdateDate(extractLastUpdateDate(typeIdExtension, items));
        schema.setDeleted(extractDeleted(typeIdExtension, items));
        schema.setContent(applyOptions(items, options));
        return schema;
    }

    private static String extractRoot(String typeIdExtension, List<SchemaDocumentDTO> items) {
        // Two cases
        // [1] Everything has been deleted
        // [2] Not everything has been deleted
        String name;
        boolean deleted = items.stream().allMatch(SchemaDocumentDTO::isDeleted);

        if(deleted) {
            // Just check for any root schema
            name = items.stream().filter(SchemaDocumentDTO::getRootSchema).findFirst().orElseThrow(
                () -> new IllegalStateException(String.format(ERR_DTO_NO_ROOT_FOUND, typeIdExtension))
            ).getNameSchema();
        } else {
            // Otherwise check for not-deleted root schema
            name = items.stream().filter(i -> !i.isDeleted() && i.getRootSchema()).findFirst().orElseThrow(
                () -> new IllegalStateException(String.format(ERR_DTO_NO_ROOT_FOUND, typeIdExtension))
            ).getNameSchema();
        }
        return name;
    }

    private static OffsetDateTime extractInsertionDate(String typeIdExtension, List<SchemaDocumentDTO> items) {
        // Emptiness check
        if(items.isEmpty()) throw new IllegalStateException(String.format(ERR_DTO_EMPTY_ITEMS, typeIdExtension));
        // Sort at run-time
        items.sort(comparing(SchemaDocumentDTO::getInsertionDate));
        // Extract
        return items.get(0).getInsertionDate();
    }

    private static OffsetDateTime extractLastUpdateDate(String typeIdExtension, List<SchemaDocumentDTO> items) {
        // Emptiness check
        if(items.isEmpty()) throw new IllegalStateException(String.format(ERR_DTO_EMPTY_ITEMS, typeIdExtension));
        // Sort at run-time
        items.sort(comparing(SchemaDocumentDTO::getLastUpdateDate).reversed());
        // Extract
        return items.get(0).getLastUpdateDate();
    }

    private static boolean extractDeleted(String typeIdExtension, List<SchemaDocumentDTO> items) {
        // Emptiness check
        if(items.isEmpty()) throw new IllegalStateException(String.format(ERR_DTO_EMPTY_ITEMS, typeIdExtension));
        // Extract
        return items.stream().allMatch(SchemaDocumentDTO::isDeleted);
    }

    private static List<SchemaDocumentDTO> applyOptions(List<SchemaDocumentDTO> items, SchemaDocumentDTO.Options options) {
        // Apply
        items.forEach(d -> d.applyOptions(options));
        // Return initial object
        return items;
    }

}
