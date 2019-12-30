package io.github.thebusybiscuit.electricspawners;

import java.util.logging.Level;

import org.bukkit.entity.EntityType;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.Objects.Research;
import me.mrCookieSlime.Slimefun.api.Slimefun;
import me.mrCookieSlime.Slimefun.bstats.bukkit.Metrics;
import me.mrCookieSlime.Slimefun.cscorelib2.config.Config;
import me.mrCookieSlime.Slimefun.cscorelib2.item.CustomItem;
import me.mrCookieSlime.Slimefun.cscorelib2.skull.SkullItem;
import me.mrCookieSlime.Slimefun.cscorelib2.updater.GitHubBuildsUpdater;
import me.mrCookieSlime.Slimefun.cscorelib2.updater.Updater;

public class ElectricSpawners extends JavaPlugin implements Listener {
	
	@Override
	public void onEnable() {
		Config cfg = new Config(this);
		
		// Setting up bStats
		new Metrics(this);
		
		if (getDescription().getVersion().startsWith("DEV - ")) {
			Updater updater = new GitHubBuildsUpdater(this, getFile(), "TheBusyBiscuit/ElectricSpawners/master");
			
			// Only run the Updater if it has not been disabled
			if (cfg.getBoolean("options.auto-update")) updater.start();
		}
		
		Category category = new Category(new CustomItem(SkullItem.fromBase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGI2YmQ5NzI3YWJiNTVkNTQxNTI2NTc4OWQ0ZjI5ODQ3ODFhMzQzYzY4ZGNhZjU3ZjU1NGE1ZTlhYTFjZCJ9fX0="), "&9Electric Spawners", "", "&a> Click to open"));
		Research research = new Research(4820, "Powered Spawners", 30);
		
		for (String mob : cfg.getStringList("mobs")) {
			try {
				EntityType type = EntityType.valueOf(mob);
				new ElectricSpawner(category, mob, type, research).registerChargeableBlock(false, 2048);
			} catch(Exception x) {
				getLogger().log(Level.SEVERE, "An Error has occured while adding an Electric Spawner for the EntityType \"" + mob + "\"", x);
			}
		}
		
		Slimefun.registerResearch(research);
	}
}