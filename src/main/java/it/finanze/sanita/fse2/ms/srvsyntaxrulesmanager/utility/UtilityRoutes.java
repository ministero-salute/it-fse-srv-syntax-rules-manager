package it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.utility;

public final class UtilityRoutes {

    /**
     * Private constructor to disallow to access from other classes
     */
    private UtilityRoutes() {}

    public static final String API_VERSION = "v1";
    public static final String API_CHANGESET = "changeset";
    public static final String API_QP_LAST_UPDATE = "lastUpdate";
    public static final String API_STATUS = "status";
    public static final String API_SCHEMA = "schema";
    public static final String API_ID = "id";
    public static final String API_DOCUMENT_MAPPER = "/" + API_VERSION + "/" + API_SCHEMA;

    public static final String API_CHANGESET_STATUS = "/" + API_VERSION + "/" + API_CHANGESET + "/" + API_SCHEMA + "/" + API_STATUS ;
    public static final String API_PATH_EXTS_VAR = "extension";
    public static final String API_PATH_ID_VAR = "id";
    public static final String API_PATH_EXTS = "/{" + API_PATH_EXTS_VAR + "}";
    public static final String API_ID_EXTS = "/{" + API_PATH_ID_VAR + "}";
    public static final String API_GET_BY_EXTS = API_PATH_EXTS;
    public static final String API_DELETE_BY_EXTS = API_PATH_EXTS;
    public static final String API_GET_ONE_BY_ID = API_ID + API_ID_EXTS;
    public static final String API_GET_ALL = API_DOCUMENT_MAPPER;
    public static final String API_GET_ONE_BY_ID_FULL = API_DOCUMENT_MAPPER + "/" + API_GET_ONE_BY_ID;
    public static final String API_GET_BY_EXTS_FULL = API_DOCUMENT_MAPPER + API_GET_BY_EXTS;
    public static final String API_DELETE_BY_EXTS_FULL = API_DOCUMENT_MAPPER + API_DELETE_BY_EXTS;
    public static final String API_CHANGESET_TAG = "ChangeSet";
    public static final String API_DOCUMENTS_TAG = "Documents";

}
