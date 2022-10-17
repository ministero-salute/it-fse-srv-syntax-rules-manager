package it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.utility;

public final class UtilityOA {

    /**
     * Private constructor to disallow to access from other classes
     */
    private UtilityOA() {}

    /**
     * Min size extension string
     */
    public static final int OA_EXTS_STRING_MIN = 1;

    /**
     * Max size extension string
     */
    public static final int OA_EXTS_STRING_MAX = 25;

    /**
     * Max size given string ids
     */
    public static final int OA_IDS_SIZE_MAX = 255;

    /**
     * Min size given string
     */
    public static final int OA_ANY_STRING_MIN = 1;

    /**
     * Max size given string
     */
    public static final int OA_ANY_STRING_MAX = 255;

    /**
     * Min array size files
     */
    public static final int OA_ARRAY_FILES_MIN = 1;
    /**
     * Max array size files
     */
    public static final int OA_ARRAY_FILES_MAX = 25;

    /**
     * Min change set array files
     */
    public static final int OA_ARRAY_CHANGESET_MIN = 0;
    /**
     * Max change set array files
     */
    public static final int OA_ARRAY_CHANGESET_MAX = 100;

    /**
     * Max file size
     */
    public static final int OA_FILE_CONTENT_MAX = Integer.MAX_VALUE / 2;

}
