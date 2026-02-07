package de.shiirroo.tps.webserver.adapter.nitrado;

import com.hypixel.hytale.server.core.plugin.PluginBase;
import de.shiirroo.tps.Tps;
import de.shiirroo.tps.webserver.adapter.WebAdapter;
import net.nitrado.hytale.plugins.webserver.WebServerPlugin;

public class NitradoWebServerHudAdapterV1 implements WebAdapter {

    private WebServerPlugin webServerPlugin;

    public NitradoWebServerHudAdapterV1(PluginBase plugin) {
        if  (plugin instanceof WebServerPlugin wsPlugin) {
            this.webServerPlugin = wsPlugin;
            Tps.getLog().info("NitradoWebServerHudAdapterV1 plugin found");
        } else {
            Tps.getLog().severe("Provided plugin is not an instance of WebServerPlugin. Web server integration will not work.");
        }
    }

    @Override
    public void registerWebServer() {
        try {
            webServerPlugin.addServlet(Tps.get(), "", new TpsServlet(webServerPlugin, Tps.get()));
                Tps.getLog().info("Registered TPS servlet with Nitrado WebServerPlugin at /Shiirroo/TPS");
        } catch (Exception e) {
            Tps.getLog().severe("Failed to register web server servlet: " + e.getMessage());
        }
    }

    @Override
    public void unregisterWebServer() {
        webServerPlugin.removeServlets(Tps.get());
            Tps.getLog().info("Unregistered TPS servlet from Nitrado WebServerPlugin");
    }



}
