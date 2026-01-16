package de.shiirroo.tps.hud;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.hud.CustomUIHud;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import de.shiirroo.tps.Tps;
import de.shiirroo.tps.TpsHelper;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jline.utils.Log;

import java.util.logging.Logger;

public class TpsHud extends CustomUIHud {

    private static final Logger log = Logger.getLogger("TpsHud");

    public TpsHud(PlayerRef playerRef) {
        super(playerRef);
    }

    @Override
    protected void build(UICommandBuilder builder) {
        builder.append("Hud/TPS.ui");
    }

    @Override
    public void update(boolean clear, @NotNull UICommandBuilder commandBuilder) {
        log.info("Updating Hud");
        Ref<EntityStore> ref = getPlayerRef().getReference();
        if (ref != null){
            Player player = ref.getStore().getComponent(ref, Player.getComponentType());
            if (player == null) {
                log.warning("Player not found");
                return;
            }
            World world = player.getWorld();
            if (world == null) {
                log.warning("World not found for player: " + getPlayerRef().getUsername());
                return;
            }
            double liveTps = TpsHelper.getLiveTPS(world);
            double maxMSPT = TpsHelper.getMaxMSPT(world);
            Message live  = Message.raw("TPS: ");
            Message liveTpsMessage = TpsHelper.colorizeTps(liveTps, world.getTps());
            Message liveMsptMessage = TpsHelper.colorizeMspt(TpsHelper.getLiveMspt(world), maxMSPT);
            Message msptlabel = Message.raw("MSPT: ");


            Message hud = live.insert(liveTpsMessage).insert(TpsHelper.spacer).insert(msptlabel).insert(liveMsptMessage);
            commandBuilder.set("#MyLabel.TextSpans", Message.raw("").insert(hud));

        } else {
            log.warning("EntityStore reference not found for player: " + getPlayerRef().getUsername());
        }
        super.update(clear, commandBuilder);
    }
}