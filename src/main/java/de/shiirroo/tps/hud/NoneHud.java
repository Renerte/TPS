package de.shiirroo.tps.hud;

import com.hypixel.hytale.server.core.entity.entities.player.hud.CustomUIHud;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import org.jetbrains.annotations.NotNull;

public class NoneHud extends CustomUIHud {

    public NoneHud(PlayerRef playerRef) {
        super(playerRef);
    }

    @Override
    protected void build(UICommandBuilder builder) {
        builder.append("Hud/NONE.ui");
    }

    @Override
    public void update(boolean clear, @NotNull UICommandBuilder commandBuilder) {
        super.update(clear, commandBuilder);
    }
}