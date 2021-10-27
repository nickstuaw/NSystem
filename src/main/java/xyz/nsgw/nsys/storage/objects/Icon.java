package xyz.nsgw.nsys.storage.objects;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class Icon {

    private ItemStack stack;

    private String additions;

    public Icon(Material material, int amount, OfflinePlayer player, String additions) {
        this.additions = additions;
        stack = new ItemStack(material);
        stack.setAmount(amount);
        if(material.equals(Material.PLAYER_HEAD)) {
            SkullMeta meta = (SkullMeta) stack.getItemMeta();
            meta.setOwningPlayer(player);
            stack.setItemMeta(meta);
        }
    }

    public ItemStack getStack() {
        return stack;
    }

    public void setStack(final ItemStack is) {
        this.stack = is;
    }

    public String getAdditions() {
        return this.additions;
    }

}
