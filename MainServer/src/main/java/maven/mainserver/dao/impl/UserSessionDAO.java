/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maven.mainserver.dao.impl;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.QueryBuilder;
import java.util.ArrayList;
import java.util.List;
import maven.mainserver.dao.DBLoader;
import maven.mainserver.server.session.Session;
import maven.multiutil.constant.ErrorCode;
import maven.multiutil.constant.UserdbKey;
import maven.multiutil.exception.ApplicationException;
import maven.multiutil.util.Util;

/**
 *
 * @author huongnt
 */
public class UserSessionDAO {

    private static DBCollection coll;

    static {
        try {
            coll = DBLoader.getUserDB().getCollection(UserdbKey.USER_SESSION_COLLECTION);
        } catch (Exception ex) {
            Util.addErrorLog(ex);
        }
    }

    public static boolean add(Session session) throws ApplicationException {
        boolean result = false;
        try {
            BasicDBObject insert = new BasicDBObject();
            insert.append(UserdbKey.USER_SESSION.USER_ID, session.userID);                          //NTQ#37166
            insert.append(UserdbKey.USER_SESSION.TOKEN, session.token);
            coll.insert(insert);
        } catch (Exception ex) {
            Util.addErrorLog(ex);
            throw new ApplicationException(ErrorCode.UNKNOWN_ERROR);
        }
        return result;
    }

    public static boolean remove(Session session) {
        boolean result = false;
        try {
            BasicDBObject remove = new BasicDBObject();
            remove.append(UserdbKey.USER_SESSION.TOKEN, session.token);
            coll.remove(remove);
            result = true;
        } catch (Exception ex) {
            Util.addErrorLog(ex);
        }
        return result;
    }

    public static boolean removeByTokens(List<String> tokens) {
        boolean result = false;
        try {
            DBObject query = QueryBuilder.start(UserdbKey.USER_SESSION.TOKEN).in(tokens).get();
            coll.remove(query);
            result = true;
        } catch (Exception ex) {
            Util.addErrorLog(ex);
        }
        return result;
    }

    public static boolean remove(List<Session> list) throws ApplicationException {
        boolean result = false;
        try {
            for (Session session : list) {
                remove(session);
            }
        } catch (Exception ex) {
            Util.addErrorLog(ex);
            throw new ApplicationException(ErrorCode.UNKNOWN_ERROR);
        }
        return result;
    }

    public static List<Session> getAll() throws ApplicationException {
        List<Session> result = new ArrayList<>();
        try {
            DBCursor cursor = coll.find();
            while (cursor.hasNext()) {
                BasicDBObject obj = (BasicDBObject) cursor.next();
                String token = obj.getString(UserdbKey.USER_SESSION.TOKEN);
                String userId = obj.getString(UserdbKey.USER_SESSION.USER_ID);
                Session session = new Session(token, userId);                                                                //NTQ#37166
                result.add(session);
            }
            cursor.close();
        } catch (Exception ex) {
            Util.addErrorLog(ex);
            throw new ApplicationException(ErrorCode.UNKNOWN_ERROR);
        }
        return result;
    }

}
