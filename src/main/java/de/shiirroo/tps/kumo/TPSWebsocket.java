package de.shiirroo.tps.kumo;

import com.hypixel.hytale.server.core.util.Config;
import de.shiirroo.tps.config.TPSConfig;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

@Slf4j
public final class TPSWebsocket extends WebSocketClient {

    @Getter
    private final Config<TPSConfig> tpsConfig;
    private final Logger logger = Logger.getLogger(TPSWebsocket.class.getName());


    public TPSWebsocket(URI serverUri, Config<TPSConfig> tpsConfig) {
        super(serverUri);
        this.logger.log(Level.INFO, "Initializing TPSWebsocket with server URI: " + serverUri);
        this.tpsConfig = tpsConfig;
    }

    @Override
    public void onOpen(ServerHandshake handshake) {
        logger.log(Level.INFO, "Websocket connection established with server.");
    }

    @Override
    public void onMessage(String message) {
        logger.log(Level.INFO, "[TPSWebsocket] " + message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        logger.log(Level.INFO, "Websocket closed by server.");
    }

    @Override
    public void onError(Exception ex) {
        logger.severe("WebSocket error: " + ex.getMessage());


    }

    public void sendTPS(TpsData tps) {
        if (isOpen()) {
            send(tps.toJson());
        }
    }
}