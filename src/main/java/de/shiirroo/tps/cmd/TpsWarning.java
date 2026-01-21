package de.shiirroo.tps.cmd;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand;
import de.shiirroo.tps.hud.TpsManager;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class TpsWarning extends AbstractAsyncCommand {

    @Getter
    private final TpsManager tpsManager;

    public TpsWarning(TpsManager tpsManager) {
        super("warning", "Update value of warning", false);
        requirePermission("tps.command.tps.warning");
        this.tpsManager = tpsManager;
    }


    @Override
    protected @NotNull CompletableFuture<Void> executeAsync(@NotNull CommandContext paramCommandContext) {
        var bool =  tpsManager.getSettings().get().isEnableTPSWarning();
        tpsManager.getSettings().get().setEnableTPSWarning(!bool);
        paramCommandContext.sendMessage(Message.raw("TPS Warning set to: " + !bool));
        return tpsManager.getSettings().save();
    }
}
