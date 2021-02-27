package com.gray17.kling.catcraft;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemManager {
	
	public CatCraft plugin;
	public ItemStack watch = null;

	public ItemManager(CatCraft plugin) {
		this.plugin = plugin;
	}
	
	public void init() {	
		if(this.watch == null) {
			this.createWatch();
		}
		
		//TODO: Deklaration von weiteren Items
	}
	
	
	public final ItemStack createWatch() {
		this.watch = new ItemStack(Material.CLOCK, 1);
		this.watch.addUnsafeEnchantment(Enchantment.SILK_TOUCH, 2);
        ItemMeta im = this.watch.getItemMeta();
        im.setDisplayName("Time Transfer Device");
        String itemlore[] = {
                (new StringBuilder()).append(plugin.getConfig().getString("clockLore")).toString()
        };
        im.setLore(Arrays.asList(itemlore));
        this.watch.setItemMeta(im);
        NamespacedKey key = new NamespacedKey(plugin, "TTD");
        ShapedRecipe recipeitem = new ShapedRecipe(key, this.watch);
        recipeitem.shape(new String[] {
                " G ", "GRG", " G "
        });
        recipeitem.setIngredient('G', Material.GOLD_INGOT);
        recipeitem.setIngredient('R', Material.REDSTONE_BLOCK);
        plugin.getServer().addRecipe(recipeitem);
        if(InputHandler.VERBOSE) {
        	plugin.debugger.info("ITEM RECIPE CREATION - TIME TRANSFER DEVICE (WATCH) CREATED");
        }
        
        return watch;
	}
	
	public final void giveItem(Player player) {
		if(watch != null) {
			player.getInventory().addItem(watch);
		}
	}
}
