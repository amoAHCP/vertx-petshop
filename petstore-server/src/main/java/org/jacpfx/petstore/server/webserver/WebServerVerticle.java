package org.jacpfx.petstore.server.webserver;

import org.jacpfx.petstore.util.GlobalConstants;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.http.RouteMatcher;
import org.vertx.java.platform.Verticle;

/**
 * The WebServer was moved to an extra verticle
 * Created by Andy Moncsek on 11.03.14.
 */
public class WebServerVerticle extends Verticle {
    public static Integer PORT_NUMBER = 8000;

    @Override
    public void start() {
        HttpServer server = startServer();
        RouteMatcher rm = new RouteMatcher();


        rm.get("/", (request) -> {
            System.out.println("Path: " + request.path());
            this.handlePath("index.html", request);
        });

        // TODO add better REGEX to provide more security
        rm.getWithRegEx("/js/.*", (request) -> {
            System.out.println("Path JS: " + request.path());
            this.handlePath("index.html", request);
        });
        // TODO add better REGEX to provide more security
        rm.getWithRegEx("/css/.*", (request) -> {
            System.out.println("Path css: " + request.path());
            this.handlePath("index.html", request);
        });

        rm.getWithRegEx("/img/.*", (request) -> {
            System.out.println("Path IMG: " + request.path());
            this.handlePath(GlobalConstants.DUMMY_PICTURE, request);
        });
        server.requestHandler(rm).listen(PORT_NUMBER);
        // if no resource allowed
        //req.response().setStatusCode(404);
        //req.response().end();

    }

    private void handlePath(String defaultFile, HttpServerRequest request) {
        String file = request.path().equals("/") ? defaultFile : request.path();
        request.response().sendFile("web/" + file);

    }


    private HttpServer startServer() {
        return vertx.createHttpServer();
    }
}
