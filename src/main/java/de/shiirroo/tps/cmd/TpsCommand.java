package de.shiirroo.tps.cmd;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.universe.Universe;
import de.shiirroo.tps.MetricsTime;
import de.shiirroo.tps.Tps;
import de.shiirroo.tps.TpsHelper;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class TpsCommand extends CommandBase {

    public TpsCommand() {
        super("tps", "Shows the TPS of all worlds.", false);
        requirePermission("tps.command.tps");
        addSubCommand(new TpsShow(Tps.getInstance().getTpsManager()));
    }

    @Override
    protected void executeSync(@NotNull CommandContext context) {
        Universe universe = Universe.get();
        if (universe.getWorlds().isEmpty()) {
            context.sendMessage(Message.raw("No worlds found.").color(Color.RED));
            return;
        }

        Message label = Message.raw("========== Worlds – TPS / MSPT ==========").color(Color.orange);
        context.sendMessage(label);

        universe.getWorlds().forEach((worldName, world) -> {
            double liveTps = TpsHelper.getLiveTPS(world);
            double maxMSPT = TpsHelper.getMaxMSPT(world);
            //World Name
            Message worldLabel = Message.raw("World: ");
            Message worldNameMessage = Message.raw(worldName).color(Color.ORANGE);
            //Target TPS / MSPT
            Message target = Message.raw("    Target TPS: ");
            Message targetTps = Message.raw(String.format("%.2f ", (double) world.getTps())).color(Color.green);
            Message tpsLabel = Message.raw("| Max MSPT: ");


            Message targetMspt = Message.raw(String.format("%.2f ", TpsHelper.getMaxMSPT(world))).color(Color.red);
            Message msptLabel = Message.raw("ms");
            //Live TPS / MSPT
            Message live  = Message.raw("             Live => ");
            Message liveTpsMessage = TpsHelper.colorizeTps(liveTps, world.getTps());
            Message liveMsptMessage = TpsHelper.colorizeMspt(TpsHelper.getLiveMspt(world), maxMSPT);


            context.sendMessage(worldLabel.insert(worldNameMessage));
            context.sendMessage(target.insert(targetTps).insert(tpsLabel).insert(targetMspt).insert(msptLabel));
            context.sendMessage(live.insert(liveTpsMessage).insert(TpsHelper.spacer).insert(liveMsptMessage));
            context.sendMessage(TpsHelper.getAvgMessage(MetricsTime.TEN_SECONDS, world, maxMSPT));
            context.sendMessage(TpsHelper.getAvgMessage(MetricsTime.ONE_MINUTE, world, maxMSPT));
            context.sendMessage(TpsHelper.getAvgMessage(MetricsTime.FIVE_MINUTES, world, maxMSPT));

        });
    }

}
