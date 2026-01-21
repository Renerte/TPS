package de.shiirroo.tps.cmd;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand;
import de.shiirroo.tps.hud.TpsManager;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class TpsMetrics extends AbstractAsyncCommand {

    @Getter
    private final TpsManager tpsManager;

    public TpsMetrics(TpsManager tpsManager) {
        super("metrics", "Update value of Metrics.", false);
        requirePermission("tps.command.tps.metrics");
        this.tpsManager = tpsManager;
    }


    @Override
    protected @NotNull CompletableFuture<Void> executeAsync(@NotNull CommandContext paramCommandContext) {
        var bool =  tpsManager.getSettings().get().isEnableMetrics();
        tpsManager.getSettings().get().setEnableMetrics(!bool);
        paramCommandContext.sendMessage(Message.raw("TPS metrics set to: " + !bool));
        return tpsManager.getSettings().save();
    }
}
