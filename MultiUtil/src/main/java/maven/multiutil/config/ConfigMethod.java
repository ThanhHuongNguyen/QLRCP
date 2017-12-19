/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maven.multiutil.config;

import java.util.Properties;
import maven.multiutil.util.PropertiesLoader;
import maven.multiutil.util.Util;

/**
 *
 * @author huongnt
 */
public class ConfigMethod {

    public static int configIntProperty(String fileName, String propertyName, String defaulValue) {
        Properties serverProperties = PropertiesLoader.load(fileName);
        String property = serverProperties.getProperty(propertyName, defaulValue);
        int result = 0;
        try {
            result = Integer.parseInt(property.trim());
        } catch (NumberFormatException ex) {
            ex.printStackTrace();
            Util.addErrorLog(ex);
        }
        return result;
    }

    public static double configDoubleProperty(String fileName, String propertyName, String defaulValue) {
        Properties serverProperties = PropertiesLoader.load(fileName);
        String property = serverProperties.getProperty(propertyName, defaulValue);
        double result = 0;
        try {
            result = Double.parseDouble(property.trim());
        } catch (NumberFormatException ex) {
            ex.printStackTrace();
            Util.addErrorLog(ex);
        }
        return result;
    }

    public static String configStringProperty(String fileName, String propertyName, String defaulValue) {
        Properties serverProperties = PropertiesLoader.load(fileName);
        return serverProperties.getProperty(propertyName, defaulValue).trim();
    }

    public static Boolean configBooleanProperty(String fileName, String propertyName) {
        Properties serverProperties = PropertiesLoader.load(fileName);
        String property = serverProperties.getProperty(propertyName);
        return property != null && property.trim().equalsIgnoreCase("true");
    }

    public static Boolean configBooleanProperty(String fileName, String propertyName, boolean defaultValue) {   //ITS#24641
        Properties serverProperties = PropertiesLoader.load(fileName);                                              //ITS#24641
        String property = serverProperties.getProperty(propertyName);                                               //ITS#24641
        return property == null ? defaultValue : property.trim().equalsIgnoreCase("true");                           //ITS#24641
    }                                                                                                               //ITS#24641
}
