/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maven.mainserver.server.adapter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import maven.mainserver.server.session.Session;
import maven.mainserver.server.session.SessionManager;

/**
 *
 * @author huongnt
 */
public class MinistryOfInternalAffairs {

    private static final String CheckTokenString = "checktoken=";
    private static final Pattern CheckTokenPattern = Pattern.compile(CheckTokenString);

    private static final String API1_Result_True = "result=true&user_id=%s";
    private static final String API1_Result_False = "result=false";

    public static String execute(String requestStr) {
        Matcher matcher = CheckTokenPattern.matcher(requestStr);
        if (matcher.find()) {
            /**
             * API No 1: CheckToken.
             */
            int end = matcher.end();
            String token = requestStr.substring(end);
            Session session = SessionManager.getSession(token);
            if (session != null) {
                return String.format(API1_Result_True, session.userID);
            } else {
                return API1_Result_False;
            }

        }

        return API1_Result_False;
    }
}
