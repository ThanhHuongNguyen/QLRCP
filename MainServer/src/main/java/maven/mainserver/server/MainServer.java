/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maven.mainserver.server;

import org.eclipse.jetty.server.Server;

/**
 *
 * @author huongnt
 */
public class MainServer extends AbstractServer {

    public MainServer(String serverName, int port) {
        super(serverName, port);
    }

    @Override
    public void initServer() {
        server = new Server(port);                                                                                                    //ITS#25017
        MainHandler handler = new MainHandler();
        server.setHandler(handler);
    }
}
