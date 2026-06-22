package de.shiirroo.tps;

import com.hypixel.hytale.event.EventPriority;
import com.hypixel.hytale.server.core.event.events.player.PlayerDisconnectEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.util.Config;
import de.shiirroo.tps.cmd.TpsCommand;
import de.shiirroo.tps.config.TPSConfig;
import de.shiirroo.tps.manager.TpsManager;
import de.shiirroo.tps.web.WebServer;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;
import java.util.logging.Logger;


public class Tps extends JavaPlugin {

    private static Tps instance;
    public final String PREFIX = "[Tps] ";
    @Getter
    private final TpsManager tpsManager;
    @Getter
    private static final Logger log = Logger.getLogger(Tps.class.getName());
    @Getter
    private final Config<TPSConfig> config;

    public Tps(@NotNull JavaPluginInit init) {
        super(init);
        instance = this;
        this.config = this.withConfig(getIdentifier().getName(), TPSConfig.CODEC);
        this.config.load();
        this.tpsManager = new TpsManager(config);
    }


    protected void setup() {
        this.config.save();
        handlePlayerLeave();
        getCommandRegistry().registerCommand(new TpsCommand());
        Logger.getLogger(Tps.class.getName()).log(Level.INFO, PREFIX + "Plugin enabled!!");
        tpsManager.initialize();
        if(this.config.get().getWebServerConfig().isEnableWebServer()) WebServer.get().registerWebServer();
    }

    @Override
    protected void start() {
        Logger.getLogger(Tps.class.getName()).log(Level.INFO, PREFIX + "Plugin started!!");
    }

    @Override
    protected void shutdown() {
        Logger.getLogger(Tps.class.getName()).log(Level.INFO, PREFIX + "Plugin shutdown!!");
        tpsManager.shutdown();

        if(this.config.get().getWebServerConfig().isEnableWebServer()) WebServer.get().unregisterWebServer();
    }

    public static Tps get() {
        return instance;
    }

    private void handlePlayerLeave() {
        getEventRegistry().register(
                EventPriority.FIRST, PlayerDisconnectEvent.class, event -> tpsManager.getTaskManager().getHudTask().removeEffectPlayer(event.getPlayerRef())
        );
    }


}
