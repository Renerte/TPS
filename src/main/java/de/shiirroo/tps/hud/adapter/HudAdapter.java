package de.shiirroo.tps.hud.adapter;

import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.hud.CustomUIHud;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public interface HudAdapter {
    void setCustomHud (@NonNullDecl Player player, @NonNullDecl PlayerRef playerRef, @NonNullDecl String hudIdentifier, @NonNullDecl CustomUIHud hud);
    void updatePlayerHud(@NonNullDecl Player player,String hudIdentifier);
}