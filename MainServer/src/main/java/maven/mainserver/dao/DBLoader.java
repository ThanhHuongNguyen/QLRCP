/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maven.mainserver.dao;

import com.mongodb.DB;
import common.constant.mongokey.UserdbKey;
import common.dao.CommonDAO;

/**
 *
 * @author huongnt
 */
public class DBLoader {

    private static final DB userDB;

    static {
        userDB = CommonDAO.mongo.getDB(UserdbKey.DB_NAME);
    }

    public static DB getUserDB() {
        return userDB;
    }
}
