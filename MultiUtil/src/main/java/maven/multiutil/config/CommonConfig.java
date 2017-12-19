/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maven.multiutil.config;

import maven.multiutil.util.Util;

/**
 *
 * @author huongnt
 */
public class CommonConfig {

    private static final String SERVICE_PORT_CONFIG_KEY = "SERVICE_PORT";
    public static int SERVICE_PORT = 0;

    private static final String DB_SERVER_CONFIG_KEY = "DB_SERVER";
    public static String DB_SERVER = "localhost";

    private static final String DB_PORT_CONFIG_KEY = "DB_PORT";
    public static int DB_PORT = 27017;

    private static final String DB_USER_CONFIG_KEY = "DB_USER";                 //ITS#23493
    public static String DB_USER = "Nexiv";                                     //ITS#23493

    private static final String DB_PASSWORD_CONFIG_KEY = "DB_PASSWORD";         //ITS#23493
    public static String DB_PASSWORD = "100manUser";                            //ITS#23493

    private static final String DB_AUTHEN_DB_CONFIG_KEY = "DB_AUTHEN_DB";       //ITS#23493
    public static String DB_AUTHEN_DB = "admin";                                //ITS#23493

    private static final String LOG_FILE_CONFIG_KEY = "LOG_FILE";
    public static String LOG_FILE = "logger.log";

    private static final String LOG_PATTERN_CONFIG_KEY = "LOG_PATTERN";
    public static String LOG_PATTERN = "[%p] %m%n";

    private static final String LOG_LEVEL_CONFIG_KEY = "LOG_LEVEL";
    public static String LOG_LEVEL = "ERROR";

    private static final String DB_CONNECTION_PER_HOST_CONFIG_KEY = "DB_CONNECTION_PER_HOST";
    public static int DB_CONNECTION_PER_HOST = 1500;

    private static final String LOG_API_KEY = "LOG_API";
    public static boolean LOG_API = false;

    private static final String LOG_API_TIME_KEY = "LOG_API_TIME";
    public static String LOG_API_TIME = "2100_0259";

    private static final String REDIS_SERVER_CONFIG_KEY = "REDIS_SERVER";
    public static String REDIS_SERVER = "localhost";

    private static final String REDIS_PORT_CONFIG_KEY = "REDIS_PORT";
    public static int REDIS_PORT = 6379;

    private static final String REDIS_PASSWORD_CONFIG_KEY = "REDIS_PASSWORD";
    public static String REDIS_PASSWORD = "123456123";

    public static void initConfig(String configFile) {
        try {

            SERVICE_PORT = ConfigMethod.configIntProperty(configFile, SERVICE_PORT_CONFIG_KEY, "0");

            DB_SERVER = ConfigMethod.configStringProperty(configFile, DB_SERVER_CONFIG_KEY, "localhost");

            DB_PORT = ConfigMethod.configIntProperty(configFile, DB_PORT_CONFIG_KEY, "27017");

            DB_USER = ConfigMethod.configStringProperty(configFile, DB_USER_CONFIG_KEY, "dbadmin");                             //ITS#23493

            DB_PASSWORD = ConfigMethod.configStringProperty(configFile, DB_PASSWORD_CONFIG_KEY, "adminntq");                    //ITS#23493

            DB_AUTHEN_DB = ConfigMethod.configStringProperty(configFile, DB_AUTHEN_DB_CONFIG_KEY, "admin");                     //ITS#23493

            LOG_LEVEL = ConfigMethod.configStringProperty(configFile, LOG_LEVEL_CONFIG_KEY, "ERROR");

            LOG_FILE = ConfigMethod.configStringProperty(configFile, LOG_FILE_CONFIG_KEY, "logger.log");

            LOG_PATTERN = ConfigMethod.configStringProperty(configFile, LOG_PATTERN_CONFIG_KEY, "[%p] %m%n");

            DB_CONNECTION_PER_HOST = ConfigMethod.configIntProperty(configFile, DB_CONNECTION_PER_HOST_CONFIG_KEY, "1500");

            LOG_API = ConfigMethod.configBooleanProperty(configFile, LOG_API_KEY);

            LOG_API_TIME = ConfigMethod.configStringProperty(configFile, LOG_API_TIME_KEY, "0000_2359");

            REDIS_SERVER = ConfigMethod.configStringProperty(configFile, REDIS_SERVER_CONFIG_KEY, "localhost");

            REDIS_PORT = ConfigMethod.configIntProperty(configFile, REDIS_PORT_CONFIG_KEY, "6379");

            REDIS_PASSWORD = ConfigMethod.configStringProperty(configFile, REDIS_PASSWORD_CONFIG_KEY, "123456123");
        } catch (Exception ex) {
            Util.addErrorLog(ex);

        }
    }
}
