package de.shiirroo.tps.cmd;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import de.shiirroo.tps.MetricsTime;
import de.shiirroo.tps.TpsHelper;
import de.shiirroo.tps.hud.HudManager;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.logging.Logger;

public class TpsShow  extends AbstractPlayerCommand {

    @Getter
    private final HudManager hudManager;
    private final Logger log = Logger.getLogger("TpsShow");

    public TpsShow(HudManager hudManager) {
        super("show", "Display TPS on HUD.", false);
        this.hudManager = hudManager;
    }

    @Override
    protected void execute(@NotNull CommandContext context, @NotNull Store store, @NotNull Ref storeRef, @NotNull PlayerRef playerRef, @NotNull World world) {
        Ref<EntityStore> ref = playerRef.getReference();
        if (ref != null) {
            Player player = ref.getStore().getComponent(ref, Player.getComponentType());
            if (player == null) {
                log.warning("Player not found");
                return;
            }
            boolean toggleTo = hudManager.toggleHud(player, playerRef);
            if (toggleTo) {
                context.sendMessage(Message.raw("TPS HUD enabled").color(Color.GREEN));
            } else {
                context.sendMessage(Message.raw("TPS HUD disabled").color(Color.RED));
            }

        }
    }

}
