package de.cirrus.catcraft;

import java.util.Iterator;
import org.bukkit.ChatColor;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class InputHandler implements Listener {
    public static boolean VERBOSE;
    public static boolean GET_ACTIVE;
    private PlayerHandler playerHandler;
    private CatCraft plugin;

    public InputHandler(CatCraft plugin) {
        this.plugin = plugin;
        this.playerHandler = this.plugin.playerHandler;
        this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
    }

    public void init() {
        VERBOSE = this.plugin.getConfig().getBoolean("verbose");
        GET_ACTIVE = this.plugin.getConfig().getBoolean("get command activated");
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
        if(e.getEntityType().equals(EntityType.OCELOT)) {
            if(VERBOSE) {
                this.plugin.getLogger().info("Ocelot died");
            }

            if(e.getEntity().getKiller() != null && e.getEntity().getKiller() instanceof Player) {
                Player p = e.getEntity().getKiller();
                p.damage(9000.0D);
                if(VERBOSE) {
                    this.plugin.getLogger().info(p + " received 9000 damage");
                }

                Iterator var3 = this.playerHandler.getPlayers().iterator();

                while(var3.hasNext()) {
                    Player all = (Player)var3.next();
                    all.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + p.getName() + " killed a cat " + "and is now recieving his righteous judgement!");
                }

                e.getEntity().getWorld().setStorm(true);
                e.getEntity().getWorld().setThundering(true);
                e.getEntity().getWorld().setThunderDuration(1000);
                if(VERBOSE) {
                    this.plugin.getLogger().info("Thunderstorm inflicted");
                }
            }
        }

    }

    @EventHandler(
            priority = EventPriority.NORMAL
    )
    public void catWard(EntityDamageByEntityEvent evt) {
        double dmg = evt.getDamage();
        if(VERBOSE) {
            this.plugin.getLogger().info(evt.getEntity() + " was harmed by " + evt.getDamager() + " of the type " + evt.getDamager().getType() + ", damage: " + dmg);
        }

        if(evt.getEntity() instanceof Ocelot) {
            if(VERBOSE) {
                this.plugin.getLogger().info("It was a cat!");
            }

            Player offender;
            if(evt.getDamager() != null && evt.getDamager() instanceof Player) {
                if(VERBOSE) {
                    this.plugin.getLogger().info("Damager :" + evt.getDamager() + " is a Player");
                }

                offender = (Player)evt.getDamager();
                offender.damage(dmg);
                if(VERBOSE) {
                    this.plugin.getLogger().info(evt.getDamager() + " received " + dmg + " damage");
                }
            } else if(evt.getDamager() instanceof Arrow) {
                if(VERBOSE) {
                    this.plugin.getLogger().info("Damager was an arrow, owner: " + ((Arrow)evt.getDamager()).getShooter());
                }

                offender = (Player)((Arrow)evt.getDamager()).getShooter();
                offender.damage(dmg);
                if(VERBOSE) {
                    this.plugin.getLogger().info(((Arrow)evt.getDamager()).getShooter() + " received " + dmg + " damage");
                }
            }
        }

    }
}
