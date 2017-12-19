/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maven.mainserver.server.request;

import common.constant.ParamKey;
import common.util.Util;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author huongnt
 */
public class Request {

    public JSONObject reqObj;
    public String api;
    public String token;
    public String userAgent;
    public JSONParser jSONParser = new JSONParser();

    public Request() {
    }

    public static Request initRequest(String requestStr) {
        try {

            Request r = new Request();
            JSONObject obj = (JSONObject) r.jSONParser.parse(requestStr);
            r.api = (String) obj.get(ParamKey.API_NAME);
            r.token = (String) obj.get(ParamKey.TOKEN_STRING);
            r.reqObj = obj;

            return r;
        } catch (Exception ex) {
            Util.addErrorLog(ex);
            return null;
        }
    }

    public void put(String key, String value) {
        this.reqObj.put(key, value);
    }

    public boolean contain(String key) {
        return this.reqObj.containsKey(key);
    }

    public Object getParamValue(String key) {
        return this.reqObj.get(key);
    }

    public String toJson() {
        return reqObj.toJSONString();
    }

    @Override
    public String toString() {
        return toJson();
    }

}
