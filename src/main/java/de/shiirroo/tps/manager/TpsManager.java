package de.shiirroo.tps.manager;

import com.hypixel.hytale.server.core.util.Config;
import de.shiirroo.tps.Tps;
import de.shiirroo.tps.config.TPSConfig;
import de.shiirroo.tps.kumo.TPSWebsocket;
import lombok.Getter;

import java.net.URI;

public class TpsManager implements IManager {

    @Getter
    private final Config<TPSConfig> settings;
    private TPSWebsocket tpsWebsocket;
    @Getter
    private final TaskManager taskManager;

    public TpsManager(Config<TPSConfig> settings) {
        this.settings = settings;
        this.taskManager = new TaskManager();
    }


    @Override
    public void initialize() {
        taskManager.initialize();
        initializeKumoWebSocket();
    }

    public void shutdown() {
        if (tpsWebsocket != null) tpsWebsocket.close();
        taskManager.shutdown();
    }

    public void initializeKumoWebSocket() {
        if(settings.get().getKumoConfig().isEnableKumoSupport()) {
            try {
                tpsWebsocket = new TPSWebsocket(URI.create(settings.get().getKumoConfig().getKumoURL()), settings);
                tpsWebsocket.connect();
                Tps.getLog().info("Kumo Websocket initialized");
            } catch (Exception e) {
                Tps.getLog().severe("Failed to initialize Kumo WebSocket: " + e.getMessage());
            }
        }
    }

}
