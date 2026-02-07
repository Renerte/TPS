package de.shiirroo.tps.webserver.adapter;

import com.hypixel.hytale.common.plugin.PluginIdentifier;
import com.hypixel.hytale.server.core.plugin.PluginBase;
import com.hypixel.hytale.server.core.plugin.PluginManager;
import de.shiirroo.tps.webserver.adapter.nitrado.NitradoWebServerHudAdapterV1;

public class WebAdapterSelector implements WebAdapter {

    private final WebAdapter webAdapter;
    private static final WebAdapterSelector instance = new WebAdapterSelector();

    public WebAdapterSelector() {
        PluginBase plugin = PluginManager.get().getPlugin(PluginIdentifier.fromString("Nitrado:WebServer"));
        if (plugin != null) {
            webAdapter = new NitradoWebServerHudAdapterV1(plugin);
        } else {
            webAdapter = new DefaultWebAdapter();
        }

    }

    public static WebAdapterSelector get() {
        return instance;
    }

    @Override
    public void registerWebServer() {
        webAdapter.registerWebServer();
    }


    @Override
    public void unregisterWebServer() {
        webAdapter.unregisterWebServer();
    }
}
