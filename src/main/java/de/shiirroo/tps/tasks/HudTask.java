package de.shiirroo.tps.tasks;

import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import de.shiirroo.tps.hud.TpsHud;
import lombok.Getter;

import java.util.HashSet;
import java.util.UUID;

import static de.shiirroo.tps.hud.TpsHud.HUD_ID;

public class HudTask extends AbPlayerTask {

    @Getter
    private final HashSet<UUID> effectPlayers = new HashSet<>();
    @Getter
    private final Tasks task = Tasks.HUD;

    @Override
    public void run() {
        updatePlayers(this::updateHud);
    }

    private void updateHud(Player player, World world) {
        var hud = player.getHudManager().getCustomHud(HUD_ID);
        if (hud instanceof TpsHud tpsHud) {
            tpsHud.updatePlayerHud(player);
        }
    }


    @Override
    public boolean addEffectPlayer(Player player, PlayerRef playerRef) {
        if (effectPlayers.contains(playerRef.getUuid())) return false;
        player.getHudManager().addCustomHud(playerRef, new TpsHud(playerRef));
        return effectPlayers.add(playerRef.getUuid());
    }

    @Override
    public boolean removeEffectPlayer(Player player, PlayerRef playerRef) {
        if (!effectPlayers.contains(playerRef.getUuid())) return false;
        if (player != null) {
            player.getHudManager().removeCustomHud(playerRef, HUD_ID);
        }
        effectPlayers.remove(playerRef.getUuid());
        return true;
    }

    public void removeEffectPlayer(PlayerRef playerRef) {
        if (!effectPlayers.contains(playerRef.getUuid())) return;
        effectPlayers.remove(playerRef.getUuid());
    }

    @Override
    public boolean toggleEffectPlayer(Player player, PlayerRef playerRef) {
        if (effectPlayers.contains(playerRef.getUuid())) {
            removeEffectPlayer(player, playerRef);
            return false;
        } else {
            addEffectPlayer(player, playerRef);
            return true;
        }
    }

}




