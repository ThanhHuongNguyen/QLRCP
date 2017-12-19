/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maven.mainserver.server;

import common.util.Util;
import org.eclipse.jetty.server.Server;

/**
 *
 * @author huongnt
 */
public abstract class AbstractServer implements Runnable {

    protected int port;

    protected String serverName;

    protected Server server;

    public AbstractServer(String serverName, int port) {
        this.serverName = serverName;
        this.port = port;
        server = new Server(port);                                            //NTQ#31117, ITS#25017
    }

    protected abstract void initServer();

    private void startServer() {
        try {
            server.start();
            server.join();
        } catch (Exception ex) {
            Util.addErrorLog(ex);
        }
    }

    private static final int INVALID_PORT = 0;

    @Override
    public void run() {
        if (port != INVALID_PORT) {
            Util.addInfoLog("Server " + serverName + " is started");
            initServer();
            startServer();
        } else {
            Util.addInfoLog("Server " + serverName + " is not started");
        }
    }

}
