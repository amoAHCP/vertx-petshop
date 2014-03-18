package org.jacpfx.petstore.server.webserver;

import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.http.RouteMatcher;
import org.vertx.java.platform.Verticle;

/**
 * The WebServer was moved to an extra verticle
 * Created by Andy Moncsek on 11.03.14.
 */
public class WebServerVerticle extends Verticle {
    public static Integer PORT_NUMER = 8000;
    @Override
    public void start() {
        HttpServer server = startServer();
        RouteMatcher rm = new RouteMatcher();


        rm.get("/",(request)->{
            System.out.println("Path: "+request.path());
            String file = request.path().equals("/") ? "index.html" : request.path();
            request.response().sendFile("web/" + file);
        });

        // TODO add better REGEX to provide more security
        rm.getWithRegEx("/js/.*", (request) -> {
            System.out.println("Path JS: " + request.path());
            String file = request.path().equals("/") ? "index.html" : request.path();
            request.response().sendFile("web" + file);
        });
        // TODO add better REGEX to provide more security
        rm.getWithRegEx("/css/.*", (request) -> {
            System.out.println("Path css: " + request.path());
            String file = request.path().equals("/") ? "index.html" : request.path();
            request.response().sendFile("web" + file);
        });
        server.requestHandler(rm).listen(PORT_NUMER);
         // if no resource allowed
        //req.response().setStatusCode(404);
        //req.response().end();

    }

    private HttpServer startServer() {
        return vertx.createHttpServer();
    }
}
