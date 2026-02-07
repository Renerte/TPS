package de.shiirroo.tps.hud.adapter;

import com.hypixel.hytale.common.plugin.PluginIdentifier;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.hud.CustomUIHud;
import com.hypixel.hytale.server.core.plugin.PluginBase;
import com.hypixel.hytale.server.core.plugin.PluginManager;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import de.shiirroo.tps.Tps;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.jetbrains.annotations.NotNull;

public class HudAdapterSelector implements HudAdapter {

    private final HudAdapter hudAdapter;
    private static final HudAdapterSelector instance = new HudAdapterSelector();

    public HudAdapterSelector() {
        PluginBase plugin = PluginManager.get().getPlugin(PluginIdentifier.fromString("Buuz135:MultipleHUD"));
        if (plugin == null || !plugin.isEnabled()) {
            hudAdapter = new DefaultHudAdapter();
            Tps.getLog().severe("MultipleHUD plugin not found. Mod won't be compatible with other HUD mods.");
        } else {
            long major = plugin.getManifest().getVersion().getMajor();
            long minor = plugin.getManifest().getVersion().getMinor();
            long patch = plugin.getManifest().getVersion().getPatch();
            if (patch <= 3) {
                Tps.getLog().info("MultipleHUD plugin version 1.0.3 or lower found. Mod is compatible with other HUD mods, using MultipleHudAdapterV1.");
                hudAdapter = new MultipleHudAdapterV1();
            } else {
                Tps.getLog().info("MultipleHUD plugin version higher than 1.0.3 found. Mod is compatible with other HUD mods, using MultipleHudAdapterV2.");
                hudAdapter = new MultipleHudAdapterV2();
            }
        }
    }

    public void setCustomHud(@NonNullDecl Player player, @NonNullDecl PlayerRef playerRef, @NonNullDecl String hudIdentifier, @NonNullDecl CustomUIHud hud) {
        hudAdapter.setCustomHud(player, playerRef, hudIdentifier, hud);
    }

    @Override
    public void updatePlayerHud(@NotNull Player player, String hudIdentifier) {
        hudAdapter.updatePlayerHud(player, hudIdentifier);
    }

    public static HudAdapterSelector get() {
        return instance;
    }
}
