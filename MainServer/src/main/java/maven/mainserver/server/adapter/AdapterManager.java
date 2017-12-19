/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maven.mainserver.server.adapter;

import java.util.HashMap;

/**
 *
 * @author huongnt
 */
public class AdapterManager {

    public static final HashMap<String, IServiceAdapter> m = new HashMap<>();

    static {
    }

    public static IServiceAdapter getAdapter(String apiName) {
        return m.get(apiName);
    }

}
