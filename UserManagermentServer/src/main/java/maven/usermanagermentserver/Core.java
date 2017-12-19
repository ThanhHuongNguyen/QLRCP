/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maven.usermanagermentserver;


/**
 *
 * @author huongnt
 */
public class Core {

    private static final String CONFIG_FILE = "/opt/config/UMSConfig.properties";

    public static void main(String[] args) {
        CommonCore.init(CONFIG_FILE);
        ServerConfig.initConfig(CONFIG_FILE);
        long start = System.currentTimeMillis();
        DatabaseLoader.init();
        DataBaseCleaner.startCleaner();
        UnlockCleaner.startUnlockCleaner();
        SystemAccountCreator.addAdministratorAccount();
        SystemAccountManager.init();
        SupervisorUserManager.init();                                           //ITS#25210
        UserDataCleaner.startCleaner();
        LogPointContainer.run();
        EventManager.init();
        EmailElementManager.init();
        long end = System.currentTimeMillis();
        WriteTrackersToDBRunner.start(ApiLogDBKey.ApiLogColl.ums);
        Util.addInfoLog("Time to start ums: " + (end - start) / 1000 + " seconds");
        Util.addInfoLog("Start UMS Service");
        vn.com.ntqsolution.usermanagementserver.server.Server.run();
    }

}
