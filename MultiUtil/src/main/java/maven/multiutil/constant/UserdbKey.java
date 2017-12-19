/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maven.multiutil.constant;

/**
 *
 * @author huongnt
 */
public class UserdbKey {

    public static final String DB_NAME = "userdb";

    public static final String USER_SESSION_COLLECTION = "user_session";

    public class USER_SESSION {

        public static final String ID = "_id";
        public static final String USER_ID = "user_id";
        public static final String TOKEN = "token";
        public static final String FINISH_REGISTER_USER = "finish_register_user";                   //NTQ#37166
        public static final String REGISTER_APPLICATION_ID = "register_application_id";
        public static final String USING_APPLICATION = "using_application";
    }

}
