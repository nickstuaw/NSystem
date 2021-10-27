/*
 * Copyright (c) Nicholas Williams 2021.
 */

package xyz.nsgw.nsys.commands;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.arguments.*;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.nsgw.nsys.NSys;
import xyz.nsgw.nsys.config.settings.GeneralSettings;
import xyz.nsgw.nsys.storage.objects.Profile;
import xyz.nsgw.nsys.storage.objects.locations.Home;
import xyz.nsgw.nsys.storage.objects.locations.Warp;
import xyz.nsgw.nsys.utils.DisplayUtils;
import xyz.nsgw.nsys.utils.EconUtils;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static xyz.nsgw.nsys.utils.DisplayUtils.txt;

public class CommandHandler {

    public CommandHandler(NSys pl) {

        new CommandAPICommand("nsys")
                .withPermission("nsys.admin")
                .withSubcommand(
                        new CommandAPICommand("reload")
                                .executes((sender, args) -> {
                                    NSys.reload();
                                    sender.sendMessage(ChatColor.BLUE + "The general_config for NSys has been reloaded.");
                                })
                )
                .executes((sender, args) -> {
                    sender.sendMessage("NSys installation: " + NSys.version());
                })
                .register();

        new CommandAPICommand("seen")
                .withArguments(new OfflinePlayerArgument("player"))
                .withPermission("nsys.cmd.seen.other")
                .executes((sender, args) -> {
                    OfflinePlayer p = (OfflinePlayer) args[0];
                    if(p.isOnline()) {
                        sender.sendMessage(MiniMessage.get().parse("<aqua>"+p.getName()+" has been online for "
                                + DisplayUtils.timeDiff(p.getLastLogin()) + "."));
                    } else {
                        sender.sendMessage(MiniMessage.get().parse("<aqua>"+p.getName()+" was last seen " +
                                DisplayUtils.timeDiff(p.getLastSeen()) + " ago."));
                    }
                })
                .register();

        new CommandAPICommand("seen")
                .withPermission("nsys.cmd.seen")
                .executesPlayer((player, args) -> {
                    player.sendMessage(MiniMessage.get().parse("<aqua>You have been online for "
                            + DisplayUtils.timeDiff(player.getLastLogin()) + "."));
                })
                .register();

        //CommandAPI.unregister("homes", true);

        new CommandAPICommand("homes")
                .withSubcommand(
                        new CommandAPICommand("tp")
                                .withArguments(homeArgument())
                                .withPermission("nsys.cmd.home")
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
                .withSubcommand(
                        new CommandAPICommand("visit")
                                .withArguments(new PlayerArgument("player"),
                                        new IntegerArgument("home"))
                                .withPermission(CommandPermission.fromString("nsys.cmd.homes.visit"))
                                .executesPlayer((player, args) -> {
                                    if(args.length > 0) {
                                        if(args[1] == null) {
                                            CommandAPI.fail("Invalid argument.");
                                        } else {
                                            Home home = NSys.sql().wrapProfile((Player) args[0])
                                                    .getHomeAtIndex((Integer) args[1]);
                                            if(home == null) {
                                                CommandAPI.fail("Home not found.");
                                            }
                                            tp(player, home);
                                        }
                                    }
                                })
                )
                .withPermission(CommandPermission.fromString("nsys.cmd.homes"))
                .executesPlayer((player, args) -> {
                    Profile profile = NSys.sql().wrapProfile(player);
                    pl.guiHandler().homes(profile, player);
                })
        .register();

        //CommandAPI.unregister("home", true);

        new CommandAPICommand("home")
                .withRequirement(sender -> getHomes(sender).length > 0)
                .withArguments(homeArgument())
                .withPermission(CommandPermission.fromString("nsys.cmd.home"))
                .withAliases("h","hometp","tphome")
                .executesPlayer((player, args) -> {
                    if(args.length == 0) {
                        Profile p = NSys.sql().wrapProfile(player);
                        Home h = p.getHome("home");
                        if(h != null) {
                            tp(player, h);
                        } else {
                            player.sendMessage(ChatColor.RED + "Home not found.");
                        }
                    } else if(args[0] == null) {
                        player.sendMessage(ChatColor.RED + "Home not found.");
                    } else {
                        ((Home) args[0]).teleport(player, true);
                    }
                })
                .register();

        new CommandAPICommand("delhome")
                .withRequirement(sender -> getHomes(sender).length > 0)
                .withArguments(homeArgument())
                .withPermission(CommandPermission.fromString("nsys.cmd.delhome"))
                .withAliases("rmhome","removehome","homedel")
                .executesPlayer((player, args) -> {
                    String homeName = args.length == 0 ? "home" : ((Home) args[0]).getName();

                    if(NSys.sql().wrapProfile(player.getUniqueId()).delHome(homeName) == null) {
                        player.sendMessage(ChatColor.RED + homeName + " does not exist.");
                    } else {
                        player.sendMessage(ChatColor.GREEN + "Home deleted.");
                        CommandAPI.updateRequirements(player);
                    }
                })
                .register();

        new CommandAPICommand("sethome")
                .withArguments(new GreedyStringArgument("name")
                        .replaceSuggestions(info -> getHomes(info.sender())))
                .withPermission(CommandPermission.fromString("nsys.cmd.sethome"))
                .withAliases("addhome","makehome","homeset")
                .executesPlayer((player, args) -> {
                    String name = "home";
                    if(args.length > 0) {
                        name = (String) args[0];
                        if (nameCheck(name)) {
                            CommandAPI.fail("That name is too long!");
                            return;
                        } else if(nameSymbolCheck(name)) {
                            CommandAPI.fail("Home names cannot contain : or ; characters.");
                            return;
                        }
                    }
                    NSys.sql().wrapProfile(player).setHomeHere(name);
                })
                .register();

        //CommandAPI.unregister("setwarp", true);

        new CommandAPICommand("setwarp")
                .withArguments(new GreedyStringArgument("name")
                        .replaceSuggestions(info -> getWarps()))
                .withPermission(CommandPermission.fromString("nsys.cmd.setwarp"))
                .withAliases("addwarp","makewarp","warpset")
                .executesPlayer((player, args) -> {
                    if(args.length < 1) {
                        CommandAPI.fail("Invalid argument.");
                        return;
                    }
                    String name = (String) args[0];
                    if (nameCheck(name)) {
                        CommandAPI.fail("That name is too long!");
                    } else if (nameSymbolCheck(name)) {
                        CommandAPI.fail("Warp names cannot contain : or ; characters.");
                    } else if (nameSymbolCheck(name))
                        NSys.newWarp(name, player, -1);
                })
                .register();

        new CommandAPICommand("delwarp")
                .withArguments(warpArgument())
                .withPermission(CommandPermission.fromString("nsys.cmd.warp.set"))
                .withAliases("removewarp","rmwarp","deletewarp")
                .executes((sender, args) -> {
                    if(args[0] == null) {
                        CommandAPI.fail("Invalid argument.");
                        return;
                    }
                    Warp warp = (Warp) args[0];
                    if(!warp.exists()) {
                        CommandAPI.fail("Warp not found.");
                        return;
                    }
                    NSys.sql().invalidateAndDeleteWarp(warp);
                    NSys.sql().wrapList("warps").del(warp.getName());
                    sender.sendMessage(txt("<green>Warp <yellow>" + warp.getName() +"</yellow> deleted."));
                })
                .register();

        //CommandAPI.unregister("warp", true);

        new CommandAPICommand("warp")
                .withArguments(warpArgument())
                .withAliases("warptp","go")
                .withPermission(CommandPermission.fromString("nsys.cmd.warp"))
                .executesPlayer((player, args) -> {
                    if(args.length == 0) return;
                    if(args[0] == null) {
                        player.sendMessage(ChatColor.RED+"Warp not found.");
                    } else if(!NSys.sql().wrapList("warps").getList().contains(((Warp) args[0]).getName())) {
                        player.sendMessage(ChatColor.RED+"Warp not found.");
                    } else {
                        ((Warp) args[0]).teleport(player, true);
                    }
                })
                .register();

        //CommandAPI.unregister("warpinfo", true);

        new CommandAPICommand("warpinfo")
                .withArguments(warpArgument())
                .withAliases("aboutwarp","iwarp")
                .withPermission(CommandPermission.fromString("nsys.cmd.warp.info"))
                .executes((sender, args) -> {
                    if(args.length == 0) return;
                    if(args[0] == null) {
                        sender.sendMessage(ChatColor.RED+"Warp not found.");
                    } else if(!NSys.sql().wrapList("warps").getList().contains(((Warp) args[0]).getName())) {
                        sender.sendMessage(ChatColor.RED+"Warp not found.");
                    } else {
                        Warp warp = (Warp) args[0];
                        if(warp.exists()) {
                            sender.sendMessage(DisplayUtils.txt((sender instanceof Player ? "" : "\n")
                                    + "<green>Warp name: " + warp.getName()
                                    + "\nOwner: <aqua>" + Bukkit.getOfflinePlayer(warp.getOwnerUuid()).getName()
                                    + "</aqua>\nFor sale: " + (warp.isForSale() ?
                                    "<green>Yes</green>" : "<red>No</red>")
                                    + "\nValue: <red>$" + warp.getPrice()
                                    + (sender.hasPermission("nsys.admin") ?
                                    "</red>\nLocation: <yellow>" + warp + "</yellow>." : "")));
                        } else {
                            sender.sendMessage(txt("<red>Warp <yellow>"+warp.getName()+"</yellow> doesn't exist."));
                        }
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
                    String name = (String) args[0];
                    Warp warp = null;
                    if(NSys.sql().wrapList("warps").getList().contains(name)) {
                        warp = NSys.sql().wrapWarp(name);
                        price = warp.getPrice();
                        if(!warp.isForSale() && !warp.getOwnerUuid().equals(player.getUniqueId())) {
                            CommandAPI.fail("This warp is not for sale.");
                            return;
                        } else if (warp.getOwnerUuid().equals(player.getUniqueId())) {
                            if(!warp.isForSale()) {
                                warp.setLocation(player.getLocation());
                                player.sendMessage(txt("<green>Location updated for " + warp.getName() + "."));
                                NSys.log().info(player.getName() + " updated location of warp "
                                        +warp.getName()+".");
                                return;
                            } else {
                                warp.setForSale(false);
                                warp.setLocation(player.getLocation());
                                player.sendMessage(txt("<green>Your warp is no longer up for sale. " +
                                        "Warp location updated."));
                                NSys.log().info(player.getName() + " is no longer selling warp "+warp.getName()+".");
                            }
                        }
                    }
                    if(EconUtils.makeTransaction(player, price)) {
                        if(warp != null) {
                            if(warp.isForSale()) {
                                warp.setForSale(false);
                                OfflinePlayer previousOwner = Bukkit.getOfflinePlayer(warp.getOwnerUuid());
                                EconUtils.give(
                                        previousOwner, price,
                                        true, false);
                                if(previousOwner.isOnline()) {
                                    Objects.requireNonNull(previousOwner.getPlayer())
                                            .sendMessage(txt("<green>Your warp (<yellow>"+warp.getName()+"</yellow>)"
                                                    + " has been purchased by " + player.getName() + " for <red>$"
                                                    + price + "</red>.</green>"));
                                }
                                player.sendMessage(txt("<green>You bought the warp <yellow>" +  args[0]
                                        + "</yellow> for "  +"<red>$" + price + "</red>" +
                                        " from " + Bukkit.getOfflinePlayer(warp.getOwnerUuid()).getName()
                                        +".</green>"));
                                warp.setOwnerUuid(player.getUniqueId());
                                warp.setLocation(player.getLocation());
                            }
                        } else {
                            warp = NSys.newWarp((String) args[0], player, price);
                            if(warp == null) {
                                EconUtils.give(player, price, true, true);
                                CommandAPI.fail("You have been refunded because an error has occurred." +
                                        "\nPlease ask an administrator to fix the configuration file for NSys.");
                            } else {
                                player.sendMessage(txt("<green>You bought the warp <yellow>" + warp.getName()
                                        + "</yellow> for " + "<red>$" + price + "</red>" + ".</green>"));
                            }
                        }
                    } else {
                        CommandAPI.fail("Transaction unsuccessful. Do you have enough funds?\nYour balance is "
                                + NSys.econ().getBalance(player));
                    }
                })
                .register();

        new CommandAPICommand("sellwarp")
                .withArguments(ownWarpArgument())
                .withPermission(CommandPermission.fromString("nsys.cmd.warp.sell"))
                .executesPlayer((player, args) -> {
                    if(args.length < 1) {
                        CommandAPI.fail("That warp does not exist.");
                        return;
                    } else if(args[0] == null) {
                        CommandAPI.fail("You cannot sell that warp.");
                        return;
                    }
                    Warp warp = (Warp) args[0];
                    if(!warp.exists()) {
                        CommandAPI.fail("You cannot sell that warp.");
                        return;
                    } else if (warp.getOwnerUuid().equals(player.getUniqueId()) && warp.isForSale()) {
                        warp.setForSale(false);
                        player.sendMessage(txt("<green>Your warp is no longer up for sale."));
                        NSys.log().info(player.getName() + " is no longer selling warp "+warp.getName()+".");
                        return;
                    }
                    warp.setForSale(true);
                    player.sendMessage(txt("<green>You are now selling the warp <yellow>" + warp.getName()
                            + "</yellow> for <red>$" + warp.getPrice() + "</red>."));
                    NSys.log().info(player.getName() + " is now selling warp "+warp.getName()+".");
                })
                .register();

        new CommandAPICommand("refundwarp")
                .withArguments(warpArgument())
                .withPermission(CommandPermission.fromString("nsys.cmd.refundwarp"))
                .withAliases("refwarp")
                .executes((sender, args) -> {
                    if(args.length == 0) return;
                    else if(args[0] == null) return;
                    Warp warp = (Warp) args[0];
                    EconUtils.give(Bukkit.getOfflinePlayer(warp.getOwnerUuid()), warp.getPrice(), true, true);
                    NSys.sql().invalidateAndDeleteWarp(warp);
                    NSys.sql().wrapList("warps").del(warp.getName());
                    sender.sendMessage(ChatColor.GREEN + "Warp " + ChatColor.YELLOW + warp.getName() + ChatColor.GREEN + " deleted.");
                })
                .register();

        //CommandAPI.unregister("warps", true);

        new CommandAPICommand("warps")
                .withSubcommand(
                        new CommandAPICommand("tp")
                                .withArguments(warpArgument())
                                .withPermission("nsys.cmd.warp")
                                .executesPlayer((player, args) -> {
                                    if(args.length == 0) {
                                        pl.guiHandler().warps(player);
                                        return;
                                    }
                                    Warp warp = (Warp) args[0];
                                    if(warp == null) {
                                        CommandAPI.fail("No warp found.");
                                        return;
                                    }
                                    warp.teleport(player, true);
                                })
                )
                .withSubcommand(
                        new CommandAPICommand("list")
                                .withPermission("nsys.cmd.warps")
                                .executes((sender, args) -> {
                                    List<String> warps = NSys.sql().wrapList("warps").getList();
                                    sender.sendMessage(ChatColor.GREEN +""+warps.size()+" warps: "+ChatColor.RESET+ String.join(", ", warps));
                                })
                )
                .withPermission(CommandPermission.fromString("nsys.cmd.warps"))
                .executesPlayer((player, args) -> {
                    pl.guiHandler().warps(player);
                })
                .executesConsole((sender, args) -> {
                    List<String> warps = NSys.sql().wrapList("warps").getList();
                    sender.sendMessage(ChatColor.GREEN +""+warps.size()+" warps: "+ChatColor.RESET+ String.join(", ", warps));
                })
                .register();

        new CommandAPICommand("profile")
                .withPermission(CommandPermission.fromString("nsys.cmd.profile.other"))
                .withArguments(new OfflinePlayerArgument("player"))
                .withAliases("prof")
                .executesPlayer((player, args) -> {
                    UUID uuid = player.getUniqueId();
                    if(args[0] != null) {
                        uuid = ((OfflinePlayer) args[0]).getUniqueId();
                    }
                    pl.guiHandler().profile(NSys.sql().wrapProfile(uuid), player, uuid == player.getUniqueId());
                })
                .executesConsole((sender, args) -> {
                    if(args[0] != null) {
                        OfflinePlayer target = (OfflinePlayer) args[0];
                        sender.sendMessage(txt("\n"+ChatColor.YELLOW+target.getName()+"'s profile:\n"
                                + DisplayUtils.rawAdminProfileMeta(getProfile(target), target)));
                    } else {
                        CommandAPI.fail("Invalid argument.");
                    }
                })
                .register();

        new CommandAPICommand("profile")
                .withPermission(CommandPermission.fromString("nsys.cmd.profile"))
                .withAliases("prof|myprof")
                .executesPlayer((player, args) -> {
                    pl.guiHandler().profile(NSys.sql().wrapProfile(player), player, true);
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

    private String[] getWarps(Player player) {
        return NSys.sql().wrapProfile(player).getOwnedWarps().toArray(String[]::new);
    }

    private Argument homeArgument() {
        return new CustomArgument<>("home", info -> {
            if(info.sender() instanceof Player) {
                return NSys.sql().wrapProfile((Player) info.sender()).getHomes().get(info.input());
            }
            return null;
        }).replaceSuggestions(info -> getHomes(info.sender()));
    }

    private Argument warpArgument() {
        return new CustomArgument<>("warp", info -> {
            return NSys.sql().wrapWarp(info.input());
        }).replaceSuggestions(info -> getWarps());
    }

    private Argument ownWarpArgument() {
        return new CustomArgument<>("warp", info -> {
            if(!(info.sender() instanceof Player)) return null;
            Warp warp = NSys.sql().wrapWarp(info.input());
            if(!warp.exists())
                return null;
            if(warp.getOwnerUuid().equals(((Player)info.sender()).getUniqueId()))
                return warp;
            return null;
        }).replaceSuggestions(info -> {
            if(info.sender() instanceof Player)
                return getWarps((Player) info.sender());
            return null;
        });
    }

    private Argument locNameArgument() {
        return new CustomArgument<>("name", info -> {
            if(info.sender() instanceof Player) {
                String name = info.input();
                if(nameCheck(name) || nameSymbolCheck(name)) {
                    return null;
                } else {
                    return name;
                }
            }
            return null;
        });
    }

    private boolean nameCheck(String name) {
        return name.length() > 32;
    }

    private boolean nameSymbolCheck(String name) {
        return name.contains(";") || name.contains(":");
    }

    private static void tp(final Player p, final Home home) {
        home.teleport(p, true);
    }

    private static Profile getProfile(final OfflinePlayer p) {
        return NSys.sql().wrapProfile(p.getUniqueId());
    }
}
