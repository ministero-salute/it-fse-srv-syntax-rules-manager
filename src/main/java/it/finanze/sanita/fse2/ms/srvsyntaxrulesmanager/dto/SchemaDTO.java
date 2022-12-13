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
        return items.stream().filter(SchemaDocumentDTO::getRootSchema).findFirst().orElseThrow(
            () -> new IllegalStateException(
                String.format(ERR_DTO_NO_ROOT_FOUND, typeIdExtension)
            )
        ).getNameSchema();
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
        return items.stream().anyMatch(SchemaDocumentDTO::isDeleted);
    }

    private static List<SchemaDocumentDTO> applyOptions(List<SchemaDocumentDTO> items, SchemaDocumentDTO.Options options) {
        return items.stream().map(d -> d.applyOptions(options)).collect(Collectors.toList());
    }

}
