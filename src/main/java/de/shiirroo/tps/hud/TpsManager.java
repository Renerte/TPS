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
import de.shiirroo.tps.config.TPSConfig;
import de.shiirroo.tps.Tps;
import de.shiirroo.tps.helper.TpsHelper;
import de.shiirroo.tps.history.TpsHistory;
import de.shiirroo.tps.history.TpsMetrics;
import de.shiirroo.tps.history.WorldMetrics;
import de.shiirroo.tps.hud.adapter.HudAdapterSelector;
import de.shiirroo.tps.kumo.TPSWebsocket;
import de.shiirroo.tps.kumo.TpsData;
import de.shiirroo.tps.page.TpsGuiPage;
import lombok.Getter;

import java.awt.*;
import java.net.URI;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;

public class TpsManager implements Runnable {

    private final List<UUID> playersWithHud = new ArrayList<>();
    private final List<UUID> playersWithGui = new ArrayList<>();
    private final static String HUD_ID = "TpsHud";
    @Getter
    private final EnumMap<MetricsTime, Integer> metricsUpdateTime = new EnumMap<>(MetricsTime.class);
    @Getter
    private final Config<TPSConfig> settings;
    private long lastWarningTime = 0;
    private static final long WARNING_COOLDOWN_MS = 59 * 1000;
    private TPSWebsocket tpsWebsocket;

    public TpsManager(Config<TPSConfig> settings) {
        this.settings = settings;
        this.initializeKumoWebSocket();
    }


    public void initializeKumoWebSocket() {
        if(settings.get().getKumoConfig().isEnableKumoSupport()) {
            try {
                tpsWebsocket = new TPSWebsocket(URI.create(settings.get().getKumoConfig().getKumoURL()), settings);
                tpsWebsocket.connect();
                Tps.getLog().info("Kumo Websocket initialized");
            } catch (Exception e) {
                Tps.getLog().severe("Failed to initialize Kumo WebSocket: " + e.getMessage());
            }
        }
    }


    public void update() {
        if (settings.get().getMetricsConfig().isEnableMetrics())  updateTpsHistory();
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
        try {
            int secondOfDay = LocalTime.now().toSecondOfDay();
            ZonedDateTime now = ZonedDateTime.now();
            ArrayList<TpsMetrics> newRecords = new ArrayList<>();
            MetricsTime[] metricsTimes = new MetricsTime[MetricsTime.values().length];
            for (int i = 0; i < MetricsTime.values().length; i++) {
                MetricsTime mt = MetricsTime.values()[i];
                int currentIndex = secondOfDay / mt.getSeconds();
                boolean range = checkRange(currentIndex, mt);
                if (!range) metricsTimes[i] = mt;
            }

            if (metricsTimes.length > 0) {
                Universe.get().getWorlds().forEach((worldName, world) -> {
                    TpsMetrics tpsMetrics = new TpsMetrics(worldName, world.getWorldConfig().getUuid(), now);
                    for (MetricsTime time : metricsTimes) {
                        if (time == null) continue;
                        double tps = time.getTps(world);
                        double mspt = time.getMspt(world);
                        WorldMetrics worldMetrics = new WorldMetrics(world, time);
                        TpsHistory.get().addMetrics(world, time, worldMetrics);
                        tpsMetrics.addTpsMspt(time, tps, mspt);
                    }
                    newRecords.add(tpsMetrics);
                });

            }



            TpsData dataDTO = new TpsData(newRecords);
            if (settings.get().getKumoConfig().isEnableKumoSupport() && tpsWebsocket != null && tpsWebsocket.isOpen()) {
                tpsWebsocket.sendTPS(dataDTO);
            }



        } catch (Exception e) {
            Tps.getLog().severe("TPS History update failed: " + e.getMessage());
        }
    }

    private void sendTPSWaring(){
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastWarningTime < WARNING_COOLDOWN_MS) {
            return;
        }
        Universe.get().getWorlds().forEach((worldName, world) -> {
            world.execute(() -> {
                WorldMetrics currentMetrics = new WorldMetrics(world, MetricsTime.TEN_SECONDS);
                if (currentMetrics.getTps() < settings.get().getWarningThreshold()) {
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
                            Message tpsMessage = TpsHelper.colorizeTps(currentMetrics.getTps(), world.getTps());
                            player.sendMessage(first.insert(second).insert(worldMessage).insert(third).insert(tpsMessage));
                        }
                    }
                }
            });
        });
        lastWarningTime = currentTime;
    }

    private boolean checkRange(int currentIndex, MetricsTime mt) {
        Integer last = metricsUpdateTime.getOrDefault(mt, 0);
        boolean isInRange = currentIndex  <= last;
        if (!isInRange) metricsUpdateTime.put(mt, currentIndex);
        return isInRange;
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
