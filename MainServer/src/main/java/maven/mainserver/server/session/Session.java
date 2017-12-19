/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maven.mainserver.server.session;

import common.util.Util;
import java.util.UUID;
import maven.mainserver.config.ServerConfig;

/**
 *
 * @author huongnt
 */
public class Session {

    private static final long SESSION_TIME_OUT_MILLISECONDS = (long) ServerConfig.SESSION_TIMEOUT * 60 * 1000;

    public String token;
    public String userID;
    public int timeToLive;
    public long sessionExpire = 0;

    public Session(String userID) {
        this.token = UUID.randomUUID().toString();
        this.userID = userID;

        this.timeToLive = 0;
        this.sessionExpire = Util.currentTime() + SESSION_TIME_OUT_MILLISECONDS;
    }

    public Session(String token, String userID) {
        this.token = token;
        this.userID = userID;

        this.timeToLive = 0;
    }

    public void resetExpire() {
        this.sessionExpire = Util.currentTime() + SESSION_TIME_OUT_MILLISECONDS;
    }
}
