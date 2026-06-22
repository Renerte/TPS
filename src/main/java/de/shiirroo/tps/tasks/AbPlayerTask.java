package de.shiirroo.tps.tasks;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import java.util.HashSet;
import java.util.UUID;
import java.util.function.BiConsumer;

public abstract class AbPlayerTask implements TpsTaskRunnable {

    abstract HashSet<UUID> getEffectPlayers();

    protected void updatePlayers(BiConsumer<Player, World> action) {
        HashSet<UUID> players = getEffectPlayers();
        if (players.isEmpty()) return;
        for (UUID uuid : players) {
            PlayerRef ref = Universe.get().getPlayer(uuid);
            if (ref == null || ref.getWorldUuid() == null) continue;
            World world = Universe.get().getWorld(ref.getWorldUuid());
            if (world == null) continue;
            world.execute(() -> {
                Ref<EntityStore> storeRef = ref.getReference();
                if (storeRef == null) return;
                Player player = storeRef.getStore().getComponent(storeRef, Player.getComponentType());
                if (player == null) return;
                action.accept(player, world);
            });
        }
    }

    public abstract boolean addEffectPlayer(Player player, PlayerRef playerRef);

    public abstract boolean removeEffectPlayer(Player player, PlayerRef playerRef);

    public abstract boolean toggleEffectPlayer(Player player, PlayerRef playerRef);


}
