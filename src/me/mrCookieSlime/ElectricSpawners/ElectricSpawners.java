package me.mrCookieSlime.ElectricSpawners;

import me.mrCookieSlime.CSCoreLibPlugin.PluginUtils;
import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.Item.CustomItem;
import me.mrCookieSlime.CSCoreLibPlugin.general.World.CustomSkull;
import me.mrCookieSlime.ElectricSpawners.ElectricSpawner.SpecialAttribute;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.Objects.Research;
import me.mrCookieSlime.Slimefun.api.Slimefun;

import org.bukkit.entity.EntityType;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class ElectricSpawners extends JavaPlugin implements Listener {
	
	@Override
	public void onEnable() {
		PluginUtils utils = new PluginUtils(this);
		utils.setupConfig();
		utils.setupUpdater(99876, getFile());
		
		Config cfg = utils.getConfig();
		
		Category category = null;
		try {
			category = new Category(new CustomItem(CustomSkull.getItem("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGI2YmQ5NzI3YWJiNTVkNTQxNTI2NTc4OWQ0ZjI5ODQ3ODFhMzQzYzY4ZGNhZjU3ZjU1NGE1ZTlhYTFjZCJ9fX0="), "&9Electric Spawners", "", "&a> Click to open"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Research research = new Research(4820, "Powered Spawners", 30);
		
		for (String mob: cfg.getStringList("mobs")) {
			if (mob.equals("WITHER_SKELETON")) {
				try {
					new ElectricSpawner(category, mob, EntityType.SKELETON, research, SpecialAttribute.WITHER_SKELETON)
					.registerChargeableBlock(false, 2048);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			else {
				try {
					EntityType type = EntityType.valueOf(mob);
					new ElectricSpawner(category, mob, type, research, SpecialAttribute.NONE).registerChargeableBlock(false, 2048);
				} catch(Exception x) {
					System.err.println("[ElectricSpawners] " + x.getClass().getName() + ": " + mob);
				}
			}
		}
		
		Slimefun.registerResearch(research);
	}
}