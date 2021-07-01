package com.gray17.soul.catcraft;

import java.util.Iterator;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Cat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.projectiles.ProjectileSource;


public class InputHandler implements Listener {
    public static boolean VERBOSE;
    public static boolean VERBOSE_PLAYER_ONLY;
    public static boolean IS_GET_CMD_ACTIVATED;
    private PlayerHandler playerHandler;
    private CatCraft plugin;
    private Debugger debugger;

    public InputHandler(CatCraft plugin, Debugger debugger) {
        this.plugin = plugin;
        this.debugger = debugger;
        this.playerHandler = this.plugin.playerHandler;
        this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
    }

    public void init() {    	
        VERBOSE = this.plugin.getConfig().getBoolean("verbose");
        IS_GET_CMD_ACTIVATED = this.plugin.getConfig().getBoolean("get command activated");
        VERBOSE_PLAYER_ONLY = this.plugin.getConfig().getBoolean("verbose player only");
    }

    @EventHandler(
            priority = EventPriority.NORMAL
    )
    public void playerConnects(PlayerJoinEvent e) {
        this.playerHandler.addPlayer(e.getPlayer());
    }

    @EventHandler(
            priority = EventPriority.NORMAL
    )
    public void playerDisconnects(PlayerQuitEvent e) {
        this.playerHandler.removePlayer(e.getPlayer());
    }

	@EventHandler(
            priority = EventPriority.NORMAL
    )
    
    public void catDeath(EntityDeathEvent e) {
        if(e.getEntity() instanceof Ocelot || e.getEntity() instanceof Cat) {
            if(VERBOSE) {
            	debugger.info("Ocelot died");
            }

            if(e.getEntity().getKiller() != null && e.getEntity().getKiller() instanceof Player) {
                Player p = e.getEntity().getKiller();
                p.damage(9000.0D);
                if(VERBOSE) {
                	debugger.info(p + " received 9000 damage");
                }

                @SuppressWarnings("rawtypes")
				Iterator var3 = this.playerHandler.getPlayers().iterator();

                while(var3.hasNext()) {
                    Player all = (Player)var3.next();
                    all.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + p.getName() + " killed a cat " + "and is now recieving his righteous judgement!");
                }

                e.getEntity().getWorld().setStorm(true);
                e.getEntity().getWorld().setThundering(true);
                e.getEntity().getWorld().setThunderDuration(1000);
                if(VERBOSE) {
                	debugger.info("Thunderstorm inflicted");
                }
            }
        }
    }

    @EventHandler(
            priority = EventPriority.NORMAL
    )
    public void ward(EntityDamageByEntityEvent evt) {
    	
        double dmg = evt.getDamage();
        Entity damaged = evt.getEntity();
        Entity damager = evt.getDamager() instanceof Arrow ? (Entity) ((Arrow) evt.getDamager()).getShooter() : evt.getDamager();
        if(VERBOSE) {
        	//if verbose mode is only for player vs player interaction AND (thus); if the damager is not a player AND the damaged not one, do nothing
        	if(VERBOSE_PLAYER_ONLY && (!(damaged instanceof Player) || !(damager instanceof Player))) {
        		//do nothing
        		//else print log as usual
        	} else {
        		if(evt.getDamager() instanceof Arrow) {
            		ProjectileSource attacker = ((Arrow) evt.getDamager()).getShooter();
            		debugger.info(evt.getEntity() + " was harmed by " + evt.getDamager() + " of the type " + evt.getDamager().getType() + ", shooter: "+ attacker + ", damage: " + dmg);
            	} else if (evt.getDamager() instanceof Fireball) {
            		ProjectileSource attacker = ((Fireball) evt.getDamager()).getShooter();
            		debugger.info(evt.getEntity() + " was harmed by " + evt.getDamager() + " of the type " + evt.getDamager().getType() + ", shooter: "+ attacker + ", damage: " + dmg);
            	} else {
            		debugger.info(evt.getEntity() + " was harmed by " + evt.getDamager() + " of the type " + evt.getDamager().getType() + ", damage: " + dmg);
            	}
        	}
        	
        }
        
        if(evt.getEntity() instanceof Ocelot || evt.getEntity() instanceof Cat) {
            if(VERBOSE) {
                this.plugin.getLogger().info("A defended entity has been attacked! Type: " + evt.getEntity());
            }

            Player offender;
            if(evt.getDamager() != null && evt.getDamager() instanceof Player) {
                if(VERBOSE) {
                	debugger.info("Damager :" + evt.getDamager() + " is a Player");
                }

                offender = (Player)evt.getDamager();
                offender.damage(dmg);
                if(VERBOSE) {
                	debugger.info(evt.getDamager() + " received " + dmg + " damage");
                }
            } else if(evt.getDamager() instanceof Arrow) {
                if(VERBOSE) {
                	debugger.info("Damager was an arrow, owner: " + ((Arrow)evt.getDamager()).getShooter());
                }

                offender = (Player)((Arrow)evt.getDamager()).getShooter();
                offender.damage(dmg);
                if(VERBOSE) {
                	debugger.info(((Arrow)evt.getDamager()).getShooter() + " received " + dmg + " damage");
                }
            }
        }
    }
    @EventHandler(
            priority = EventPriority.LOW
    )
    public void playerInteract(InventoryOpenEvent evt) {
    	
    	
    	HumanEntity player = evt.getPlayer();
    	Location loc = player.getLocation();
    	if(InputHandler.VERBOSE) {
    		if(evt.getInventory().getType() instanceof InventoryType) {
    		
    			debugger.info("Player " + player.getName() + " opened " + evt.getInventory().getType() 
    					+ " at location: x=" + loc.getBlockX() + " y=" + loc.getBlockY() + " z=" + loc.getBlockZ());
    			
    		}
    	}
    }
}
