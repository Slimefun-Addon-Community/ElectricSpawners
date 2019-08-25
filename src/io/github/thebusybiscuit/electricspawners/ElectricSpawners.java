package io.github.thebusybiscuit.electricspawners;

import me.mrCookieSlime.CSCoreLibPlugin.PluginUtils;
import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.Item.CustomItem;
import me.mrCookieSlime.CSCoreLibPlugin.general.World.CustomSkull;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.Objects.Research;
import me.mrCookieSlime.Slimefun.api.Slimefun;

import org.bstats.bukkit.Metrics;
import org.bukkit.entity.EntityType;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import io.github.thebusybiscuit.cscorelib2.updater.BukkitUpdater;
import io.github.thebusybiscuit.cscorelib2.updater.GitHubBuildsUpdater;
import io.github.thebusybiscuit.cscorelib2.updater.Updater;

public class ElectricSpawners extends JavaPlugin implements Listener {
	
	@Override
	public void onEnable() {
		PluginUtils utils = new PluginUtils(this);
		utils.setupConfig();
		Config cfg = utils.getConfig();
		
		// Setting up bStats
		new Metrics(this);

		// Setting up the Auto-Updater
		Updater updater;

		if (!getDescription().getVersion().startsWith("DEV - ")) {
			// We are using an official build, use the BukkitDev Updater
			updater = new BukkitUpdater(this, getFile(), 99876);
		}
		else {
			// If we are using a development build, we want to switch to our custom 
			updater = new GitHubBuildsUpdater(this, getFile(), "TheBusyBiscuit/ElectricSpawners/master");
		}
		
		// Only run the Updater if it has not been disabled
		if (cfg.getBoolean("options.auto-update")) updater.start();
		
		Category category = null;
		try {
			category = new Category(new CustomItem(CustomSkull.getItem("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGI2YmQ5NzI3YWJiNTVkNTQxNTI2NTc4OWQ0ZjI5ODQ3ODFhMzQzYzY4ZGNhZjU3ZjU1NGE1ZTlhYTFjZCJ9fX0="), "&9Electric Spawners", "", "&a> Click to open"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Research research = new Research(4820, "Powered Spawners", 30);
		
		for (String mob: cfg.getStringList("mobs")) {
			try {
				EntityType type = EntityType.valueOf(mob);
				new ElectricSpawner(category, mob, type, research).registerChargeableBlock(false, 2048);
			} catch(Exception x) {
				System.err.println("[ElectricSpawners] " + x.getClass().getName() + ": " + mob);
			}
		}
		
		Slimefun.registerResearch(research);
	}
}