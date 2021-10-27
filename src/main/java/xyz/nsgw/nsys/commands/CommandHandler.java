/*
 * Copyright (c) Nicholas Williams 2021.
 */

package xyz.nsgw.nsys.commands;

import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.PaperCommandManager;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.nsgw.nsys.NSys;
import xyz.nsgw.nsys.commands.homes.HomesCmd;
import xyz.nsgw.nsys.commands.warps.WarpsCmd;
import xyz.nsgw.nsys.config.settings.GeneralSettings;
import xyz.nsgw.nsys.storage.objects.OnlineHome;
import xyz.nsgw.nsys.storage.objects.Profile;
import xyz.nsgw.nsys.storage.objects.locations.Home;
import xyz.nsgw.nsys.storage.objects.locations.Warp;
import xyz.nsgw.nsys.utils.EconUtils;

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
                                .getKeyFromVal(arg[0])));
                Home h = p.getHome(arg[1]);
                if(h == null) {
                    throw new InvalidCommandArgument("No home found.");
                }
                return new OnlineHome(h);
            }
            return null;
        });

        manager.getCommandContexts().registerContext(Profile.class, c-> {
            UUID uuid = UUID.fromString(NSys.sql().wrapMap("players").getKeyFromVal(c.popFirstArg()));
            if(uuid.toString().isEmpty()) throw new InvalidCommandArgument("Player not found.");
            return NSys.sql().wrapProfile(uuid);
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

        manager.getCommandContexts().registerContext(OfflinePlayer.class, c-> {
                String str = NSys.sql()
                        .wrapMap("players").getKeyFromVal(c.popFirstArg());
                if(str.isEmpty()) {
                    throw new InvalidCommandArgument("No player found.");
                }
                return Bukkit.getOfflinePlayer(UUID.fromString(str));
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

        manager.getCommandCompletions().registerCompletion("@globalplayers", c-> NSys.sql().wrapMap("players").getMap().values());

        manager.setDefaultExceptionHandler((command, registeredCommand, sender, args, t) -> {
            NSys.log().warning("Error occurred while executing command " + command.getName());
            return false;// mark as unhandled, sender will see default message
        });

        manager.registerCommand(new MainCmd());

        manager.registerCommand(new ProfileCmd(pl));

        manager.registerCommand(new SeenCmd());

        manager.registerCommand(new HomesCmd(pl));

        manager.registerCommand(new WarpsCmd(pl));

        CommandAPI.unregister("homes", true);
        new CommandAPICommand("homes")
                .withArguments(new StringArgument("action")
                        .replaceSuggestions(info -> {
                            if(info.sender().hasPermission("nsys.cmd.home.admin")) {
                                return new String[]{"tp","set","delete","list","ftp","oftp"};
                            }
                            return new String[]{"tp","set","delete","list"};
                        }))
                .withSubcommand(
                        new CommandAPICommand("tp")
                                .withArguments(homeArgument()
                                        .replaceSuggestions(info -> getHomes(info.sender())))
                                .withPermission("nsys.cmd.home.tp")
                                .executesPlayer((player, args) -> {
                                    String name = "home";
                                    if(args.length > 0) {
                                        name = (String) args[0];
                                    }
                                    Home home = NSys.sql().wrapProfile(player).getHome(name);
                                    if(home == null) {
                                        CommandAPI.fail("No home found.");
                                        return;
                                    }
                                    tp(player, home);
                                })
                )
                        .register();

        CommandAPI.unregister("home", true);
        new CommandAPICommand("home")
                .withRequirement(sender -> getHomes(sender).length > 0)
                .withArguments(homeArgument()
                        .replaceSuggestions(info -> getHomes(info.sender())))
                .withPermission(CommandPermission.fromString("nsys.cmd.home"))
                .withAliases("h","hometp","tphome")
                .executesPlayer((player, args) -> {
                    if(args.length == 0) {
                        Profile p = NSys.sql().wrapProfile(player);
                        Home h = p.getHome("home");
                        if(h != null) {
                            player.sendMessage(ChatColor.GREEN + "You're teleporting...");
                            h.teleport(player);
                        } else {
                            player.sendMessage(ChatColor.RED + "Home not found.");
                        }
                    } else if(args[0] == null) {
                        player.sendMessage(ChatColor.RED + "Home not found.");
                    } else {
                        player.sendMessage(ChatColor.GREEN + "You're teleporting...");
                        ((Home) args[0]).teleport(player);
                    }
                })
                .register();

        CommandAPI.unregister("delhome", true);
        new CommandAPICommand("delhome")
                .withRequirement(sender -> getHomes(sender).length > 0)
                .withArguments(homeArgument()
                        .replaceSuggestions(info -> getHomes(info.sender())))
                .withPermission(CommandPermission.fromString("nsys.cmd.home.delete"))
                .withAliases("rmhome","removehome","homedel")
                .executesPlayer((player, args) -> {
                    String homeName = args.length == 0 ? "home" : (String) args[0];

                    if(NSys.sql().wrapProfile(player.getUniqueId()).delHome(homeName) == null) {
                        player.sendMessage(ChatColor.RED + homeName + " does not exist.");
                    } else {
                        player.sendMessage(ChatColor.GREEN + "Home deleted.");
                        CommandAPI.updateRequirements(player);
                    }
                })
                .register();

        CommandAPI.unregister("sethome", true);
        new CommandAPICommand("sethome")
                .withRequirement(sender -> getHomes(sender).length > 0)
                .withArguments(new GreedyStringArgument("name")
                        .replaceSuggestions(info -> getHomes(info.sender())))
                .withPermission(CommandPermission.fromString("nsys.cmd.home.set"))
                .withAliases("addhome","makehome","homeset")
                .executesPlayer((player, args) -> {
                    String name = "home";
                    if(args.length > 0) {
                        name = (String) args[0];
                        if (nameCheck(name)) {
                            CommandAPI.fail("That name is too long!");
                            return;
                        }
                    }
                    NSys.sql().wrapProfile(player).setHomeHere(name);
                })
                .register();

        CommandAPI.unregister("setwarp", true);
        new CommandAPICommand("setwarp")
                .withRequirement(sender -> getHomes(sender).length > 0)
                .withArguments(new GreedyStringArgument("name")
                        .replaceSuggestions(info -> getWarps()))
                .withPermission(CommandPermission.fromString("nsys.cmd.warp.set"))
                .withAliases("addwarp","makewarp","warpset")
                .executesPlayer((player, args) -> {
                    if (nameCheck((String) args[0])) {
                        CommandAPI.fail("That name is too long!");
                        return;
                    }
                    NSys.newWarp((String) args[0], player, -1);
                })
                .register();

        CommandAPI.unregister("warp", true);
        new CommandAPICommand("warp")
                .withRequirement(sender -> getHomes(sender).length > 0)
                .withArguments(warpArgument()
                        .replaceSuggestions(info -> getWarps()))
                .withAliases("warptp","go")
                .withPermission(CommandPermission.fromString("nsys.cmd.warp"))
                .executesPlayer((player, args) -> {
                    if(args.length == 0) return;
                    if(args[0] == null) {
                        player.sendMessage(ChatColor.RED+"Warp not found.");
                    } else if(!NSys.sql().wrapList("warps").getList().contains(((Warp) args[0]).getKey())) {
                        player.sendMessage(ChatColor.RED+"Warp not found.");
                    } else {
                        player.teleport((Warp) args[0]);
                        player.sendMessage(ChatColor.YELLOW + "Teleported!");
                    }
                })
                .register();

        CommandAPI.unregister("buywarp", true);
        new CommandAPICommand("buywarp")
                .withArguments(locNameArgument())
                .withPermission(CommandPermission.fromString("nsys.cmd.warp.buy"))
                .withAliases("bwarp","purchasewarp","getwarp")
                .executesPlayer((player, args) -> {
                    if(args.length == 0) return;
                    else if(args[0] == null) return;
                    double price = NSys.sh().gen().getProperty(GeneralSettings.PRICE_WARPS);
                    if(EconUtils.makeTransaction(player, price)) {
                        NSys.newWarp((String) args[0], player, price);
                        player.sendMessage(ChatColor.GREEN+"You bought the warp " + ChatColor.YELLOW + args[0]
                                + "" + ChatColor.GREEN+ " for "+
                                ChatColor.RED+"$" + price + ChatColor.GREEN + ".");
                    } else {
                        CommandAPI.fail("Transaction unsuccessful. Do you have enough funds?\nYour balance is "
                                + NSys.econ().getBalance(player));
                    }
                })
                .register();
    }

    private String[] getHomes(CommandSender  sender) {
        if(!(sender instanceof Player)) return new String[]{};
        return NSys.sql().wrapProfile((Player) sender).getHomeNames().toArray(String[]::new);
    }

    private String[] getWarps() {
        return NSys.sql().wrapList("warps").getArray();
    }

    public void onDisable() {
        manager.unregisterCommands();
    }

    private Argument homeArgument() {
        return new CustomArgument<>("home", info -> {
            if(info.sender() instanceof Player) {
                return NSys.sql().wrapProfile((Player) info.sender()).getHomes().get(info.input());
            }
            return null;
        });
    }

    private Argument warpArgument() {
        return new CustomArgument<>("warp", info -> {
            if(info.sender() instanceof Player) {
                return NSys.sql().wrapWarp(info.input());
            }
            return null;
        });
    }

    private Argument locNameArgument() {
        return new CustomArgument<>("name", info -> {
            if(info.sender() instanceof Player) {
            }
            return null;
        });
    }

    private boolean nameCheck(String name) {
        return name.length() > 32;
    }

    private static void tp(final Player p, final Home home) {
        p.sendMessage(ChatColor.GREEN+"You're teleporting...");
        p.teleport(home);
    }
}
