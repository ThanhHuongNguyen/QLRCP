/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maven.mainserver;

import common.CommonCore;
import common.config.CommonConfig;
import common.util.Util;
import maven.mainserver.config.ServerConfig;
import maven.mainserver.server.MainServer;
import maven.mainserver.server.session.SessionManager;

/**
 *
 * @author huongnt
 */
public class Core {
    public static final String CONFIG_FILE = "/opt/application/config/MainConfig.properties";

    public static void main(String[] args) {
        ServerConfig.initConfig(CONFIG_FILE);
        CommonCore.init(CONFIG_FILE);
        Util.addInfoLog("Start Application Service....");
        Thread mainServer = new Thread(new MainServer("MAIN SERVER", CommonConfig.SERVICE_PORT));
        mainServer.start();
        Util.addInfoLog("Start main Service in maintain mode");
        SessionManager.init();
        Util.addInfoLog("Start main Service in user mode");
    }
}
