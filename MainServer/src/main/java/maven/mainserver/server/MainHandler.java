/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maven.mainserver.server;

import static com.sun.xml.internal.ws.api.message.Packet.Status.Request;
import common.apitracker.ApiTracker;
import common.config.CommonConfig;
import common.constant.API;
import common.constant.Constant;
import common.constant.ErrorCode;
import common.constant.ParamKey;
import common.constant.ResponseMessage;
import common.entity.Tracker;
import common.token.JWTCreator;
import common.token.TokenElement;
import common.util.InterCommunicator;
import common.util.Util;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.mail.Session;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import maven.mainserver.Core;
import maven.mainserver.config.ServerConfig;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.SessionManager;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

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
            if (string != null && string.substring(1).startsWith("tracker_id")) {
                Map<String, String> adjustMap = convertStringToMap(string, "/");
                Util.addInfoLog(" tracker: " + string);
                String trackerId = adjustMap.get(Constant.TRACKER.TRACKER_ID);
                String deviceType = adjustMap.get(Constant.TRACKER.DEVICE_TYPE);
                int type = Constant.DEVICE_TYPE.IOS;
                if (deviceType.equals(Constant.ANDROID)) {
                    type = Constant.DEVICE_TYPE.ANDROID;
                }
                String deviceId = adjustMap.get(Constant.TRACKER.DEVICE_ID);
                String label = adjustMap.get(Constant.TRACKER.LABEL);
                String application = null;
                if (isFromCmCode(adjustMap)) {
                    int flag = Constant.FLAG.OFF;
                    application = !Util.validateString(application) ? Constant.DEFAULT_APPLICATION : application;
                    if (Util.validateString(application, label, deviceId)) {
                        AdjustCMCodeDAO.installTracker(trackerId, application, deviceType, deviceId, time, flag, label);
                        UserManagermentServiceAdapter.updateInstallCmCode(deviceId, label, type, application);
                    }
                } else if (isFromWelcomeBack(adjustMap)) {
                    String packageName = adjustMap.get(Constant.TRACKER.PACKAGE_NAME);
                    application = ApplicationConfigManager.getAppByPackageAndDeviceType(type, packageName);
                    Util.addDebugLog("packageName: " + packageName);
                    Util.addDebugLog("application: " + application);

                    if (application != null) {                                                                                                          //ITS#22497
                        if (Util.validateString(deviceId)) {                                                                                            //ITS#22497
                            JSONObject requestUMS = new JSONObject();                                                                                         //ITS#22497
                            requestUMS.put(ParamKey.API_NAME, API.WELCOME_BACK);                                                                        //ITS#22497
                            requestUMS.put(ParamKey.DEVICE_ID, deviceId);                                                                               //ITS#22497
                            requestUMS.put(ParamKey.APPLICATION, application);                                                                          //ITS#22497
                            String ums = InterCommunicator.sendRequest(requestUMS.toString(), ServerConfig.UMS_SERVER_IP, ServerConfig.UMS_SERVER_PORT);//ITS#22497
                            try {                                                                                                                       //ITS#22497
                                JSONParser parser = new JSONParser();
                                JSONObject umsJson = (JSONObject) parser.parse(ums);                                                                //ITS#22497
                                Long code = (Long) umsJson.get(ParamKey.ERROR_CODE);                                                                    //ITS#22497
                                if (code == ErrorCode.SUCCESS) {                                                                                        //ITS#22497
                                    JSONObject dataObj = (JSONObject) umsJson.get(ParamKey.DATA);                                                       //ITS#22497
                                    if (dataObj != null) {                                                                                              //ITS#22497
                                        String userId = (String) dataObj.get(ParamKey.USER_ID);                                                         //ITS#22497
                                        if (userId != null) {                                                                                           //ITS#22497
                                            SessionManager.removeSessionsByUserId(userId);                                                              //ITS#22497
                                        }                                                                                                               //ITS#22497
                                    }                                                                                                                   //ITS#22497
                                }                                                                                                                       //ITS#22497
                            } catch (ParseException ex) {                                                                                               //ITS#22497
                                Util.addErrorLog(ex);                                                                                                   //ITS#22497
                            }                                                                                                                           //ITS#22497
                        }
                    }
                }
            } else if (string != null && string.substring(1).startsWith(ParamKey.CM_CODE)) {
                try {
                    String host = rqst.getHeader("Host");
                    String userAgent = rqst.getHeader(USER_AGENT);
                    if (host != null && host.contains(ServerConfig.OLD_EAZY_DOMAIN)) {
                        String url = "http://" + ServerConfig.NEW_EAZY_DOMAIN + ":" + CommonConfig.SERVICE_PORT + string;
                        response.sendRedirect(url);
                        return;
                    }
                    String redirectUrl = CMCodeProcessor.execute(string.substring(1), host, userAgent, rqst);
                    if (Util.validateString(redirectUrl)) {
                        response.sendRedirect(redirectUrl);
                    }
                } catch (Exception ex) {
                    Util.addErrorLog(ex);
                }
                return;
            } else if (string != null && string.substring(1).startsWith(ParamKey.ID)) {
                try {
                    Util.addInfoLog("id: " + string);
                    String userAgent = rqst.getHeader(USER_AGENT);
                    Map<String, String> userCodeMap = convertStringToMap(string, "&");
                    String application = userCodeMap.get(Constant.ID.APPLICATION);
                    Util.addInfoLog("id application: " + application);
                    int applicationType = Constant.APPLICATION_TYPE.IOS_PRODUCTION_APPLICATION;
                    if (userCodeMap.size() > 1) {
                        try {
                            String applicationTypeString = userCodeMap.get(Constant.ID.APPLICATION_TYPE);
                            applicationType = Integer.parseInt(applicationTypeString);
                        } catch (NumberFormatException ex) {
                            Util.addErrorLog(ex);
                        }
                    }
                    String redirectUrl = "";
                    if (application != null && !application.isEmpty()) {
                        ApplicationConfig config = ApplicationConfigManager.get(application);
                        if (config != null) {
                            Util.addInfoLog("id config not null: " + application);
                            if (userAgent.contains(Constant.DEVICE_NAME.MAC_DEVICE)) {
                                if (applicationType == Constant.APPLICATION_TYPE.IOS_PRODUCTION_APPLICATION) {

                                    String iosDeepLink = config.iOSProductionDeepLink;
                                    redirectUrl = iosDeepLink;
//                                    response.sendRedirect(iosDeepLink);
                                    Util.addInfoLog("id application deep link: " + iosDeepLink);
                                } else {
                                    String iosDeepLink = config.iOSEnterpriseDeepLink;
                                    redirectUrl = iosDeepLink;
//                                    response.sendRedirect(iosDeepLink);
                                }
                            } else if (userAgent.contains(Constant.DEVICE_NAME.ANDROID_DEVICE)) {
                                String androidDeepLink = config.androidDeepLink;
                                redirectUrl = androidDeepLink;
//                                response.sendRedirect(androidDeepLink);
                            }
                        }
                    }
                    if (Util.validateString(redirectUrl)) {
                        response.sendRedirect(redirectUrl);
                    }
                } catch (Exception ex) {
                    Util.addErrorLog(ex);
                }
                return;
            } else if (string != null && string.substring(1).startsWith(ParamKey.UNIQUE_NUMBER)) {
                Util.addInfoLog("uniqueElement : " + string);
                try {
//                    String cmCode = null;
                    String userAgent = rqst.getHeader(USER_AGENT);
                    Map<String, String> uniqueMap = convertStringToMap(string, "&");

                    String application;
                    if (uniqueMap.size() >= 3) {
                        application = uniqueMap.get(Constant.UNIQUE_NUMBER.APPLICATION);
                        application = application == null || application.isEmpty() ? common.constant.Constant.DEFAULT_APPLICATION : application;
                    } else {
                        application = common.constant.Constant.DEFAULT_APPLICATION;
                    }
                    int applicationType = Constant.APPLICATION_TYPE.IOS_PRODUCTION_APPLICATION;

                    if (uniqueMap.size() > 1) {
                        try {
                            String applicationTypeString = uniqueMap.get(Constant.UNIQUE_NUMBER.APPLICATION_TYPE);
                            applicationType = Integer.parseInt(applicationTypeString);

                        } catch (NumberFormatException ex) {
                            Util.addInfoLog("VAOD LOI : " + string);
                        }
                    }
                    Util.addInfoLog("application unique : " + application);
                    String redirectUrl = null;
                    if (application != null && !application.isEmpty()) {
                        Util.addInfoLog(" vao redirect application unique : " + application);
                        Util.addInfoLog(" vao redirect application type : " + applicationType);
                        ApplicationConfig config = ApplicationConfigManager.get(application);
                        if (config != null) {
                            Util.addInfoLog(" vao config not null : " + application);
                            if (userAgent.contains(Constant.DEVICE_NAME.MAC_DEVICE)) {
                                if (applicationType == Constant.APPLICATION_TYPE.IOS_PRODUCTION_APPLICATION) {
                                    Util.addInfoLog(" vao redirect  : " + application);
                                    String iosDeepLink = config.iOSProductionDeepLink;
                                    redirectUrl = iosDeepLink;
//                                    response.sendRedirect(iosDeepLink);
                                } else {
                                    String iosDeepLink = config.iOSEnterpriseDeepLink;
                                    redirectUrl = iosDeepLink;
//                                    response.sendRedirect(iosDeepLink);
                                }
                            } else if (userAgent.contains(Constant.DEVICE_NAME.ANDROID_DEVICE)) {
                                String androidDeepLink = config.androidDeepLink;
                                redirectUrl = androidDeepLink;
//                                response.sendRedirect(androidDeepLink);
                            }
                        }
                    }
                    if (Util.validateString(redirectUrl)) {
                        response.sendRedirect(redirectUrl);
                    }
                } catch (Exception ex) {
                    Util.addErrorLog(ex);
                }
                return;
            } else if (string != null && string.substring(1).startsWith("read_config")) {
                ServerConfig.initConfig(Core.CONFIG_FILE);
                return;
//                localhost:abc/welcom_back&application=applicationId&application_type=1
            } else if (string != null && string.substring(1).startsWith(ParamKey.WELCOME_BACK)) {                                           //ITS#22497
                try {                                                                                                                       //ITS#22497
                    Util.addInfoLog("welcome back: " + string);                                                                             //ITS#22497
                    String userAgent = rqst.getHeader(USER_AGENT);                                                                          //ITS#22497
                    Map<String, String> userCodeMap = convertStringToMap(string, "\\|");
                    String application = userCodeMap.get(Constant.WELCOME_BACK.APPLICATION);
                    Util.addInfoLog("id application: " + application);                                                                      //ITS#22497
                    int applicationType = Constant.APPLICATION_TYPE.IOS_PRODUCTION_APPLICATION;                                             //ITS#22497
                    if (userCodeMap.size() > 1) {                                                                                       //ITS#22497
                        try {                                                                                                               //ITS#22497
                            String applicationTypeString = userCodeMap.get(Constant.WELCOME_BACK.APPLICATION_TYPE);                                           //ITS#22497
                            applicationType = Integer.parseInt(applicationTypeString);
                        } catch (NumberFormatException ex) {                                                                                //ITS#22497
                            Util.addErrorLog(ex);                                                                                           //ITS#22497
                        }                                                                                                                   //ITS#22497
                    }                                                                                                                       //ITS#22497
                    String redirectUrl = "";                                                                                                //ITS#22497
                    if (application != null && !application.isEmpty()) {                                                                    //ITS#22497
                        ApplicationConfig config = ApplicationConfigManager.get(application);                                               //ITS#22497
                        if (config != null) {                                                                                               //ITS#22497
                            Util.addInfoLog("id config not null: " + application);                                                          //ITS#22497
                            if (userAgent.contains(Constant.DEVICE_NAME.MAC_DEVICE)) {                                                      //ITS#22497
                                if (applicationType == Constant.APPLICATION_TYPE.IOS_PRODUCTION_APPLICATION) {                              //ITS#22497
                                    String iosDeepLink = config.iOSProductionDeepLink;                                                      //ITS#22497
                                    redirectUrl = iosDeepLink;                                                                              //ITS#22497
                                    Util.addInfoLog("id application deep link: " + iosDeepLink);                                            //ITS#22497
                                } else {                                                                                                    //ITS#22497
                                    String iosDeepLink = config.iOSEnterpriseDeepLink;                                                      //ITS#22497
                                    redirectUrl = iosDeepLink;                                                                              //ITS#22497
                                }                                                                                                           //ITS#22497
                            } else if (userAgent.contains(Constant.DEVICE_NAME.ANDROID_DEVICE)) {                                           //ITS#22497
                                String androidDeepLink = config.androidDeepLink;                                                            //ITS#22497
                                redirectUrl = androidDeepLink;                                                                              //ITS#22497
                            }                                                                                                               //ITS#22497
                        }                                                                                                                   //ITS#22497
                    }                                                                                                                       //ITS#22497
                    if (Util.validateString(redirectUrl)) {                                                                                 //ITS#22497
                        response.sendRedirect(redirectUrl);                                                                                 //ITS#22497
                    }                                                                                                                       //ITS#22497
                } catch (Exception ex) {                                                                                                    //ITS#22497
                    Util.addErrorLog(ex);                                                                                                   //ITS#22497
                }                                                                                                                           //ITS#22497
                return;                                                                                                                     //ITS#22497
            }                                                                                                                               //ITS#22497
            try {
                String ip = Util.getClientIpAddr(hsr);                                                                      //NTQ#36234
                if (SettingManager.getMaintainMode()) {                                                                      //NTQ#36234
                    sendMaintenanceMessage(response);                                                                       //NTQ#36234
                    return;                                                                                                 //NTQ#36234
                } else {                                                                                                    //NTQ#36234
                    if (ServerConfig.MAINTAIN_TIME) {                                                                       //NTQ#36234
                        if (ServerConfig.FILTER_IP) {                                                                       //NTQ#36234
                            if (!ServerConfig.ALLOWED_IPS.contains(ip)) {                                                   //NTQ#36234
                                sendMaintenanceMessage(response);                                                           //NTQ#36234
                                return;                                                                                     //NTQ#36234
                            }                                                                                               //NTQ#36234
                        } else {                                                                                            //NTQ#36234
                            sendMaintenanceMessage(response);                                                               //NTQ#36234
                            return;                                                                                         //NTQ#36234
                        }                                                                                                   //NTQ#36234
                    }                                                                                                       //NTQ#36234
                }
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

            String userAgent = rqst.getHeader(USER_AGENT);
            Tracker tracker = new Tracker(api, userAgent);

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
                        || api.equals(API.GET_COMMUNICATION_POINT)
                        || api.equals(API.LIST_GIFT_TRANSACTION) //ITS#26083
                        || api.equals(API.REQUEST_UPDATE_GIFT) //ITS#26083
                        || api.equals(API.UPDATE_TRANSACTION_STATUS)) {                     //ITS#26083
                    IServiceAdapter adapter = AdapterManager.getAdapter(api);
                    Util.addDebugLog("api: " + api);
                    if (adapter != null) {
                        addTracker(tracker);
                        String result = adapter.callService(request);
                        dataBack(result, response);
                        tracker.endTracker();
                        addTracker(tracker);
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
                sendBadRequestNotification(response, ResponseMessage.InvailidTokenMessage);                 //NTQ#37166
                //
                return;
            } else if (!session.finishRegisterUser) {                                                          //NTQ#37166
                if (!api.equals(API.UPDATE_USER_INFOR)
                        && !api.equals(API.GET_USER_INFOR)
                        && !api.equals(API.GET_UPDATE_INFO_FLAGS)) { //
                    Util.addDebugLog("DefaultHandler -> invalid userId: " + session.userId);
                    response.setContentType(UTF8Stamp);
                    OutputStream out = response.getOutputStream();
                    JSONObject obj = new JSONObject();
                    obj.put(ParamKey.ERROR_CODE, ErrorCode.INVALID_ACCOUNT);
                    if (api.equals(API.CHECK_TOKEN)) {
                        JSONObject data = new JSONObject();
                        data.put(ParamKey.TOKEN_STRING, token);
                        obj.put(ParamKey.DATA, data);
                    }
                    out.write(obj.toJSONString().getBytes());
                    out.flush();
                    out.close();
                    return;
                }
            }

//            session.timeToLive = 0;
            session.resetExpire();
            request.put(ParamKey.USER_ID, session.userId);                                              //NTQ#37166
//            if(session.changeSettingToken){
//                if(!api.equals(API.GET_BACKEND_SETTING)){
//                    sendBadRequestNotification(response, ResponseMessage.NeedToGetSettingMessage, time);
//                    session.changeSettingToken = false;
//                    return;
//                }
//            }
            TokenElement tokenElement = JWTCreator.getInstance().parse(token);                          //NTQ#37166
            if (tokenElement == null) {                                                                   //NTQ#37166
                sendBadRequestNotification(response, ResponseMessage.InvailidTokenMessage);             //NTQ#37166
                return;                                                                                 //NTQ#37166
            }                                                                                           //NTQ#37166
            if (Util.validateString(session.registerApplicationId)) {                                   //NTQ#37166
                request.put(ParamKey.APPLICATION_ID, session.registerApplicationId);                    //NTQ#37166
            }                                                                                           //NTQ#37166
            if (Util.validateString(session.usingApplication)) {                                        //NTQ#37166
                request.put("using_application", session.usingApplication);                             //NTQ#37166
            }                                                                                           //NTQ#37166
            IServiceAdapter adapter = AdapterManager.getAdapter(api);
            if (adapter == null) {
                sendBadRequestNotification(response, ResponseMessage.BadResquestMessage);
                return;
            }
            if (adapter instanceof IServiceBackendAdapter) {
                if (session.sessionType == Constant.SESSION_TYPE.APPLICATION_USER) { // token is not belong administrator
                    sendBadRequestNotification(response, ResponseMessage.UnknownError);
                    return;
                }
            } else {
                if (!request.contain(ParamKey.APPLICATION)) {                                           //NTQ#37166
                    if (Util.validateString(tokenElement.getRegisterApplication())) {                   //NTQ#37166
                        request.put(ParamKey.APPLICATION, tokenElement.getRegisterApplication());       //NTQ#37166
                    }                                                                                   //NTQ#37166
                }                                                                                       //NTQ#37166
            }
            addTracker(tracker);
            String result = adapter.callService(request);
            dataBack(result, response);
            tracker.endTracker();
            addTracker(tracker);

        } catch (Exception ex) {
            Util.addErrorLog(ex);
            sendBadRequestNotification(response, ResponseMessage.UnknownError);
        }
    }

    private void addTracker(Tracker tracker) {
        ApiTracker.getInstance().put(tracker);
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

    private void sendMaintenanceMessage(HttpServletResponse response) {                         //NTQ#36234
        JSONObject dataResponse = new JSONObject();                                             //NTQ#36234
        dataResponse.put(ParamKey.ERROR_CODE, ErrorCode.SERVER_MAINTAIN);                       //NTQ#36234
        JSONObject data = new JSONObject();                                                     //NTQ#36234
        data.put(ParamKey.MAINTAIN_PAGE_CONTENT, SettingManager.getMaintainPageContent());      //ITS#24491, NTQ#36234
        data.put("use_redirect_url", ServerConfig.USE_REDIRECT_URL);                            //NTQ#36234
        data.put(REDIRECT_URL, ServerConfig.MAINTAIN_REDIRECT_URL);                             //NTQ#36234
        dataResponse.put(ParamKey.DATA, data);                                                  //NTQ#36234
        dataBack(dataResponse.toJSONString(), response);                                        //NTQ#36234
    }                                                                                           //NTQ#36234

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

    public static Map convertStringToMap(String request, String splitCharacter) {
        Map result = new HashMap<>();
        String[] adjustElements = request.substring(1).split(splitCharacter);
        for (String adjustElement : adjustElements) {
            String[] splitString = adjustElement.split(Constant.SPILIT_CHARACTER);
            if (splitString.length == 2) {								//NTQ#41207
                result.put(splitString[0], splitString[1]);
            }															//NTQ#41207
        }
        return result;
    }

    public static boolean isFromCmCode(Map adjustMap) {
        return adjustMap.get(Constant.TRACKER.LABEL) != null;
    }

    public static boolean isFromWelcomeBack(Map adjustMap) {
        return adjustMap.get(Constant.TRACKER.PACKAGE_NAME) != null;
    }
}
