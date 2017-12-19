/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maven.mainserver.config;

import common.config.ConfigMethod;
import common.util.Util;

/**
 *
 * @author huongnt
 */
public class ServerConfig {

    private static final String UMS_SERVER_IP_CONFIG_KEY = "UMS_SERVER_IP";
    public static String UMS_SERVER_IP = "localhost";

    private static final String UMS_SERVER_PORT_CONFIG_KEY = "UMS_SERVER_PORT";
    public static int UMS_SERVER_PORT = 8090;

    private static final String STF_SERVER_IP_CONFIG_KEY = "STF_SERVER_IP";
    public static String STF_SERVER_IP = "localhost";

    private static final String STF_SERVER_PORT_CONFIG_KEY = "STF_SERVER_PORT";
    public static int STF_SERVER_PORT = 9117;
    private static final String SESSION_TIMEOUT_CONFIG_KEY = "SESSION_TIMEOUT"; // 20 minutes
    public static int SESSION_TIMEOUT = 20; // 20 minutes

    public static void initConfig(String configFile) {
        try {
            UMS_SERVER_IP = ConfigMethod.configStringProperty(configFile, UMS_SERVER_IP_CONFIG_KEY, "localhost");
            UMS_SERVER_PORT = ConfigMethod.configIntProperty(configFile, UMS_SERVER_PORT_CONFIG_KEY, "8090");

            STF_SERVER_IP = ConfigMethod.configStringProperty(configFile, STF_SERVER_IP_CONFIG_KEY, "localhost");
            STF_SERVER_PORT = ConfigMethod.configIntProperty(configFile, STF_SERVER_PORT_CONFIG_KEY, "9117");

            SESSION_TIMEOUT = ConfigMethod.configIntProperty(configFile, SESSION_TIMEOUT_CONFIG_KEY, "60");
        } catch (Exception ex) {
            Util.addErrorLog(ex);
        }
    }
}
