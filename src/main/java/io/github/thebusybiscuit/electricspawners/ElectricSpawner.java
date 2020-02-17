package io.github.thebusybiscuit.electricspawners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import me.mrCookieSlime.CSCoreLibPlugin.general.String.StringUtils;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Lists.SlimefunItems;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.Objects.Research;
import me.mrCookieSlime.Slimefun.Objects.SlimefunBlockHandler;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SimpleSlimefunItem;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.UnregisterReason;
import me.mrCookieSlime.Slimefun.Objects.handlers.BlockTicker;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import me.mrCookieSlime.Slimefun.api.energy.ChargableBlock;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import me.mrCookieSlime.Slimefun.api.item_transport.ItemTransportFlow;
import me.mrCookieSlime.Slimefun.cscorelib2.item.CustomItem;

public class ElectricSpawner extends SimpleSlimefunItem<BlockTicker> {
	
	private static int lifetime = 0;
	
	private final EntityType entity;

	public ElectricSpawner(Category category, String mob, EntityType type, Research research) throws Exception {
		super(category, new SlimefunItemStack("ELECTRIC_SPAWNER_" + mob, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGI2YmQ5NzI3YWJiNTVkNTQxNTI2NTc4OWQ0ZjI5ODQ3ODFhMzQzYzY4ZGNhZjU3ZjU1NGE1ZTlhYTFjZCJ9fX0=", "&ePowered Spawner &7(" + StringUtils.format(mob) + ")", "", "&8\u21E8 &e\u26A1 &7Max Entity Cap: 6", "&8\u21E8 &e\u26A1 &7512 J Buffer", "&8\u21E8 &e\u26A1 &7240 J/Mob"), RecipeType.ENHANCED_CRAFTING_TABLE, 
		new ItemStack[] {
				null, SlimefunItems.PLUTONIUM, null, 
				SlimefunItems.ELECTRIC_MOTOR, new CustomItem(Material.SPAWNER, "&bReinforced Spawner", "&7Type: &b" + StringUtils.format(type.toString())), SlimefunItems.ELECTRIC_MOTOR, 
				SlimefunItems.BLISTERING_INGOT_3, SlimefunItems.ANDROID_MEMORY_CORE, SlimefunItems.BLISTERING_INGOT_3
		});
		
		this.entity = type;
		
		registerBlockHandler(getID(), new SlimefunBlockHandler() {
			
			@Override
			public void onPlace(Player p, Block b, SlimefunItem item) {
				BlockStorage.addBlockInfo(b, "enabled", "false");
				BlockStorage.addBlockInfo(b, "owner", p.getUniqueId().toString());
			}
			
			@Override
			public boolean onBreak(Player p, Block b, SlimefunItem item, UnregisterReason reason) {
				return true;
			}
		});
		
		new BlockMenuPreset(getID(), "&cPowered Spawner") {
			
			@Override
			public void init() {
				for (int i = 0; i < 9; i++) {
					if (i != 4) {
						addItem(i, new CustomItem(Material.LIGHT_GRAY_STAINED_GLASS_PANE, " "), (p, slot, item, action) -> false);
					}
				}
			}

			@Override
			public void newInstance(BlockMenu menu, Block b) {
				if (!BlockStorage.hasBlockInfo(b) || BlockStorage.getLocationInfo(b.getLocation(), "enabled") == null || BlockStorage.getLocationInfo(b.getLocation(), "enabled").equals("false")) {
					menu.replaceExistingItem(4, new CustomItem(Material.GUNPOWDER, "&7Enabled: &4\u2718", "", "&e> Click to enable this Machine"));
					menu.addMenuClickHandler(4, (p, slot, item, action) -> {
						BlockStorage.addBlockInfo(b, "enabled", "true");
						newInstance(menu, b);
						return false;
					});
				}
				else {
					menu.replaceExistingItem(4, new CustomItem(Material.REDSTONE, "&7Enabled: &2\u2714", "", "&e> Click to disable this Machine"));
					menu.addMenuClickHandler(4, (p, slot, item, action) -> {
						BlockStorage.addBlockInfo(b, "enabled", "false");
						newInstance(menu, b);
						return false;
					});
				}
			}

			@Override
			public boolean canOpen(Block b, Player p) {
				return BlockStorage.getLocationInfo(b.getLocation(), "owner").equals(p.getUniqueId().toString()) || p.hasPermission("slimefun.cargo.bypass");
			}

			@Override
			public int[] getSlotsAccessedByItemTransport(ItemTransportFlow flow) {
				return new int[0];
			}
		};
		
		research.addItems(this);
	}
	
	public int getEnergyConsumption() {
		return 240;
	}
	
	protected void tick(Block b) {
		if (BlockStorage.getLocationInfo(b.getLocation(), "enabled").equals("false")) return;
		if (lifetime % 3 != 0) return;
		if (ChargableBlock.getCharge(b) < getEnergyConsumption()) return;
		
		int count = 0;
		for (Entity n : b.getWorld().getNearbyEntities(b.getLocation(), 4.0, 4.0, 4.0)) {
			if (n.getType().equals(this.entity)) {
				count++;
				if (count > 6) return;
			}
		}
		
		ChargableBlock.addCharge(b, -getEnergyConsumption());
		b.getWorld().spawnEntity(new Location(b.getWorld(), b.getX() + 0.5D, b.getY() + 1.5D, b.getZ() + 0.5D), this.entity);
	}

	@Override
	public BlockTicker getItemHandler() {
		return new BlockTicker() {
			
			@Override
			public void tick(Block b, SlimefunItem sf, Config data) {
				ElectricSpawner.this.tick(b);
			}

			@Override
			public void uniqueTick() {
				lifetime++;
			}

			@Override
			public boolean isSynchronized() {
				return true;
			}
			
		};
	}

}
