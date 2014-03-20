package org.jacpfx.petstore.gui.backoffice.components;

import com.google.gson.Gson;
import javafx.event.Event;
import org.jacpfx.api.annotations.Resource;
import org.jacpfx.api.annotations.component.Component;
import org.jacpfx.api.annotations.lifecycle.PostConstruct;
import org.jacpfx.api.annotations.lifecycle.PreDestroy;
import org.jacpfx.api.message.Message;
import org.jacpfx.petstore.dto.ProductListDTO;
import org.jacpfx.petstore.gui.backoffice.configuration.BaseConfig;
import org.jacpfx.petstore.model.Product;
import org.jacpfx.petstore.util.Serializer;
import org.jacpfx.rcp.component.CallbackComponent;
import org.jacpfx.rcp.context.Context;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.VertxFactory;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.http.HttpClient;
import org.vertx.java.core.http.WebSocket;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by amo on 18.03.14.
 */
@Component(id = BaseConfig.WSPRODUCT_COMPONENT_ID, name = "WSProductComponent", active = true)
public class WSProductComponent implements CallbackComponent {
    @Resource
    private Context context;
    private WebSocket allProductsWebSocket;
    private WebSocket updateProductsWebSocket;
    private HttpClient allProductsClient;
    private HttpClient updateProductsClient;

    @Override
    public Object handle(Message<Event, Object> eventObjectMessage) throws Exception {
         if(eventObjectMessage.isMessageBodyTypeOf(Product.class)) {
             updateProductsWebSocket.write(new Buffer(Serializer.serialize(eventObjectMessage.getTypedMessageBody(Product.class))));
         }
        return null;
    }

    @PostConstruct
    public void onStart() {
        try {
            connectToAllProducts("localhost", "8080");
            connectToUpdateProducts("localhost", "8080");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @PreDestroy
    public void onClose() {
        allProductsClient.close();
        updateProductsClient.close();
    }

    private void connectToUpdateProducts(final String ip, final String port) throws InterruptedException {
        final CountDownLatch connected = new CountDownLatch(1);
        final Vertx vertx = VertxFactory.newVertx();
        updateProductsClient = vertx.
                createHttpClient().
                setHost(ip).
                setPort(Integer.valueOf(port)).
                connectWebsocket("/update",
                        (ws) -> {
                            connected.countDown();
                            this.updateProductsWebSocket = ws;

                        }
                );
        connected.await(5000, TimeUnit.MILLISECONDS);
    }

    private void connectToAllProducts(final String ip, final String port) throws InterruptedException {
        final CountDownLatch connected = new CountDownLatch(1);
        final Vertx vertx = VertxFactory.newVertx();
        allProductsClient = vertx.
                createHttpClient().
                setHost(ip).
                setPort(Integer.valueOf(port)).
                connectWebsocket("/all",
                        (ws) -> {
                            connected.countDown();
                            this.allProductsWebSocket = ws;
                            ws.dataHandler(this::allProductsData);
                        }
                );
        connected.await(5000, TimeUnit.MILLISECONDS);
    }

    private void allProductsData(Buffer buffer) {
        Gson parser = new Gson();
        String json = buffer.getString(0, buffer.length());
        ProductListDTO p = parser.fromJson(json, ProductListDTO.class);
        context.send(BaseConfig.PRODUCT_COMPONENT_ID, p);
    }
}
