package de.shiirroo.tps.hud;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.hud.CustomUIHud;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import de.shiirroo.tps.Tps;
import de.shiirroo.tps.helper.TpsHelper;

public class TpsHud extends CustomUIHud {
    public final static String HUD_ID = "TpsHud";

    public TpsHud(PlayerRef playerRef) {
        super(playerRef, HUD_ID);
    }

    @Override
    protected void build(UICommandBuilder builder) {
        builder.append("Hud/TPS.ui");
        Ref<EntityStore> ref = getPlayerRef().getReference();
        if (ref != null) {
            Player player = ref.getStore().getComponent(ref, Player.getComponentType());
            setHud(player, builder);

        } else {
            Tps.getLog().warning("EntityStore reference not found for player: " + getPlayerRef().getUsername());
        }
    }

    public void updatePlayerHud(Player player) {
        var builder = new UICommandBuilder();
        setHud(player, builder);
        this.update(false, builder);
    }

    private void setHud(Player player, UICommandBuilder commandBuilder) {
        if (player == null) {
            Tps.getLog().warning("Player not found");
            return;
        }
        World world = player.getWorld();
        if (world == null) {
            Tps.getLog().warning("World not found for player: " + getPlayerRef().getUsername());
            return;
        }
        double liveTps = TpsHelper.getLiveTPS(world);
        double maxMSPT = TpsHelper.getMaxMSPT(world);
        Message live  = Message.raw("TPS: ");
        Message liveTpsMessage = TpsHelper.colorizeTps(liveTps, world.getTps());
        Message liveMsptMessage = TpsHelper.colorizeMspt(TpsHelper.getLiveMspt(world), maxMSPT);
        Message msptlabel = Message.raw("MSPT: ");

        Message hud = live.insert(liveTpsMessage).insert(TpsHelper.spacer).insert(msptlabel).insert(liveMsptMessage);
        commandBuilder.set("#TPSLabel.TextSpans", Message.raw("").insert(hud));
    }
}