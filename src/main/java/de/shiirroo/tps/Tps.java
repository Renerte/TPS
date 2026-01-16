package de.shiirroo.tps;

import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;
import java.util.logging.Logger;


public class Tps extends JavaPlugin {

    private static Tps instance;
    public final String PREFIX = "[Tps] ";

    public Tps(@NotNull JavaPluginInit init) {
        super(init);
        instance = this;
    }


    protected void setup() {
        HytaleServer.get().getCommandManager().register(new TpsCommand());
        Logger.getLogger(Tps.class.getName()).log(Level.INFO, PREFIX + "Plugin enabled!!");

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
}
