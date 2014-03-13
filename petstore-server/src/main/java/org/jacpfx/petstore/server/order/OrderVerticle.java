package org.jacpfx.petstore.server.order;

import com.google.gson.Gson;
import org.jacpfx.petstore.server.ws.WebSocketRepository;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.platform.Verticle;

/**
 * Created by amo on 25.02.14.
 */
public class OrderVerticle extends Verticle {
    private WebSocketRepository repository = new WebSocketRepository();

    public static Integer PORT_NUMER = 8080;
    private Gson parser = new Gson();

    @Override
    public void start() {

    }

    private HttpServer startServer() {
        return vertx.createHttpServer();
    }
}
