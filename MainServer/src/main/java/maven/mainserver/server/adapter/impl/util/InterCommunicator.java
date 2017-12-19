/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maven.mainserver.server.adapter.impl.util;

import common.util.HttpRequestUtils;

/**
 *
 * @author huongnt
 */
public class InterCommunicator {

    public static String sendRequest(String requestString, String serverIP, int serverPort) {
        return HttpRequestUtils.sendRequest(requestString, serverIP, serverPort);

    }
}
