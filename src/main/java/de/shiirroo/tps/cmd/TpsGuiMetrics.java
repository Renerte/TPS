package de.shiirroo.tps.cmd;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand;
import de.shiirroo.tps.manager.TpsManager;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class TpsGuiMetrics extends AbstractAsyncCommand {

    @Getter
    private final TpsManager tpsManager;

    public TpsGuiMetrics(TpsManager tpsManager) {
        super("showMetrics", "Send Metrics to console", false);
        requirePermission("tps.command.tps.showMetrics");
        this.tpsManager = tpsManager;
    }

    private void sendTpsHistoryToConsole() {
        tpsManager.getSettings().load();

    }

    @Override
    protected @NotNull CompletableFuture<Void> executeAsync(@NotNull CommandContext paramCommandContext) {
        if (!paramCommandContext.sender().hasPermission("tps.command.tps.showMetrics")) {
            paramCommandContext.sender().sendMessage(Message.parse("You don't have permission to use this command."));
            return CompletableFuture.completedFuture(null);
        }
        return CompletableFuture.runAsync(this::sendTpsHistoryToConsole);
    }
}
