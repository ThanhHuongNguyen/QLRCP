/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maven.multiutil.logger;

import java.util.Date;
import maven.multiutil.config.CommonConfig;
import maven.multiutil.util.DateFormat;
import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

/**
 *
 * @author huongnt
 */
public class DailyLogger {

    private static final Logger logger = Logger.getRootLogger();

    static {
        // creates pattern layout
        PatternLayout layout = new PatternLayout();
        String conversionPattern = CommonConfig.LOG_PATTERN;
        layout.setConversionPattern(conversionPattern);

        // creates daily rolling file appender
        DailyRollingFileAppender rollingAppender = new DailyRollingFileAppender();
        rollingAppender.setFile(CommonConfig.LOG_FILE);
        rollingAppender.setDatePattern("'.'yyyy-MM-dd");
        rollingAppender.setLayout(layout);
        rollingAppender.activateOptions();

        // configures the root logger
        Logger rootLogger = Logger.getRootLogger();
        rootLogger.setLevel(Level.toLevel(CommonConfig.LOG_LEVEL));
        rootLogger.addAppender(rollingAppender);
    }

    private static String completeMessage(String str) {
        StringBuilder sb = new StringBuilder("At ");
        sb.append(DateFormat.format_yyyyMMddHHmmssSSS(new Date()));
        sb.append(" : ");
        sb.append(str);
        return sb.toString();
    }

    public static void debug(String message) {
        String inforMessage = completeMessage(message);
        logger.debug(inforMessage);
    }

    public static void error(Throwable t) {
        logger.error("At " + DateFormat.format_yyyyMMddHHmmssSSS(new Date()), t);
    }

    public static void info(String message) {
        String inforMessage = completeMessage(message);
        logger.info(inforMessage);
    }
}
