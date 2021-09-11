package xyz.nsgw.nsys.commands;

import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.PaperCommandManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.nsgw.nsys.NSys;
import xyz.nsgw.nsys.commands.homes.HomeCmd;
import xyz.nsgw.nsys.commands.homes.HomesCmd;
import xyz.nsgw.nsys.commands.warps.WarpCmd;
import xyz.nsgw.nsys.commands.warps.WarpsCmd;
import xyz.nsgw.nsys.storage.objects.OnlineHome;
import xyz.nsgw.nsys.storage.objects.locations.Home;
import xyz.nsgw.nsys.storage.objects.Profile;
import xyz.nsgw.nsys.storage.objects.locations.Warp;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class CommandHandler {

    private final PaperCommandManager manager;

    public CommandHandler(NSys pl) {

        manager = new PaperCommandManager(pl);

        manager.enableUnstableAPI("brigadier");
        manager.enableUnstableAPI("help");

        manager.getCommandContexts().registerContext(OnlineHome.class, c-> {
            String[] arg = c.popFirstArg().split(":");
            if(arg.length < 2) throw new InvalidCommandArgument("Invalid argument.");
            if(NSys.sql().wrapMap("players").hasVal(arg[0])) {
                Profile p = NSys.sql().wrapProfile(UUID.fromString(NSys.sql().wrapMap("players")
                                .getFromVal(arg[0])));
                Home h = p.getHome(arg[1]);
                if(h == null) {
                    throw new InvalidCommandArgument("No home found.");
                }
                return new OnlineHome(h);
            }
            return null;
        });

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

        manager.getCommandCompletions().registerCompletion("@warps",c-> NSys.sql().wrapList("warps")
                .getList());

        manager.getCommandCompletions().registerCompletion("@homes",c-> {
            CommandSender sender = c.getSender();
            if (sender instanceof Player) {
                return new ArrayList<>(NSys.sql().wrapProfile(c.getPlayer().getUniqueId()).getHomes().keySet());
            }
            return null;
        });

        manager.getCommandCompletions().registerCompletion("@onlinehomes", c-> {
            List<String> res = new ArrayList<>();
            for(Player p : Bukkit.getOnlinePlayers()) {
                Profile prf = NSys.sql().wrapProfile(p.getUniqueId());
                res.addAll(prf.getHomeNames().stream().map(h->p.getName()+":"+h).collect(Collectors.toList()));
            }
            return res;
        });

        manager.setDefaultExceptionHandler((command, registeredCommand, sender, args, t) -> {
            NSys.log().warning("Error occurred while executing command " + command.getName());
            return false;// mark as unhandled, sender will see default message
        });

        manager.registerCommand(new MainCmd());

        manager.registerCommand(new ProfileCmd(pl));

        manager.registerCommand(new AfkCmd());

        manager.registerCommand(new HomeCmd());
        manager.registerCommand(new HomesCmd(pl));

        manager.registerCommand(new WarpCmd());
        manager.registerCommand(new WarpsCmd(pl));
    }

    public void onDisable() {
        manager.unregisterCommands();
    }
}
