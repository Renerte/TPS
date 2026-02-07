package de.shiirroo.tps.hud.adapter;

import com.buuz135.mhud.MultipleCustomUIHud;
import com.buuz135.mhud.MultipleHUD;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.hud.CustomUIHud;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import de.shiirroo.tps.hud.TpsHud;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.jetbrains.annotations.NotNull;

public class MultipleHudAdapterV1 implements HudAdapter {
    public void setCustomHud (@NonNullDecl Player player, @NonNullDecl PlayerRef playerRef, @NonNullDecl String hudIdentifier, @NonNullDecl CustomUIHud hud) {
        MultipleHUD.getInstance().setCustomHud(player, playerRef, hudIdentifier, hud);
    }

    @Override
    public void updatePlayerHud(@NotNull Player player, String hudIdentifier) {
        var hub = player.getHudManager().getCustomHud();
        if (hub instanceof MultipleCustomUIHud multipleCustomUIHud) {
            CustomUIHud var = multipleCustomUIHud.getCustomHuds().get(hudIdentifier);
            if (var != null) {
                if (var instanceof TpsHud tpsHud) {
                    tpsHud.updatePlayerHud(player);
                }
            }
        }
    }
}
