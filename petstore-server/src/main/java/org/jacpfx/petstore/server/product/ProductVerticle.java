package org.jacpfx.petstore.server.product;

import com.google.gson.Gson;
import org.jacpfx.petstore.dto.ProductListDTO;
import org.jacpfx.petstore.model.Product;
import org.jacpfx.petstore.server.ws.WebSocketRepository;
import org.jacpfx.petstore.util.MessageUtil;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.http.ServerWebSocket;
import org.vertx.java.platform.Verticle;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Pattern;

/**
 * Created by Andy Moncsek on 25.02.14.
 */
public class ProductVerticle extends Verticle {
    private WebSocketRepository repository = new WebSocketRepository();

    public static Integer PORT_NUMER = 8080;

    private List<Product> all = new CopyOnWriteArrayList<>(
            Arrays.asList(new Product("Katze","box1.png",200d),
                    new Product("Hund","box2.png",200d),
                    new Product("Pferd","box3.png",2000d),
                    new Product("Koala","dog.png",1000d),
                    new Product("Tieger","box1.png",5000d),
                    new Product("Giraffe","box2.png",2000d),
                    new Product("Igel","box3.png",100d))
    );
    private Gson parser = new Gson();

    @Override
    public void start() {
        final HttpServer httpServer = startServer();
        registerEventBusMessageHandlerAddAll();
        registerEventBusMessageHandlerAdd();
        registerWebsocketHandler(httpServer);
        httpServer.listen(PORT_NUMER);

        container.deployVerticle("org.jacpfx.petstore.server.webserver.WebServerVerticle",10);
    }

    private HttpServer startServer() {
        return vertx.createHttpServer();
    }

    private void registerEventBusMessageHandlerAddAll() {
        vertx.eventBus().registerHandler("org.jacpfx.petstore.addAll", this::handleWSAddAllMessagesFromBus);
    }

    /**
     * Handle redirected messages from WebSocket.
     *
     * @param message
     */
    private void handleWSAddAllMessagesFromBus(final Message<byte[]> message) {
        final String productListJSON = getProductListJSON(message);
        repository.getWebSockets()
                .parallelStream()
                .forEach(ws -> ws.writeTextFrame(productListJSON));
    }


    private String getProductListJSON(final Message<byte[]> message) {

        try {
            final ProductListDTO dto = MessageUtil.getMessage(message.body(), ProductListDTO.class);
            all = new CopyOnWriteArrayList<>(dto.getProducts());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return parser.toJson(new ProductListDTO(ProductListDTO.State.ALL,all));
    }

    private void registerEventBusMessageHandlerAdd() {
        vertx.eventBus().registerHandler("org.jacpfx.petstore.add", this::handleWSAddMessagesFromBus);
    }

    /**
     * Handle redirected messages from WebSocket.
     *
     * @param message
     */
    private void handleWSAddMessagesFromBus(final Message<byte[]> message) {
        final String productListJSON = addProductToProductListAndReturnJSON(message);
        repository.getWebSockets()
                .parallelStream()
                .forEach(ws -> ws.writeTextFrame(productListJSON));
    }

    private String addProductToProductListAndReturnJSON(final Message<byte[]> message) {
        Product product = null;
        try {
            product = MessageUtil.getMessage(message.body(), Product.class);
            all.add(product);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return parser.toJson(new ProductListDTO(ProductListDTO.State.UPDATE,Arrays.asList(product)));
    }





    /**
     * Registers onMessage and onClose message handler for WebSockets
     *
     * @param httpServer
     */
    private void registerWebsocketHandler(final HttpServer httpServer) {
        httpServer.websocketHandler((serverSocket) -> {
            final String path = serverSocket.path();
            switch (path) {
                case "/all":
                    repository.addWebSocket(serverSocket);
                    // reply to first contact
                    serverSocket.writeTextFrame(parser.toJson(new ProductListDTO(ProductListDTO.State.ALL,all)));
                    // add handler for further calls
                    serverSocket.dataHandler(data -> {
                        serverSocket.writeTextFrame(parser.toJson(new ProductListDTO(ProductListDTO.State.ALL,all)));
                    });
                    break;
                case "/update":
                    serverSocket.dataHandler(data -> vertx.eventBus().publish("org.jacpfx.petstore.add", data.getBytes()));
                    break;
                case "/updateAll":
                    serverSocket.dataHandler(data -> vertx.eventBus().publish("org.jacpfx.petstore.addAll", data.getBytes()));
                    break;
            }
            serverSocket.closeHandler((close) -> handleConnectionClose(close, serverSocket));

        });
    }

    /**
     * handles connection close
     *
     * @param event
     */
    private void handleConnectionClose(final Void event, ServerWebSocket socket) {
        repository.removeWebSocket(socket);
    }

}
