/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maven.mainserver.server.session;

import common.util.Util;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import maven.mainserver.dao.impl.UserSessionDAO;

/**
 *
 * @author huongnt
 */
public class SessionManager {

    //public static Map<String, Session> SS = new HashMap<String, Session>( Config.MaxConcurrent );
    public static Map<String, Session> SS = new ConcurrentHashMap<>();

    public static void init() {
        try {
            List<Session> listSession = UserSessionDAO.getAll();
            for (Session session : listSession) {
                putSession(session);
            }
            List<String> list = new ArrayList<>();
            for (Session session : listSession) {
                list.add(session.userID);
            }
        } catch (Exception ex) {
            Util.addErrorLog(ex);
        }
    }

    public static boolean isTokenExist(String token) {
        return SS.containsKey(token);
    }

    public static Session getSession(String token) {
        Session session = SS.get(token);
        return session;
    }

    public static List<Session> removeSessionsOfUserExcudeToken(String token) {
        //DuongLTD
        List<Session> result = new ArrayList<>();
        try {
            Session session = SS.get(token);
            String sessionEmail = session.userID;
            Iterator<Session> iter = SS.values().iterator();
            while (iter.hasNext()) {
                Session curSession = iter.next();
                if (token != null) {
                    if (curSession.userID.equals(sessionEmail) && !curSession.token.equals(token)) {
                        result.add(curSession);
                        SS.remove(curSession.token);
                    }
                }
            }
        } catch (Exception ex) {
            Util.addErrorLog(ex);

        }
        return result;
    }

    public static List<Session> removeSessionsByUserId(String userId) {
        //DuongLTD
        List<Session> result = new ArrayList<>();
        try {
            Iterator<Session> iter = SS.values().iterator();
            while (iter.hasNext()) {
                Session curSession = iter.next();
                if (curSession.userID.equals(userId)) {
                    result.add(curSession);
                    SS.remove(curSession.token);
                }
            }
        } catch (Exception ex) {
            Util.addErrorLog(ex);

        }
        return result;
    }

    public static List<Session> getSessionsByUserId(String userId) {
        //DuongLTD
        List<Session> result = new ArrayList<>();
        if (userId != null && !userId.isEmpty()) {
            try {
                Iterator<Session> iter = SS.values().iterator();
                while (iter.hasNext()) {
                    Session curSession = iter.next();
                    if (curSession.userID.equals(userId)) {
                        result.add(curSession);
                    }
                }
            } catch (Exception ex) {
                Util.addErrorLog(ex);

            }
        }
        return result;
    }

    public static List<Session> clearSessionOfUser(String token) {
        //DuongLTD
        List<Session> result = new ArrayList<>();
        try {
            Session session = SS.get(token);
            String sessionEmail = session.userID;
            Iterator<Session> iter = SS.values().iterator();
            while (iter.hasNext()) {
                Session curSession = iter.next();
                if (token != null) {
                    if (curSession.userID.equals(sessionEmail)) {
                        result.add(curSession);
                        SS.remove(curSession.token);
                    }
                }
            }
        } catch (Exception ex) {
            Util.addErrorLog(ex);

        }
        return result;
    }

    public static List<Session> removeSessionsOfGroupUser(String type) {
        //DuongLTD
        List<Session> result = new ArrayList<>();
        try {
            Iterator<Session> iter = SS.values().iterator();
            while (iter.hasNext()) {
                Session curSession = iter.next();
            }
        } catch (Exception ex) {
            Util.addErrorLog(ex);

        }
        return result;
    }

    public static void putSession(Session session) {
        SS.put(session.token, session);
    }

    public static void removeSession(String token) {
        SS.remove(token);
    }

    public static void clear() {
        SS.clear();
    }

    public static Collection<Session> getAllSession() {
        return SS.values();
    }

}
