package de.shiirroo.tps.page;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.protocol.packets.interface_.Page;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import de.shiirroo.tps.MetricsTime;
import de.shiirroo.tps.Tps;
import de.shiirroo.tps.helper.TpsHelper;

import javax.annotation.Nonnull;

public class TpsGuiPage extends InteractiveCustomUIPage<TpsGuiPage.CloseEventData> {

    // Data passed via constructor - will be displayed in UI
    private double live_tps;
    private double live_mspt;
    private double ten_sec_current_tps;
    private double ten_sec_avgerage_tps;

    public TpsGuiPage(PlayerRef playerRef, double liveTps, double liveMspt, double tenSecCurrentTps, double tenSecAvgerageTps) {
        super(playerRef, CustomPageLifetime.CanDismissOrCloseThroughInteraction, CloseEventData.CODEC);
        this.live_tps = liveTps;
        this.live_mspt = liveMspt;
        this.ten_sec_current_tps = tenSecCurrentTps;
        this.ten_sec_avgerage_tps = tenSecAvgerageTps;
    }

    public TpsGuiPage(@Nonnull PlayerRef playerRef, double live_tps, double live_mspt) {
        super(playerRef, CustomPageLifetime.CanDismissOrCloseThroughInteraction, CloseEventData.CODEC);
        this.live_tps = live_tps;
        this.live_mspt = live_mspt;

    }

    @Override
    public void build(@Nonnull Ref<EntityStore> ref, @Nonnull UICommandBuilder cmd, @Nonnull UIEventBuilder evt, @Nonnull Store<EntityStore> store) {
        cmd.append("Pages/TPSPage.ui");
        updateValues(cmd, evt);
    }

    @Override
    public void handleDataEvent(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store, @Nonnull CloseEventData data) {
        Player player = store.getComponent(ref, Player.getComponentType());
        if (player == null) return;

        Tps.get().getTpsManager().getTaskManager().getGuiTask().removeEffectPlayer(player, this.playerRef);
        player.getPageManager().setPage(ref, store, Page.None);
    }

    private void updateValues(UICommandBuilder cmd, UIEventBuilder evt) {
        cmd.set("#TpsValue.Text", String.valueOf(live_tps));
        cmd.set("#MsptValue.Text", String.valueOf(live_mspt));

        //cmd.set("#TenSecondCurrentTps.Text", "Current: " + String.format("%.2f ms", ten_sec_avgerage_tps));

        // cmd.set("#TenSecondAverageTps.Text", "Ø " + ten_sec_current_tps);

        evt.addEventBinding(CustomUIEventBindingType.Activating, "#CloseButton");


    }

    public void updatePlayerHud(World world) {
        this.live_tps = TpsHelper.getLiveTPS(world);
        this.live_mspt = TpsHelper.getLiveMspt(world);
        this.ten_sec_current_tps = TpsHelper.getMSPTAvg(world, MetricsTime.TEN_SECONDS);
        this.ten_sec_avgerage_tps = TpsHelper.getMSPTAvg(world, MetricsTime.TEN_SECONDS);
        UICommandBuilder cmd = new UICommandBuilder();
        UIEventBuilder evt = new UIEventBuilder();
        updateValues(cmd, evt);
        this.sendUpdate(cmd, evt, false);
    }

    /**
     * Empty EventData - we only need to handle the close button.
     * No fields, just an empty codec.
     */
    public static class CloseEventData {
        public static final BuilderCodec<CloseEventData> CODEC =
                BuilderCodec.builder(CloseEventData.class, CloseEventData::new)
                        .build();
    }
}