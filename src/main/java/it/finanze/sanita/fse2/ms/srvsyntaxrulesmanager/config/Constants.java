/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 * 
 * Copyright (C) 2023 Ministero della Salute
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.config;

/**
 * Application constants
 *
 */
public final class Constants {

    /**
     * Private constructor to disallow to access from other classes
     */
    private Constants() {}

    /**
     * Collections
     */
    public static final class Collections {

        /**
         * Schema collection name
         */
        public static final String SCHEMA = "schema_eds";

        /**
         * Private constructor to disallow to access from other classes
         */
        private Collections() {}
    }
    /**
     * Path scan.
     */
    public static final class ComponentScan {

        /**
         * Configuration mongo path.
         */
        public static final String CONFIG_MONGO = "it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.config.mongo";

        /**
         * Repository components path.
         */
        public static final String REPOSITORY = "it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.repository";

        /**
         * Utils components path.
         */
        public static final String UTILS = "it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.utils";

        /**
         * Private constructor to disallow to access from other classes
         */
        private ComponentScan() {}

    }

    /**
     * Available profiles on the microservice
     */
    public static final class Profile {

        /**
         * Runner profile name.
         * Used to load test files on the database at start-up.
         */
        public static final String RUNNER = "runner";

        /**
         * Test profile name
         */
        public static final String TEST = "test";

        /**
         * Test profile name underscored
         */
        public static final String TEST_PREFIX = "test_";

        /**
         * Private constructor to disallow to access from other classes
         */
        private Profile() {}
    }

    /**
     * Logs messages
     */
    public static final class Logs {
        /**
         * Private constructor to disallow to access from other classes
         */
        private Logs() {}

        // Validation
        // IChangesetCTL
        public static final String ERR_VAL_EXT_BLANK = "L'estensione non può essere vuota";
        public static final String ERR_VAL_ROOT_BLANK = "Il nome del 'root' file non può essere vuoto";
        public static final String ERR_VAL_ID_BLANK = "L'identificatore documento non può essere vuoto";
        public static final String ERR_VAL_ID_NOT_VALID = "L'identificatore documento non è valido";
        public static final String ERR_VAL_FUTURE_DATE = "La data del ultimo aggiornamento non può essere nel futuro";
        public static final String ERR_VAL_FILES_DUPLICATED = "Uno o più file duplicati";
        public static final String ERR_VAL_MISSING_PART = "Il campo richiesto '%s' non è presente";
        public static final String ERR_VAL_UNABLE_CONVERT = "Impossibile convertire %s nel tipo %s";
        public static final String ERR_VAL_MISSING_PARAMETER = "Il parametro richiesto '%s' non è presente";
        public static final String ERR_VAL_INVALID_SCHEMA = "Lo schema fornito non risulta valido: %s";
        public static final String ERR_VAL_INVALID_SCHEMA_IO = "Impossibile elaborare il contenuto dello schema";

        // Services
        // DocumentSRV
        public static final String ERR_SRV_ROOT_NOT_FOUND = "Il nome del 'root' file %s non corrisponde a nessuno dei valori possibili: %s";
        public static final String ERR_SRV_INVALID_ROOT_EXT = "L'estensione del 'root' file %s non è consentita";
        public static final String ERR_SRV_DOC_NOT_FOUND = "Il documento richiesto non esiste";
        public static final String ERR_SRV_EXT_NOT_FOUND = "L'estensione richiesta non esiste";
        public static final String ERR_SRV_EXT_ALREADY_ESISTS = "L'estensione richiesta esiste già";
        public static final String ERR_SRV_ROOT_DOC_NOT_FOUND = "Impossibile recuperare il documento root dello schema";
        // Repository
        // DocumentRepo
        public static final String ERR_REP_DOCS_NOT_FOUND = "Impossibile recuperare i documenti dell'estensione richiesta";
        public static final String ERR_REP_IS_EXT_INSERTED = "Impossibile verificare se l'estensione sia già stata inserita";
        public static final String ERR_REP_IS_DOCS_INSERTED = "Impossibile recuperare i file dell'estensione richiesta";
        public static final String ERR_REP_INS_DOCS_BY_EXT = "Impossibile inserire i documenti dell'estensione richiesta";
        public static final String ERR_REP_UPD_DOCS_BY_EXT = "Impossibile aggiornare i documenti dell'estensione richiesta";
        public static final String ERR_REP_UPD_MISMATCH = "Il numero delle modifiche eseguite <%d> non coincide con quelle richieste <%d>";
        public static final String ERR_REP_DEL_DOCS_BY_EXT = "Impossibile cancellare i documenti dell'estensione richiesta";
        public static final String ERR_REP_DEL_MISMATCH = "Il numero delle cancellazioni eseguite <%d> non coincide con quelle richieste <%d>";
        // Repository
        // ChangeSetRepo
        public static final String ERR_REP_CHANGESET_INSERT = "Impossibile recuperare il change-set degli inserimenti";
        public static final String ERR_REP_CHANGESET_DELETE = "Impossibile recuperare il change-set delle cancellazioni";
        public static final String ERR_REP_EVERY_ACTIVE_DOC = "Impossibile recuperare ogni estensione attiva con i relativi documenti";
        public static final String ERR_REP_COUNT_ACTIVE_DOC = "Impossibile conteggiare ogni estensione attiva";
        // Entities
        // Schema
        public static final String ERR_ETY_BINARY_CONVERSION = "Impossibile convertire i dati binari nel formato richiesto (UTF-8)";
        public static final String ERR_INVALID_CONTENT = "One or more than one file appears not to be a valid schema for the extension: %s";
        public static final String ERR_FIND_ACTIVE_DOCS = "Impossibile trovare documenti attivi";
        // DTO
        // Schema
        public static final String ERR_DTO_EMPTY_ITEMS = "La lista items per lo schema %s è vuota";
        public static final String ERR_DTO_NO_ROOT_FOUND = "Impossibile recuperare root file per estensione %s";
    }
}