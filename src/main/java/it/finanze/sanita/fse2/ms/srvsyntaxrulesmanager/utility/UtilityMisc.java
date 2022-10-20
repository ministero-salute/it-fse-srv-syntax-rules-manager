/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.utility;

import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.response.changes.ChangeSetDTO;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.repository.entity.SchemaETY;

import javax.validation.Path;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Base64;
import java.util.Date;

public final class UtilityMisc {

    /**
     * Private constructor to disallow to access from other classes
     */
    private UtilityMisc() {}

    public static OffsetDateTime convertToOffsetDateTime(Date dateToConvert) {
        return dateToConvert.toInstant().atOffset(ZoneOffset.UTC);
    }

    public static Date getYesterday() {
        return Date.from(LocalDateTime.now().minusDays(1).toInstant(ZoneOffset.UTC));
    }

    public static ChangeSetDTO toChangeset(SchemaETY entity) {
        return new ChangeSetDTO(entity.getId(), new ChangeSetDTO.Payload(entity.getTypeIdExtension(), entity.getNameSchema()));
    }

    public static String extractKeyFromPath(Path path) {
        String field = "";
        for(Path.Node node: path) field = node.getName();
        return field;
    }

    /**
     * Encode in Base64 the byte array passed as parameter.
     *
     * @param input	The byte array to encode.
     * @return		The encoded byte array to String.
     */
    public static String encodeBase64(final byte[] input) {
        return Base64.getEncoder().encodeToString(input);
    }
}
