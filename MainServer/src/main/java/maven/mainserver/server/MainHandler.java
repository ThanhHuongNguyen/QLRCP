/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maven.mainserver.server;

import common.constant.API;
import common.constant.ErrorCode;
import common.constant.ParamKey;
import common.constant.ResponseMessage;
import common.util.Util;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import maven.mainserver.server.adapter.AdapterManager;
import maven.mainserver.server.adapter.IServiceAdapter;
import maven.mainserver.server.adapter.MinistryOfInternalAffairs;
import maven.mainserver.server.request.Request;
import maven.mainserver.server.session.Session;
import maven.mainserver.server.session.SessionManager;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.json.simple.JSONObject;

/**
 *
 * @author huongnt
 */
public class MainHandler extends AbstractHandler {

    private static final String UTF8Stamp = "text/plain;charset=UTF-8";
    private static final String USER_AGENT = "User-Agent";
    private static final String REDIRECT_URL = "redirect_url";

    /**
     * NOTICE: Session is initialized and added in login or authentication
     * process.
     *
     * @param string
     * @param rqst
     * @param hsr
     * @param response
     * @throws java.io.IOException
     * @throws javax.servlet.ServletException
     */
    @Override
    public void handle(String string, org.eclipse.jetty.server.Request rqst, HttpServletRequest hsr, HttpServletResponse response) throws IOException, ServletException {
        try {
            Date time = Util.getGMTTime();
            rqst.setHandled(true);
            Request request;
            response.addHeader("Access-Control-Allow-Origin", "*");
            response.addHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, PUT, DELETE, HEAD");
            response.addHeader("Access-Control-Allow-Headers", "X-PINGOTHER, Origin, X-Requested-With, Content-Type, Accept");
            //<editor-fold desc="Internal Servers Communication">
            if (string != null && string.substring(1).startsWith("checktoken")) {
                response.setContentType(UTF8Stamp);
                OutputStream out = response.getOutputStream();
                string = string.substring(1);
                String result = MinistryOfInternalAffairs.execute(string);
                out.write(result.getBytes());
                out.flush();
                out.close();
                return;
            }
            try {
                InputStreamReader isr = new InputStreamReader(rqst.getInputStream());
                BufferedReader reader = new BufferedReader(isr);
                String inputString = reader.readLine();
                isr.close();
                reader.close();
                if (inputString == null) {
                    Util.addDebugLog("Client request : " + inputString);
                    return;
                }
                request = Request.initRequest(inputString);
                if (request == null) {
                    Util.addDebugLog("Client request : " + inputString);
                    sendBadRequestNotification(response, ResponseMessage.BadResquestMessage);
                    return;
                }
                String userAgent = rqst.getHeader(USER_AGENT);
                if (userAgent != null) {
                    request.userAgent = userAgent.toLowerCase();
                }
                if (!request.contain(ParamKey.IP)) {
                    String ip = Util.getClientIpAddr(hsr);
                    request.put(ParamKey.IP, ip);
                }
            } catch (Exception ex) {
                Util.addErrorLog(ex);
                sendBadRequestNotification(response, ResponseMessage.BadResquestMessage);
                return;
            }

            Util.addDebugLog("Client request : " + request.toString());

            String api = request.api;
            if (api == null) {
                sendBadRequestNotification(response, ResponseMessage.BadResquestMessage);
                return;
            }

            String token = request.token;

            if (token == null || api.equals(API.LOG_OUT)) {
                if (api.equals(API.LOGIN) // Adding
                        || api.equals(API.LOGIN_VERSION_2)
                        || api.equals(API.LOG_OUT)
                        || api.equals(API.FORGOT_PASSWORD)
                        || api.equals(API.FORGOT_PASSWORD_VERSION_2)
                        || api.equals(API.REGISTER_BY_MOCOM)
                        || api.equals(API.LOGIN_BY_MOCOM)
                        || api.equals(API.REGISTER_BY_FAMU)
                        || api.equals(API.LOGIN_BY_FAMU)
                        || api.equals(API.REGISTER) // session
                        || api.equals(API.CHANGE_PASS_CASE_FORGOT) //here
                        || api.equals(API.CHANGE_PASS_CASE_FORGOT_VERSION_2)
                        || api.equals(API.LOGIN_ADMINISTRATOR)
                        || api.equals(API.INIT)
                        || api.equals(API.STATIC_PAGE)
                        || api.equals(API.LOGIN_TOOL)
                        || api.equals(API.CALL_LOG)
                        || api.equals(API.GET_BANNED_WORD)
                        || api.equals(API.GET_BANNED_WORD_VER_2)
                        || api.equals(API.CALL_PAYMENT)
                        || api.equals(API.PUSH_NOTIFICATION_FROM_BACK_END)
                        || api.equals(API.GET_INFOR_FOR_APPLICATION)
                        || api.equals(API.GET_CM_CODE_BY_USER_ID)
                        || api.equals(API.GET_USER_BY_REGISTER_TIME)
                        || api.equals(API.INSTALL_APPLICATION)
                        || api.equals(API.GET_USER_STATUS_BY_EMAIL)
                        || api.equals(API.CLICK_PUSH_NOTIFICATION)
                        || api.equals(API.MAKE_CALL)
                        || api.equals(API.START_CALL)
                        || api.equals(API.END_CALL)
                        || api.equals(API.UNREGISTER_NOTI_TOKEN)
                        || api.equals(API.GET_COMMUNICATION_POINT)) {
                    IServiceAdapter adapter = AdapterManager.getAdapter(api);
                    if (adapter != null) {
                        String result = adapter.callService(request);
                        dataBack(result, response);
                    } else {
                        sendBadRequestNotification(response, ResponseMessage.BadResquestMessage);
                    }
                    return;
                } else {
                    //DuongLTD
                    response.setContentType(UTF8Stamp);
                    OutputStream out = response.getOutputStream();
                    out.write(ResponseMessage.InvailidTokenMessage.getBytes());
                    out.flush();
                    out.close();
                    return;
                }
            }

            Session session = SessionManager.getSession(token);
            if (session == null) {
                Util.addDebugLog("DefaultHandler -> Session is invalid, token = " + token);

                //DuongLTD
                response.setContentType(UTF8Stamp);
                OutputStream out = response.getOutputStream();
                JSONObject obj = new JSONObject();
                obj.put(ParamKey.ERROR_CODE, ErrorCode.INVALID_TOKEN);
                out.write(obj.toJSONString().getBytes());
                out.flush();
                out.close();
                return;
            }
            session.resetExpire();
            request.put(ParamKey.USER_ID, session.userID);
            IServiceAdapter adapter = AdapterManager.getAdapter(api);
            if (adapter == null) {
                sendBadRequestNotification(response, ResponseMessage.BadResquestMessage);
                return;
            }
            String result = adapter.callService(request);
            dataBack(result, response);

        } catch (Exception ex) {
            Util.addErrorLog(ex);
            sendBadRequestNotification(response, ResponseMessage.UnknownError);
        }
    }

    private void sendBadRequestNotification(HttpServletResponse response, String message) {
        try {
            Util.addDebugLog("Respond: " + message);
            response.setContentType(UTF8Stamp);
            OutputStream out = response.getOutputStream();
            out.write(message.getBytes());
            out.flush();
            out.close();
        } catch (Exception ex) {
            Util.addErrorLog(ex);
        }
    }

    private void dataBack(String result, HttpServletResponse response) {
        try {
            response.setContentType(UTF8Stamp);
            OutputStream out = response.getOutputStream();
            if (result == null) {
                out.write(ResponseMessage.UnknownError.getBytes());
                out.flush();
                out.close();
                Util.addDebugLog(" Respond : null");
                return;
            }

            Util.addDebugLog("Respond : " + result);
            out.write(result.getBytes());
            out.flush();
            out.close();
        } catch (Exception ex) {
            Util.addErrorLog(ex);
        }
    }
}
