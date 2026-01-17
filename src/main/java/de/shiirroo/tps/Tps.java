package de.shiirroo.tps;

import com.hypixel.hytale.event.EventPriority;
import com.hypixel.hytale.server.core.event.events.player.PlayerDisconnectEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import de.shiirroo.tps.cmd.TpsCommand;
import de.shiirroo.tps.hud.TpsManager;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;
import java.util.logging.Logger;


public class Tps extends JavaPlugin {

    private static Tps instance;
    @Getter
    public final String PREFIX = "[Tps] ";
    @Getter
    private final TpsManager tpsManager;
    @Getter
    private final Logger log = Logger.getLogger(Tps.class.getName());

    public Tps(@NotNull JavaPluginInit init) {
        super(init);
        instance = this;
        this.tpsManager = new TpsManager();
    }


    protected void setup() {
        handlePlayerLeave();
        getCommandRegistry().registerCommand(new TpsCommand());
        Logger.getLogger(Tps.class.getName()).log(Level.INFO, PREFIX + "Plugin enabled!!");
        getEntityStoreRegistry().registerSystem(this.tpsManager);
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
                    tpsManager.removePlayerRef(event.getPlayerRef());
                }
        );
    }
}
