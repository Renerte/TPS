package de.shiirroo.tps.webserver.adapter;

import com.sun.net.httpserver.HttpServer;
import de.shiirroo.tps.Tps;
import de.shiirroo.tps.history.TpsHistory;
import lombok.Getter;

import java.net.InetSocketAddress;

class DefaultWebAdapter implements WebAdapter {

    @Getter
    private HttpServer server;


    @Override
    public void registerWebServer() {
        try {
            var config = Tps.get().getConfig().get().getWebServerConfig();
            server = HttpServer.create(new InetSocketAddress(config.getBindIP(), config.getPort()), 0);
            server.createContext("/Shiirroo/TPS", exchange -> {
                var test = TpsHistory.get().asJson();
                exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
                exchange.sendResponseHeaders(200, test.getBytes().length);
                exchange.getResponseBody().write(test.getBytes());
                exchange.close();
            });
            server.start();
            Tps.getLog().info("DefaultWebAdapter: Started embedded HTTP server on " + config.getBindIP() + ":" + config.getPort());
        } catch (Exception e) {
            Tps.getLog().severe("DefaultWebAdapter: Failed to start embedded HTTP server: " + e.getMessage());
        }
    }

    @Override
    public void unregisterWebServer() {
        if (server != null) {
            server.stop(0);
            Tps.getLog().info("DefaultWebAdapter: Stopped embedded HTTP server");
        } else {
            Tps.getLog().warning("DefaultWebAdapter: No server instance found to stop");
        }
    }

}
