/**
 *
 */
package org.jacpfx.petstore.server.ws;

import org.vertx.java.core.http.ServerWebSocket;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Andy Moncsek on 25.02.14.
 */
public class WebSocketRepository {
    private List<ServerWebSocket> webSockets = new CopyOnWriteArrayList<>();

    public void addWebSocket(ServerWebSocket webSocket) {
        webSockets.add(webSocket);
    }

    public List<ServerWebSocket> getWebSockets() {
        return webSockets;
    }

    public void removeWebSocket(ServerWebSocket webSocket) {
        webSockets.remove(webSocket);
    }
}
