package org.jacpfx.petstore.gui.backoffice.components;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.event.Event;
import org.jacpfx.api.annotations.Resource;
import org.jacpfx.api.annotations.component.Component;
import org.jacpfx.api.annotations.lifecycle.PostConstruct;
import org.jacpfx.api.annotations.lifecycle.PreDestroy;
import org.jacpfx.api.message.Message;
import org.jacpfx.petstore.dto.ProductListDTO;
import org.jacpfx.petstore.gui.backoffice.configuration.BaseConfig;
import org.jacpfx.petstore.model.Product;
import org.jacpfx.rcp.component.CallbackComponent;
import org.jacpfx.rcp.context.Context;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.VertxFactory;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.http.HttpClient;
import org.vertx.java.core.http.WebSocket;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by amo on 18.03.14.
 */
@Component(id = BaseConfig.WSPRODUCT_COMPONENT_ID, name = "WSProductComponent", active = true)
public class WSProductComponent implements CallbackComponent {
    @Resource
    private Context context;
    private WebSocket webSocket;
    private HttpClient allProductsClient;

    @Override
    public Object handle(Message<Event, Object> eventObjectMessage) throws Exception {
        return null;
    }

    @PostConstruct
    public void onStart() {
        try {
            connectToAllProducts("localhost","8080");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @PreDestroy
    public void onClose() {
        allProductsClient.close();

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
                            this.webSocket = ws;
                            ws.dataHandler(this::allProductsData);
                        });
        connected.await(5000, TimeUnit.MILLISECONDS);
    }

    private void allProductsData(Buffer buffer) {
        Type collectionType = new TypeToken<List<Product>>(){}.getType();
        Gson parser = new Gson();
        String json =  buffer.getString(0, buffer.length());
        List<Product> p = parser.fromJson(json, collectionType);
        context.send(BaseConfig.PRODUCT_COMPONENT_ID, new ProductListDTO(ProductListDTO.State.ALL,p));
    }
}
