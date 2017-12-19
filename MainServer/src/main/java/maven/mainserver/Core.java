/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maven.mainserver;

import common.CommonCore;
import common.apitracker.WriteTrackersToDBRunner;
import common.config.CommonConfig;
import common.constant.Constant;
import common.constant.mongokey.ApiLogDBKey;
import common.util.Util;
import maven.mainserver.config.ServerConfig;
import org.eclipse.jetty.server.SessionManager;

/**
 *
 * @author huongnt
 */
public class Core {
    public static final String CONFIG_FILE = "/opt/config/MainConfig.properties";

    public static void main(String[] args) {
        ServerConfig.initConfig(CONFIG_FILE);
        CommonCore.init(CONFIG_FILE);
        Util.addInfoLog("Start Application Service....");
        Thread mainServer = new Thread(new MainServer("MAIN SERVER", CommonConfig.SERVICE_PORT));
        mainServer.start();
        Util.addInfoLog("Start main Service in maintain mode");
        DBLoader.init();
        SessionManager.init();
        GabageCollector.startCleaningService();
        StatisticThread.startService();
        CmCodeTracker.startService();
        BlackListManager.init();
        ActiveCallTracker.startService();
        ApplicationConfigManager.init();
        Util.addInfoLog("Checking services status...");
        System.out.println("Checking services status...");
        checkServicesHealth();
        Util.addInfoLog("All services are working.");
        System.out.println("All services are working.");
        SettingManager.changeToUserMode();
        WriteTrackersToDBRunner.start(ApiLogDBKey.ApiLogColl.main);

        Util.addInfoLog("Start main Service in user mode");
    }

    private static void checkServicesHealth() {
        while (true) {
            try {
                ServicesHealthChecker.checkServicesHealth();
                if (ServicesHealthChecker.areAllServicesLived()) {
                    break;
                }
                Thread.sleep(Constant.A_MINUTE);
            } catch (Exception ex) {
                Util.addErrorLog(ex);
            }
        }
    }
}
