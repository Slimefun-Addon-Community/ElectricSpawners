package io.github.thebusybiscuit.electricspawners;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;

import me.mrCookieSlime.CSCoreLibPlugin.general.World.ArmorStandFactory;

public class SpawnerHologram {
	
	private static final double offset = 0.8;
	
	public static void remove(final Block b) {
		ArmorStand hologram = getArmorStand(b);
		hologram.remove();
	}
	
	public static List<Entity> getNearbyEntities(final Block b, double radius) {
		ArmorStand hologram = getArmorStand(b);
		return hologram.getNearbyEntities(radius, 1D, radius);
	}
	
	private static ArmorStand getArmorStand(Block b) {
		Location l = new Location(b.getWorld(), b.getX() + 0.5, b.getY() + offset, b.getZ() + 0.5);
		
		for (Entity n: l.getChunk().getEntities()) {
			if (n instanceof ArmorStand) {
				if (n.getCustomName() == null && l.distanceSquared(n.getLocation()) < 0.4D) return (ArmorStand) n;
			}
		}
		
		ArmorStand hologram = ArmorStandFactory.createHidden(l);
		hologram.setCustomNameVisible(false);
		hologram.setCustomName(null);
		hologram.setSmall(true);
		return hologram;
	}

}
