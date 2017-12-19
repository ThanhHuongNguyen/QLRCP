/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maven.mainserver.server.adapter;

import maven.mainserver.server.request.Request;

/**
 *
 * @author huongnt
 */
public interface IServiceAdapter {

    public String callService(Request request);
}
