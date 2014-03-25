package org.jacpfx.petstore.gui.backoffice.components;

import com.google.gson.Gson;
import javafx.event.Event;
import org.jacpfx.api.annotations.Resource;
import org.jacpfx.api.annotations.component.Component;
import org.jacpfx.api.annotations.lifecycle.PostConstruct;
import org.jacpfx.api.annotations.lifecycle.PreDestroy;
import org.jacpfx.api.message.Message;
import org.jacpfx.petstore.dto.OrderListDTO;
import org.jacpfx.petstore.dto.ProductListDTO;
import org.jacpfx.petstore.gui.backoffice.configuration.BaseConfig;
import org.jacpfx.petstore.util.MessageUtil;
import org.jacpfx.rcp.component.CallbackComponent;
import org.jacpfx.rcp.context.Context;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.VertxFactory;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.http.HttpClient;
import org.vertx.java.core.http.WebSocket;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by amo on 18.03.14.
 */
@Component(id = BaseConfig.WSORDER_COMPONENT_ID, name = "WSOrderComponent", active = true)
public class WSOrderComponent implements CallbackComponent {
    @Resource
    private Context context;
    private WebSocket allOrdersWSClient;
    private HttpClient allOrdersClient;

    @Override
    public Object handle(Message<Event, Object> eventObjectMessage) throws Exception {
        return null;
    }

    @PostConstruct
    public void onStart() {
        try {
            connectToAllOrders(BaseConfig.SERVER, "9090");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @PreDestroy
    public void onClose() {
        allOrdersClient.close();

    }


    private void connectToAllOrders(final String ip, final String port) throws InterruptedException {
        final CountDownLatch connected = new CountDownLatch(1);
        final Vertx vertx = VertxFactory.newVertx();
        allOrdersClient = vertx.
                createHttpClient().
                setHost(ip).
                setPort(Integer.valueOf(port)).
                connectWebsocket("/getAllOrders",
                        (ws) -> {
                            connected.countDown();
                            this.allOrdersWSClient = ws;
                            ws.dataHandler(this::allProductsData);
                        }
                );
        connected.await(5000, TimeUnit.MILLISECONDS);
    }

    private void allProductsData(Buffer buffer) {
        OrderListDTO dto = null;
        try {
            dto = MessageUtil.getMessage(buffer.getBytes(), OrderListDTO.class);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        context.send(BaseConfig.ORDER_COMPONENT_ID, dto);
    }
}
