package xyz.nsgw.nsys.commands;

import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.MessageKeys;
import co.aikar.commands.MessageType;
import co.aikar.commands.PaperCommandManager;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.nsgw.nsys.NSys;
import xyz.nsgw.nsys.storage.Home;
import xyz.nsgw.nsys.storage.Profile;

import java.util.stream.Collectors;

public class CommandHandler {

    public CommandHandler(NSys pl, PaperCommandManager manager) {

        manager.getCommandContexts().registerContext(Home.class, c-> {
            Profile p = pl.sql().wrapProfile(c.getPlayer().getUniqueId());
            Location loc = p.getHome(c.popFirstArg());
            if(loc == null) {
                throw new InvalidCommandArgument("A home could not be found.");
            }
            return new Home(loc);
        });

        manager.getCommandCompletions().registerCompletion("homes",c-> {
            CommandSender sender = c.getSender();
            if (sender instanceof Player) {
                return pl.sql().wrapProfile(c.getPlayer().getUniqueId()).getHomes().keySet().stream().collect(Collectors.toList());
            }
            return null;
        });
        manager.registerCommand(new HomeCmd().setExceptionHandler((command, registeredCommand, sender, args, t) -> {
            sender.sendMessage(MessageType.ERROR, MessageKeys.ERROR_GENERIC_LOGGED);
            return true;
        }));
        manager.registerCommand(new HomesCmd(pl));
        manager.registerCommand(new SetHomeCmd(pl));
        manager.registerCommand(new DelHomeCmd(pl));
    }
}
