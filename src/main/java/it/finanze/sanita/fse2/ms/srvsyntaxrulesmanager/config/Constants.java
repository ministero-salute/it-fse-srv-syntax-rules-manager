package it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.config;

/**
 * Application constants
 *
 * @author G.Baittiner
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
        public static final String SCHEMA = "schema";

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
}