package de.shiirroo.tps.cmd;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import de.shiirroo.tps.MetricsTime;
import de.shiirroo.tps.Tps;
import de.shiirroo.tps.helper.TpsHelper;
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
        if (!player.hasPermission("tps.command.tps.gui")) {
            player.sendMessage(Message.parse("You don't have permission to use this command."));
            return;
        }
        if (player.getWorld() == null) return;

        // Pass dynamic data to the page
        // In a real plugin, these would come from your server/game state
        double liveTps = TpsHelper.getLiveTPS(player.getWorld());
        double liveMspt = TpsHelper.getLiveMspt(player.getWorld());
        double ten_sec_current_tps = TpsHelper.getTPS(world, MetricsTime.TEN_SECONDS);
        double ten_sec_avgerage_tps = TpsHelper.getTPSAvg(world, MetricsTime.TEN_SECONDS);
        TpsGuiPage page = new TpsGuiPage(playerRef, liveTps, liveMspt, ten_sec_current_tps, ten_sec_avgerage_tps);

        player.getPageManager().openCustomPage(ref, store, page);
        Tps.get().getTpsManager().addGui(playerRef);
    }
}