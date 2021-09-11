package xyz.nsgw.nsys.commands;

import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.MessageKeys;
import co.aikar.commands.MessageType;
import co.aikar.commands.PaperCommandManager;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.nsgw.nsys.NSys;
import xyz.nsgw.nsys.commands.homes.DelHomeCmd;
import xyz.nsgw.nsys.commands.homes.HomeCmd;
import xyz.nsgw.nsys.commands.homes.HomesCmd;
import xyz.nsgw.nsys.commands.homes.SetHomeCmd;
import xyz.nsgw.nsys.commands.warps.DelWarpCmd;
import xyz.nsgw.nsys.commands.warps.SetWarpCmd;
import xyz.nsgw.nsys.commands.warps.WarpCmd;
import xyz.nsgw.nsys.commands.warps.WarpsCmd;
import xyz.nsgw.nsys.storage.objects.locations.Home;
import xyz.nsgw.nsys.storage.objects.Profile;
import xyz.nsgw.nsys.storage.objects.locations.Warp;

import java.util.stream.Collectors;

public class CommandHandler {

    private final PaperCommandManager manager;

    public CommandHandler(NSys pl) {

        manager = new PaperCommandManager(pl);

        manager.enableUnstableAPI("brigadier");
        //manager.enableUnstableAPI("help");

        manager.getCommandContexts().registerContext(Warp.class, c-> {
            Warp warp = NSys.sql().wrapWarp(c.popFirstArg());
            if(!warp.exists()) {
                throw new InvalidCommandArgument("A warp could not be found.");
            }
            return warp;
        });

        manager.getCommandContexts().registerContext(Home.class, c-> {
            Profile p = NSys.sql().wrapProfile(c.getPlayer().getUniqueId());
            Location loc = p.getHome(c.popFirstArg());
            if(loc == null) {
                throw new InvalidCommandArgument("A home could not be found.");
            }
            return new Home(loc);
        });

        manager.getCommandCompletions().registerCompletion("@warps",c-> NSys.sql().wrapList("warps").getList());

        manager.getCommandCompletions().registerCompletion("@homes",c-> {
            CommandSender sender = c.getSender();
            if (sender instanceof Player) {
                return NSys.sql().wrapProfile(c.getPlayer().getUniqueId()).getHomes().keySet().stream().collect(Collectors.toList());
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

        manager.registerCommand(new WarpCmd().setExceptionHandler((command, registeredCommand, sender, args, t) -> {
            sender.sendMessage(MessageType.ERROR, MessageKeys.ERROR_GENERIC_LOGGED);
            return true;
        }));
        manager.registerCommand(new WarpsCmd(pl));
        manager.registerCommand(new SetWarpCmd(pl));
        manager.registerCommand(new DelWarpCmd(pl));
    }

    public void onDisable() {
        manager.unregisterCommands();
    }
}
