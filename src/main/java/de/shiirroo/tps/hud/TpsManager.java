package de.shiirroo.tps.hud;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.hud.CustomUIHud;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import de.shiirroo.tps.Tps;
import de.shiirroo.tps.page.TpsGuiPage;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TpsManager implements Runnable {


    private final List<UUID> playersWithHud = new ArrayList<>();
    private final ConcurrentHashMap<UUID, TpsGuiPage> playersWithGui = new ConcurrentHashMap<>();
    private final static String HUD_ID = "TpsHud";



    public void update() {
        updateGuiHud();
        updatePlayerHud();
    }

    private void updatePlayerHud() {
        if (playersWithHud.isEmpty()) return;
        for (UUID uuid : playersWithHud) {
            PlayerRef playerRef = Universe.get().getPlayer(uuid);
            if (playerRef == null) continue;
            if (playerRef.getWorldUuid() == null) continue;
            World world = Universe.get().getWorld(playerRef.getWorldUuid());
            if (world == null) continue;
            world.execute(() -> {
                Ref<EntityStore> ref = playerRef.getReference();
                if (ref != null) {
                    Player player = ref.getStore().getComponent(ref, Player.getComponentType());
                    if (player != null) {
                        if (player.getHudManager().getCustomHud() == null) {
                            return;
                        }
                        CustomUIHud hud = getHud(player, HUD_ID);
                        if (hud != null) {
                            if (hud instanceof TpsHud tpsHud) {
                                    tpsHud.updatePlayerHud(player);
                                }
                            }
                    }
                }
            });
        }
    }
    private void updateGuiHud() {
        if (playersWithGui.isEmpty()) return;
        for (UUID uuid : playersWithGui.keySet()) {
            PlayerRef playerRef = Universe.get().getPlayer(uuid);
            if (playerRef == null) continue;
            if (playerRef.getWorldUuid() == null) continue;
            World world = Universe.get().getWorld(playerRef.getWorldUuid());
            if (world == null) continue;
            world.execute(() -> {
                Ref<EntityStore> ref = playerRef.getReference();
                if (ref != null) {
                    Player player = ref.getStore().getComponent(ref, Player.getComponentType());
                    if (player != null) {
                        TpsGuiPage currentPage = playersWithGui.get(uuid);
                        currentPage.updatePlayerHud(world);
                    }
                }
            });
        }

    }

    private void updateTpsHistory() {

    }

    public void addGui(Player player, PlayerRef playerRef, TpsGuiPage gui) {
        if (playersWithGui.get(playerRef.getUuid()) != null) return;
        playersWithGui.put(playerRef.getUuid(), gui);
        Tps.getLog().info("Player " + playerRef.getUuid() + " added to history");
    }

    public void removeGui(Player player, PlayerRef playerRef) {
        if (playersWithGui.get(playerRef.getUuid()) == null) return;
        playersWithGui.remove(playerRef.getUuid());
        Tps.getLog().info("Player " + playerRef.getUuid() + " removed from history");
    }



    public void setupPlayer(Player player, PlayerRef playerRef) {
        if (playersWithHud.contains(playerRef.getUuid())) return;
        if (!setHud(player, playerRef, HUD_ID, new TpsHud(playerRef))) {
            Tps.getLog().severe("Failed to set up HUD for player: " + playerRef.getUsername());
            return;
        }
        playersWithHud.add(playerRef.getUuid());
    }

    public void removePlayerRef(PlayerRef playerRef) {
        if (!playersWithHud.contains(playerRef.getUuid())) return;
        playersWithHud.remove(playerRef.getUuid());
    }



    public boolean removePlayerHud(Player player, PlayerRef playerRef) {
        if (!playersWithHud.contains(playerRef.getUuid())) return false;
        if (!setHud(player, playerRef, HUD_ID, new NoneHud(playerRef))) {
            Tps.getLog().severe("Failed to remove HUD for player: " + playerRef.getUsername());
            return false;
        }
        playersWithHud.remove(playerRef.getUuid());
        return true;
    }

    public boolean setHud(Player player, PlayerRef playerRef, String hud_id, CustomUIHud hud) {
        try {
            getMultiHudClass().getMethod("setCustomHud", Player.class, PlayerRef.class, String.class, CustomUIHud.class)
                    .invoke(getMultiHudInstance(), player, playerRef, hud_id, hud);
        } catch (ClassNotFoundException e) {
            player.getHudManager().setCustomHud(playerRef, hud);
        } catch (Exception e) {
            Tps.getLog().severe("Error removing player HUD: " + e.getMessage());
            return false;
        }
        return true;
    }

    public Class<?> getMultiHudClass() throws ClassNotFoundException {
        return Class.forName("com.buuz135.mhud.MultipleHUD");
    }

    public Class<?> getMultipleCustomUIHudClass() throws ClassNotFoundException {
        return Class.forName("com.buuz135.mhud.MultipleCustomUIHud");
    }


    public CustomUIHud getHud(Player player, String hud_id) {
        var currentCustomHud = player.getHudManager().getCustomHud();
        if (currentCustomHud == null) return null;
        if (currentCustomHud instanceof TpsHud) return currentCustomHud;
        try {
            if (getMultipleCustomUIHudClass().isAssignableFrom(currentCustomHud.getClass())){
                HashMap<String, CustomUIHud> customHuds = getCustomHuds(currentCustomHud);
                return customHuds.get(hud_id);
            } else {
                return currentCustomHud;
            }
        } catch (ClassNotFoundException e) {
            return null;
        }
    }


    public Object getMultiHudInstance() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class<?> multiHudClass = Class.forName("com.buuz135.mhud.MultipleHUD");
        return multiHudClass.getMethod("getInstance").invoke(null);
    }

    public HashMap<String, CustomUIHud> getCustomHuds(CustomUIHud customUIHud) {
        try {
            Class<?> multipleCustomUIHudClass = getMultipleCustomUIHudClass();
            Method method = multipleCustomUIHudClass.getMethod("getCustomHuds");
            method.setAccessible(true);
            return (HashMap<String, CustomUIHud>) method.invoke(customUIHud);
        } catch (Exception e) {
            Tps.getLog().severe("Error getting custom HUDs: " + e.getMessage());
            return new HashMap<>();
        }
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
