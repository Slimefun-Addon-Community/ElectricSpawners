package me.mrCookieSlime.ElectricSpawners;

import me.mrCookieSlime.CSCoreLibPlugin.CSCoreLib;
import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ClickAction;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.Item.CustomItem;
import me.mrCookieSlime.CSCoreLibPlugin.general.String.StringUtils;
import me.mrCookieSlime.CSCoreLibPlugin.general.World.CustomSkull;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Lists.SlimefunItems;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.Objects.Research;
import me.mrCookieSlime.Slimefun.Objects.SlimefunBlockHandler;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.UnregisterReason;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.handlers.BlockTicker;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.energy.ChargableBlock;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import me.mrCookieSlime.Slimefun.api.item_transport.ItemTransportFlow;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Skeleton.SkeletonType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

public class ElectricSpawner extends SlimefunItem {
	
	public enum SpecialAttribute {
		
		NONE(null),
		@SuppressWarnings("deprecation")
		WITHER_SKELETON(new MaterialData(Material.SKULL_ITEM, (byte) 1).toItemStack(1));
		
		ItemStack item;
		
		SpecialAttribute(ItemStack item) {
			this.item = item;
		}
		
		public ItemStack getItem() {
			return this.item;
		}
	}
	
	private static int lifetime = 0;
	
	EntityType entity;
	SpecialAttribute attribute;

	public ElectricSpawner(Category category, String mob, EntityType type, Research research, SpecialAttribute attribute) throws Exception {
		super(category, new CustomItem(CustomSkull.getItem("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGI2YmQ5NzI3YWJiNTVkNTQxNTI2NTc4OWQ0ZjI5ODQ3ODFhMzQzYzY4ZGNhZjU3ZjU1NGE1ZTlhYTFjZCJ9fX0="), "&ePowered Spawner &7(" + (attribute.equals(SpecialAttribute.NONE) ? StringUtils.format(mob): StringUtils.format(attribute.toString())) + ")", "", "&8\u21E8 &e\u26A1 &7Max Entity Cap: 6", "&8\u21E8 &e\u26A1 &7512 J Buffer", "&8\u21E8 &e\u26A1 &7240 J/Mob"), "ELECTRIC_SPAWNER_" + (attribute.equals(SpecialAttribute.NONE) ? mob: attribute.toString()), RecipeType.ENHANCED_CRAFTING_TABLE, 
		new ItemStack[] {attribute.getItem(), SlimefunItems.PLUTONIUM, attribute.getItem(), SlimefunItems.ELECTRIC_MOTOR, new CustomItem(Material.MOB_SPAWNER, "&bReinforced Spawner", 0, new String[] {"&7Type: &b" + StringUtils.format(type.toString())}), SlimefunItems.ELECTRIC_MOTOR, SlimefunItems.BLISTERING_INGOT_3, SlimefunItems.ANDROID_MEMORY_CORE, SlimefunItems.BLISTERING_INGOT_3});
		
		this.attribute = attribute;
		this.entity = type;
		
		registerBlockHandler(this.getName(), new SlimefunBlockHandler() {
			
			@Override
			public void onPlace(Player p, Block b, SlimefunItem item) {
				BlockStorage.addBlockInfo(b, "enabled", "false");
				BlockStorage.addBlockInfo(b, "owner", p.getUniqueId().toString());
			}
			
			@Override
			public boolean onBreak(Player p, Block b, SlimefunItem item, UnregisterReason reason) {
				SpawnerHologram.remove(b);
				return true;
			}
		});
		
		new BlockMenuPreset(this.getName(), "&cPowered Spawner") {
			
			@SuppressWarnings("deprecation")
			@Override
			public void init() {
				for (int i = 0; i < 9; i++) {
					if (i != 4) {
						addItem(i, new CustomItem(new MaterialData(Material.STAINED_GLASS_PANE, (byte) 9), " "),
						new MenuClickHandler() {

							@Override
							public boolean onClick(Player arg0, int arg1, ItemStack arg2, ClickAction arg3) {
								return false;
							}
									
						});
					}
				}
			}

			@Override
			public void newInstance(final BlockMenu menu, final Block b) {
				if (!BlockStorage.hasBlockInfo(b) || BlockStorage.getBlockInfo(b, "enabled") == null || BlockStorage.getBlockInfo(b, "enabled").equals("false")) {
					menu.replaceExistingItem(4, new CustomItem(new MaterialData(Material.SULPHUR), "&7Enabled: &4\u2718", "", "&e> Click to enable this Machine"));
					menu.addMenuClickHandler(4, new MenuClickHandler() {

						@Override
						public boolean onClick(Player p, int arg1, ItemStack arg2, ClickAction arg3) {
							BlockStorage.addBlockInfo(b, "enabled", "true");
							newInstance(menu, b);
							return false;
						}
					});
				}
				else {
					menu.replaceExistingItem(4, new CustomItem(new MaterialData(Material.REDSTONE), "&7Enabled: &2\u2714", "", "&e> Click to disable this Machine"));
					menu.addMenuClickHandler(4, new MenuClickHandler() {

						@Override
						public boolean onClick(Player p, int arg1, ItemStack arg2, ClickAction arg3) {
							BlockStorage.addBlockInfo(b, "enabled", "false");
							newInstance(menu, b);
							return false;
						}
					});
				}
			}

			@Override
			public boolean canOpen(Block b, Player p) {
				return BlockStorage.getBlockInfo(b, "owner").equals(p.getUniqueId().toString()) || p.hasPermission("slimefun.cargo.bypass");
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
	
	
	@Override
	public void register(boolean slimefun) {
		addItemHandler(new BlockTicker() {
			
			@Override
			public void tick(Block b, SlimefunItem sf, Config data) {
				try {
					ElectricSpawner.this.tick(b);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void uniqueTick() {
				lifetime++;
			}

			@Override
			public boolean isSynchronized() {
				return true;
			}
		});

		super.register(slimefun);
	}
	
	protected void tick(Block b) throws Exception {
		if (BlockStorage.getBlockInfo(b, "enabled").equals("false")) return;
		if (lifetime % 3 != 0) return;
		if (ChargableBlock.getCharge(b) < getEnergyConsumption()) return;
		
		int count = 0;
		for (Entity n: SpawnerHologram.getNearbyEntities(b, 4)) {
			if (n.getType().equals(this.entity)) {
				count++;
				
				if (count > 6) return;
			}
		}
		
		ChargableBlock.addCharge(b, -getEnergyConsumption());
		
		switch (attribute) {
		case WITHER_SKELETON: {
			Skeleton skeleton = (Skeleton) b.getWorld().spawnEntity(new Location(b.getWorld(), b.getX() + 0.5D, b.getY() + 1.5D, b.getZ() + 0.5D), this.entity);
			skeleton.setSkeletonType(SkeletonType.WITHER);
			skeleton.getEquipment().setItemInMainHand(new ItemStack(Material.STONE_SWORD, CSCoreLib.randomizer().nextInt(Material.STONE_SWORD.getMaxDurability())));
			break;
		}
		default: {
			b.getWorld().spawnEntity(new Location(b.getWorld(), b.getX() + 0.5D, b.getY() + 1.5D, b.getZ() + 0.5D), this.entity);
			break;
		}
		}
	}

}
