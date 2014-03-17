package org.jacpfx.petstore.server.order;

import com.google.gson.Gson;
import org.jacpfx.petstore.dto.OrderListDTO;
import org.jacpfx.petstore.dto.OrderProcessingDTO;
import org.jacpfx.petstore.model.Order;
import org.jacpfx.petstore.server.ws.WebSocketRepository;
import org.jacpfx.petstore.util.MessageUtil;
import org.jacpfx.petstore.util.Serializer;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.http.ServerWebSocket;
import org.vertx.java.platform.Verticle;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Created by Andy Moncsek on 25.02.14.
 */
public class OrderVerticle extends Verticle {
    private WebSocketRepository repository = new WebSocketRepository();
    private WebSocketRepository shopRepository = new WebSocketRepository();
    private Set<Order> all = new HashSet<>();

    public static Integer PORT_NUMER = 9090;
    private Gson parser = new Gson();

    @Override
    public void start() {
        final HttpServer httpServer = startServer();
        registerEventBusMessageHandlerPlaceOrder();
        registerEventBusMessageHandlerUpdateOrder();
        registerWebsocketHandler(httpServer);
        httpServer.listen(PORT_NUMER);
        System.out.println("Order started");
    }

    private HttpServer startServer() {
        return vertx.createHttpServer();
    }

    private void registerEventBusMessageHandlerPlaceOrder() {
        vertx.eventBus().registerHandler("org.jacpfx.petstore.placeOrder", this::handleWSPlaceOrderMessagesFromBus);
    }

    private void handleWSPlaceOrderMessagesFromBus(final Message<byte[]> message) {
        try {
            final OrderProcessingDTO orderProcessing = MessageUtil.getMessage(message.body(), OrderProcessingDTO.class);
            addOrderAndBroadCast(orderProcessing.getOrder());
            final Optional<ServerWebSocket> result = shopRepository.getWebSockets().
                    parallelStream().
                    filter(socket ->                            socket.textHandlerID().equals(orderProcessing.getWsTextFrameID())
                    ).findFirst();
            if(result.isPresent())result.get().writeTextFrame("OK");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    private void registerEventBusMessageHandlerUpdateOrder() {
        vertx.eventBus().registerHandler("org.jacpfx.petstore.updateOrder", this::handleWSUpdateOrderMessagesFromBus);
    }

    /**
     * Handle redirected messages from WebSocket.
     *
     * @param message
     */
    private void handleWSUpdateOrderMessagesFromBus(final Message<byte[]> message) {
        try {
            final Order order = MessageUtil.getMessage(message.body(), Order.class);
            addOrderAndBroadCast(order);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void addOrderAndBroadCast(final Order order) {
        try {
            all.add(order);
            final Buffer buffer = new Buffer(Serializer.serialize(new OrderListDTO(new HashSet<>(Arrays.asList(order)), OrderListDTO.State.UPDATE)));
            repository.getWebSockets()
                    .parallelStream()
                    .forEach(ws -> ws.writeBinaryFrame(buffer));
        } catch (IOException e) {
            e.printStackTrace();
        }
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
                case "/getAllOrders":
                    repository.addWebSocket(serverSocket);

                    final Runnable r = () -> {
                        try {
                            serverSocket.writeBinaryFrame(new Buffer(Serializer.serialize(new OrderListDTO(all, OrderListDTO.State.ALL))));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    };

                    // reply to first contact
                    r.run();

                    // add handler for further calls
                    serverSocket.dataHandler(data -> r.run());
                    break;
                case "/updateOrder":
                    serverSocket.dataHandler(data -> vertx.eventBus().publish("org.jacpfx.petstore.updateOrder", data.getBytes()));
                    break;
                case "/placeOrder":
                    shopRepository.addWebSocket(serverSocket);
                    serverSocket.dataHandler(data -> {
                        final String json = data.getString(0, data.length());
                        final Order order = parser.fromJson(json, Order.class);
                        System.out.println("JSON: " + json + "  ID: " + serverSocket.textHandlerID());
                        try {
                            vertx.eventBus().publish("org.jacpfx.petstore.placeOrder", Serializer.serialize(new OrderProcessingDTO(order, serverSocket.textHandlerID())));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
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
