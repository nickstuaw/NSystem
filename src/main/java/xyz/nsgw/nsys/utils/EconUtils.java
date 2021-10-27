package xyz.nsgw.nsys.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import xyz.nsgw.nsys.NSys;

import java.util.Objects;

public class EconUtils {

    public static boolean makeTransaction(OfflinePlayer p, double amount) {
        if(!NSys.econ().has(p, amount)) {
            return false;
        }
        return NSys.econ().withdrawPlayer(p, amount).transactionSuccess();
    }

    public static void give(OfflinePlayer p, double amount, boolean notify, boolean refund) {
        if(!NSys.econ().hasAccount(p)) {
            NSys.log().warning("No account was found for " + p.getName() + ", refund did not work!");
            return;
        }
        NSys.econ().depositPlayer(p, amount);
        NSys.log().info("Refund made to " + p.getName() + " of $" + amount);
        if(notify && p.isOnline()) {
            Objects.requireNonNull(
                    Bukkit.getPlayer(p.getUniqueId())).sendMessage(ChatColor.GREEN
                    + "You have received "+
                    (refund ? "a refund of " : "")+"$"+amount+".");
        }
    }
}
