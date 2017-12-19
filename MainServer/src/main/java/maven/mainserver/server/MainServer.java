/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maven.mainserver.server;

import common.config.CommonConfig;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.thread.ExecutorThreadPool;

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
        LinkedBlockingQueue<Runnable> queue = new LinkedBlockingQueue<>(60 * CommonConfig.JETTY_MAX_REQUEST);
        ExecutorThreadPool pool = new ExecutorThreadPool(10, CommonConfig.JETTY_MAX_THREAD, 300000, TimeUnit.MILLISECONDS, queue);
        server.setThreadPool(pool);                                                                                                     //ITS#25017
        MainHandler handler = new MainHandler();
        server.setHandler(handler);
    }
}
