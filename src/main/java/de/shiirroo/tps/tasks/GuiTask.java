package de.shiirroo.tps.tasks;

import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import de.shiirroo.tps.page.TpsGuiPage;
import lombok.Getter;

import java.util.HashSet;
import java.util.UUID;

public class GuiTask extends AbPlayerTask {

    @Getter
    private final HashSet<UUID> effectPlayers = new HashSet<>();
    @Getter
    private final Tasks task = Tasks.GUI;

    @Override
    public void run() {
        updatePlayers(this::updateGui);
    }

    private void updateGui(Player player, World world) {
        var page = player.getPageManager().getCustomPage();
        if (page instanceof TpsGuiPage guiPage) {
            guiPage.updatePlayerHud(world);
        }
    }


    @Override
    public boolean addEffectPlayer(Player player, PlayerRef playerRef) {
        if (effectPlayers.contains(playerRef.getUuid())) return false;
        return effectPlayers.add(playerRef.getUuid());
    }

    @Override
    public boolean removeEffectPlayer(Player player, PlayerRef playerRef) {
        if (!effectPlayers.contains(playerRef.getUuid())) return false;
        return effectPlayers.remove(playerRef.getUuid());
    }

    @Override
    public boolean toggleEffectPlayer(Player player, PlayerRef playerRef) {
        return false;
    }
}
