package de.shiirroo.tps.cmd;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import de.shiirroo.tps.Tps;
import de.shiirroo.tps.TpsHelper;
import de.shiirroo.tps.page.TpsGuiPage;

import javax.annotation.Nonnull;

public class TpsGui extends AbstractPlayerCommand {

    public TpsGui() {
        super("gui", "Opens the TPS GUI page", false);
        requirePermission("tps.command.tps.gui");
    }

    @Override
    protected void execute(
            @Nonnull CommandContext ctx,
            @Nonnull Store<EntityStore> store,
            @Nonnull Ref<EntityStore> ref,
            @Nonnull PlayerRef playerRef,
            @Nonnull World world
    ) {
        Player player = store.getComponent(ref, Player.getComponentType());
        if (player == null) return;
        if (player.getWorld() == null) return;

        // Pass dynamic data to the page
        // In a real plugin, these would come from your server/game state
        double liveTps = TpsHelper.getLiveTPS(player.getWorld());
        double liveMspt = TpsHelper.getLiveMspt(player.getWorld());
        TpsGuiPage page = new TpsGuiPage(playerRef, liveTps, liveMspt);

        player.getPageManager().openCustomPage(ref, store, page);
        Tps.getInstance().getTpsManager().addGui(playerRef);
    }
}