package de.shiirroo.tps;

import com.hypixel.hytale.event.EventPriority;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.event.events.player.PlayerDisconnectEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import de.shiirroo.tps.cmd.TpsCommand;
import de.shiirroo.tps.config.TPSConfig;
import de.shiirroo.tps.hud.TpsManager;
import de.shiirroo.tps.webserver.adapter.WebAdapterSelector;
import lombok.Getter;
import com.hypixel.hytale.server.core.util.Config;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Tps extends JavaPlugin {

    private static Tps instance;
    @Getter
    public final String PREFIX = "[Tps] ";
    @Getter
    private final TpsManager tpsManager;
    @Getter
    private static final Logger log = Logger.getLogger(Tps.class.getName());
    @Getter
    private final Config<TPSConfig> config;
    @Getter
    private ScheduledFuture<?> tpsTask;

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
        tpsTask = HytaleServer.SCHEDULED_EXECUTOR.scheduleAtFixedRate(tpsManager, 0, 1, TimeUnit.SECONDS);
        WebAdapterSelector.get().registerWebServer();
    }

    @Override
    protected void start() {
        Logger.getLogger(Tps.class.getName()).log(Level.INFO, PREFIX + "Plugin started!!");
    }

    @Override
    protected void shutdown() {
        Logger.getLogger(Tps.class.getName()).log(Level.INFO, PREFIX + "Plugin shutdown!!");
        if (tpsTask != null && !tpsTask.isCancelled()) {
            tpsTask.cancel(true);
        }
        WebAdapterSelector.get().unregisterWebServer();
    }

    public static Tps get() {
        return instance;
    }

    private void handlePlayerLeave() {
        getEventRegistry().register(
                EventPriority.FIRST, PlayerDisconnectEvent.class, event -> {
                    tpsManager.removePlayerRef(event.getPlayerRef());
                }
        );
    }


}
