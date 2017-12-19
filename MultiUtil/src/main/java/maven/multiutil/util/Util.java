/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maven.multiutil.util;

import maven.multiutil.logger.DailyLogger;

/**
 *
 * @author huongnt
 */
public class Util {
    public static void addDebugLog(String str) {
        DailyLogger.debug(str);
    }

    public static void addErrorLog(Exception ex) {
        DailyLogger.error(ex);
    }

    public static void addInfoLog(String str) {
        DailyLogger.info(str);
    }
}
