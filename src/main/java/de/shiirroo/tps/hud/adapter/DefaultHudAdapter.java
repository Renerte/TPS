package de.shiirroo.tps.hud.adapter;

import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.hud.CustomUIHud;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import de.shiirroo.tps.hud.TpsHud;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.jetbrains.annotations.NotNull;

class DefaultHudAdapter implements HudAdapter {
    public void setCustomHud (@NonNullDecl Player player, @NonNullDecl PlayerRef playerRef, @NonNullDecl String hudIdentifier, @NonNullDecl CustomUIHud hud) {
        player.getHudManager().addCustomHud(playerRef, hud);
    }

    @Override
    public void updatePlayerHud(@NotNull Player player,String hudIdentifier) {
        var hud = player.getHudManager().getCustomHud(hudIdentifier);
        if (hud instanceof TpsHud tpsHud) {
            tpsHud.updatePlayerHud(player);
        }
    }
}
