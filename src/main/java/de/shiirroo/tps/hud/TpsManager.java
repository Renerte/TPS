package de.shiirroo.tps.hud;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.Config;
import de.shiirroo.tps.MetricsTime;
import de.shiirroo.tps.TPSConfig;
import de.shiirroo.tps.TpsHelper;
import de.shiirroo.tps.history.TpsTimeRange;
import de.shiirroo.tps.history.TpsWorldHistory;
import de.shiirroo.tps.hud.adapter.HudAdapterSelector;
import de.shiirroo.tps.page.TpsGuiPage;
import lombok.Getter;

import java.awt.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

public class TpsManager implements Runnable {

    private final List<UUID> playersWithHud = new ArrayList<>();
    private final List<UUID> playersWithGui = new ArrayList<>();
    private final static String HUD_ID = "TpsHud";
    @Getter
    private final ConcurrentHashMap<String, TpsWorldHistory> history = new ConcurrentHashMap<>();
    private final EnumMap<MetricsTime, Integer> index = new EnumMap<>(MetricsTime.class);
    @Getter
    private final Config<TPSConfig> settings;
    private long lastWarningTime = 0;
    private static long WARNING_COOLDOWN_MS = 59 * 1000;

    public TpsManager(Config<TPSConfig> settings) {
        this.settings = settings;
    }

    public void update() {
        if (settings.get().isEnableMetrics())  updateTpsHistory();
        if (settings.get().isEnableTPSWarning()) sendTPSWaring();
        updatePlayers(playersWithHud, this::updateHud);
        updatePlayers(playersWithGui, this::updateGui);
    }

    private void updatePlayers(List<UUID> players, BiConsumer<Player, World> action) {
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

    private void updateHud(Player player, World world) {
        HudAdapterSelector.get().updatePlayerHud(player, HUD_ID);
    }

    private void updateGui(Player player, World world) {
        var page = player.getPageManager().getCustomPage();
        if (page instanceof TpsGuiPage guiPage) {
            guiPage.updatePlayerHud(world);
        }
    }

    private void updateTpsHistory() {
        int secondOfDay = LocalTime.now().toSecondOfDay();
        checkRange(secondOfDay, MetricsTime.TEN_SECONDS);
        checkRange(secondOfDay, MetricsTime.ONE_MINUTE);
        checkRange(secondOfDay, MetricsTime.FIVE_MINUTES);
    }

    private void sendTPSWaring(){
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastWarningTime < WARNING_COOLDOWN_MS) {
            return;
        }
        Universe.get().getWorlds().forEach((worldName, world) -> {
            world.execute(() -> {
                double tps = MetricsTime.ONE_MINUTE.getTps(world);
                if (tps < settings.get().getWarningThreshold()) {
                    for (PlayerRef playerRef : world.getPlayerRefs()) {
                        Ref<EntityStore> ref = playerRef.getReference();
                        if (ref == null) continue;
                        Player player = ref.getStore().getComponent(ref, Player.getComponentType());
                        if (player == null) continue;
                        if (player.hasPermission("*") || player.hasPermission("tps.command.tps.warning")){
                            Message first = Message.raw("TPS Warning: ").color(Color.getHSBColor(0, 0.75f, 0.90f));
                            Message second = Message.raw("The TPS of world ").color(Color.LIGHT_GRAY);
                            Message worldMessage = Message.raw(worldName).color(Color.ORANGE);
                            Message third = Message.raw(" was below the threshold! The Last 1 minute average TPS is: ").color(Color.LIGHT_GRAY);
                            Message tpsMessage = TpsHelper.colorizeTps(tps, world.getTps());
                            player.sendMessage(first.insert(second).insert(worldMessage).insert(third).insert(tpsMessage));
                        }
                    }
                }
            });
        });
        lastWarningTime = currentTime;
    }

    private void checkRange(int secondOfDay, MetricsTime mt) {
        int currentIndex = secondOfDay / mt.getSeconds();

        Integer last = index.get(mt);
        if (last != null && currentIndex <= last) return;
        if (last != null) {
            int prevIndex = last;
            TpsTimeRange range = new TpsTimeRange(prevIndex * mt.getSeconds(), mt.getSeconds());
            addRecordForAllWorlds(mt, range);
        }
        index.put(mt, currentIndex);
    }

    private void addRecordForAllWorlds(MetricsTime mt, TpsTimeRange range) {
        Universe.get().getWorlds().forEach((worldName, world) -> {
            double tps  = mt.getTps(world);
            double mspt = mt.getMspt(world);
            var history = this.history.computeIfAbsent(worldName, TpsWorldHistory::new);
            history.addTpsRecord(mt, range, tps, mspt);
        });
    }


    public void addGui(PlayerRef playerRef) {
        if (playersWithGui.contains(playerRef.getUuid())) return;
        playersWithGui.add(playerRef.getUuid());
    }

    public void removeGui(PlayerRef playerRef) {
        if (!playersWithGui.contains(playerRef.getUuid())) return;
        playersWithGui.remove(playerRef.getUuid());
    }

    public void setupPlayer(Player player, PlayerRef playerRef) {
        if (playersWithHud.contains(playerRef.getUuid())) return;
        HudAdapterSelector.get().setCustomHud(player, playerRef, HUD_ID, new TpsHud(playerRef));
        playersWithHud.add(playerRef.getUuid());
    }

    public void removePlayerRef(PlayerRef playerRef) {
        if (!playersWithHud.contains(playerRef.getUuid())) return;
        playersWithHud.remove(playerRef.getUuid());
    }

    public boolean removePlayerHud(Player player, PlayerRef playerRef) {
        if (!playersWithHud.contains(playerRef.getUuid())) return false;
        HudAdapterSelector.get().setCustomHud(player, playerRef, HUD_ID, new NoneHud(playerRef));
        playersWithHud.remove(playerRef.getUuid());
        return true;
    }

    public boolean toggleHud(Player player, PlayerRef playerRef) {
        if (playersWithHud.contains(playerRef.getUuid())) {
            removePlayerHud(player, playerRef);
            return false;
        } else {
            setupPlayer(player, playerRef);
            return true;
        }
    }

    @Override
    public void run() {
        update();
    }
}
