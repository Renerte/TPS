package de.shiirroo.tps.cmd;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand;
import de.shiirroo.tps.hud.TpsManager;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class TpsReload extends AbstractAsyncCommand {

    @Getter
    private final TpsManager tpsManager;

    public TpsReload(TpsManager tpsManager) {
        super("reload", "Reload TPS Config File.", false);
        requirePermission("tps.command.tps.reload");
        this.tpsManager = tpsManager;
    }


    @Override
    protected @NotNull CompletableFuture<Void> executeAsync(@NotNull CommandContext paramCommandContext) {
        var future = tpsManager.getSettings().load();
        future.thenAccept(response -> {
            paramCommandContext.sendMessage(Message.raw("TPS config reloaded"));

        });
        return CompletableFuture.completedFuture(null);
    }
}
