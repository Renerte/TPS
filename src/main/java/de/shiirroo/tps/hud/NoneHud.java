package de.shiirroo.tps.hud;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.hud.CustomUIHud;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import de.shiirroo.tps.TpsHelper;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

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