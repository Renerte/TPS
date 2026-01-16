package de.shiirroo.tps;

import com.hypixel.hytale.builtin.hytalegenerator.datastructures.compression.Compressor;
import com.hypixel.hytale.common.plugin.PluginIdentifier;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.event.EventPriority;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.player.PlayerDisconnectEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.plugin.PluginBase;
import com.hypixel.hytale.server.core.task.TaskRegistration;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import de.shiirroo.tps.cmd.TpsCommand;
import de.shiirroo.tps.cmd.TpsShow;
import de.shiirroo.tps.history.TpsHandler;
import de.shiirroo.tps.hud.HudManager;
import lombok.Getter;
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
    private final HudManager hudManager;
    private final Logger logger = Logger.getLogger(Tps.class.getName());

    public Tps(@NotNull JavaPluginInit init) {
        super(init);
        instance = this;
        this.hudManager = new HudManager();
    }


    protected void setup() {
        handlePlayerLeave();
        getCommandRegistry().registerCommand(new TpsCommand());
        Logger.getLogger(Tps.class.getName()).log(Level.INFO, PREFIX + "Plugin enabled!!");
        getEntityStoreRegistry().registerSystem(this.hudManager);
    }

    @Override
    protected void start() {
        Logger.getLogger(Tps.class.getName()).log(Level.INFO, PREFIX + "Plugin started!!");
    }

    @Override
    protected void shutdown() {
        Logger.getLogger(Tps.class.getName()).log(Level.INFO, PREFIX + "Plugin shutdown!!");
    }

    public static Tps getInstance() {
        return instance;
    }

    private void handlePlayerLeave() {
        getEventRegistry().register(
                EventPriority.FIRST, PlayerDisconnectEvent.class, event -> {
                    hudManager.removePlayerRef(event.getPlayerRef());
                }
        );
    }
}
