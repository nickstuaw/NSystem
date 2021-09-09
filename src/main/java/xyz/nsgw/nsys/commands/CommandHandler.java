package xyz.nsgw.nsys.commands;

import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.MessageKeys;
import co.aikar.commands.MessageType;
import co.aikar.commands.PaperCommandManager;
import com.google.common.collect.ImmutableList;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.nsgw.nsys.NSys;
import xyz.nsgw.nsys.storage.Home;
import xyz.nsgw.nsys.storage.Profile;

import java.util.stream.Collectors;

public class CommandHandler {

    private final PaperCommandManager manager;

    public CommandHandler(NSys pl) {

        manager = new PaperCommandManager(pl);

        manager.enableUnstableAPI("brigadier");
        manager.enableUnstableAPI("help");
    }

    public void onDisable() {
        manager.unregisterCommands();
    }
}
