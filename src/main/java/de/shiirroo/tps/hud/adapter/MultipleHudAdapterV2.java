package de.shiirroo.tps.hud.adapter;

import com.buuz135.mhud.MultipleCustomUIHud;
import com.buuz135.mhud.MultipleHUD;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.hud.CustomUIHud;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import de.shiirroo.tps.Tps;
import de.shiirroo.tps.hud.TpsHud;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class MultipleHudAdapterV2 implements HudAdapter {
    public void setCustomHud (@NonNullDecl Player player, @NonNullDecl PlayerRef playerRef, @NonNullDecl String hudIdentifier, @NonNullDecl CustomUIHud hud) {
        MultipleHUD.getInstance().setCustomHud(player, playerRef, hudIdentifier, hud);
    }

    @Override
    public void updatePlayerHud(@NotNull Player player, String hudIdentifier) {
        var hub = player.getHudManager().getCustomHud();
        if (hub instanceof MultipleCustomUIHud multipleCustomUIHud) {
            try {
                var field = multipleCustomUIHud.getClass().getDeclaredField("customHuds");
                field.setAccessible(true);
                HashMap<String, CustomUIHud> map = (HashMap<String, CustomUIHud>) field.get(multipleCustomUIHud);
                CustomUIHud var = map.get(hudIdentifier);
                if (var != null) {
                    if (var instanceof TpsHud tpsHud) {
                        tpsHud.updatePlayerHud(player);
                    }
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                Tps.getLog().severe("Failed to update player HUD: " + e.getMessage());
            }

        }
    }
}
