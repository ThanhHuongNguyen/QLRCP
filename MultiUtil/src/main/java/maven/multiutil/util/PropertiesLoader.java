/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maven.multiutil.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 *
 * @author huongnt
 */
public class PropertiesLoader {

    private PropertiesLoader() {
    }

    public static Properties load(String propertyFileName) {
        Properties properties = new Properties();
        try {
            FileInputStream fis = new FileInputStream(propertyFileName);
            properties.load(fis);
        } catch (IOException ex) {
            Util.addErrorLog(ex);
        }
        return properties;
    }
}
