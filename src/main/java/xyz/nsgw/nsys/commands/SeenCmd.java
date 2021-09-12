package xyz.nsgw.nsys.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import xyz.nsgw.nsys.utils.DisplayUtils;

@CommandAlias("seen")
public class SeenCmd extends BaseCommand {
    @Subcommand("player|p")
    @CommandCompletion("@globalplayers")
    public void onSeen(CommandSender s, OfflinePlayer p) {
        s.sendMessage(MiniMessage.get().parse("<aqua>Last seen "+ DisplayUtils.time(System.currentTimeMillis() - p.getLastSeen()) +" ago."));
    }
}
