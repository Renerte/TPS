package de.shiirroo.tps.tasks;

import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import de.shiirroo.tps.hud.NoneHud;
import de.shiirroo.tps.hud.TpsHud;
import de.shiirroo.tps.hud.adapter.HudAdapterSelector;
import lombok.Getter;

import java.util.HashSet;
import java.util.UUID;

public class HudTask extends AbPlayerTask {

    @Getter
    private final static String HUD_ID = "TpsHud";
    @Getter
    private final HashSet<UUID> effectPlayers = new HashSet<>();
    @Getter
    private final Tasks task = Tasks.HUD;

    @Override
    public void run() {
        updatePlayers(this::updateHud);
    }
    private void updateHud(Player player, World world) {
        HudAdapterSelector.get().updatePlayerHud(player, HUD_ID);
    }


    @Override
    public boolean addEffectPlayer(Player player, PlayerRef playerRef) {
        if (effectPlayers.contains(playerRef.getUuid())) return false;
        HudAdapterSelector.get().setCustomHud(player, playerRef, HUD_ID, new TpsHud(playerRef));
        return effectPlayers.add(playerRef.getUuid());
    }

    @Override
    public boolean removeEffectPlayer(Player player, PlayerRef playerRef) {
        if (!effectPlayers.contains(playerRef.getUuid())) return false;
        if (player != null) {
            HudAdapterSelector.get().setCustomHud(player, playerRef, HUD_ID, new NoneHud(playerRef));
        }
        effectPlayers.remove(playerRef.getUuid());
        return true;
    }

    public boolean removeEffectPlayer(PlayerRef playerRef) {
        if (!effectPlayers.contains(playerRef.getUuid())) return false;
        effectPlayers.remove(playerRef.getUuid());
        return true;
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




