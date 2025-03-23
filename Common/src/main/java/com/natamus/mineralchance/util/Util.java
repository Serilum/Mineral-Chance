package com.natamus.mineralchance.util;

import com.natamus.collective.data.GlobalVariables;
import com.natamus.mineralchance.config.ConfigHandler;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Util {

	private static final Logger LOGGER = LoggerFactory.getLogger("Util");

	private static final List<Item> overworldMinerals = new ArrayList<Item>(Arrays.asList(Items.DIAMOND, Items.GOLD_NUGGET, Items.IRON_NUGGET, Items.LAPIS_LAZULI, Items.REDSTONE, Items.EMERALD));
	private static boolean overworldMineralsInited = false;

	private static final List<Item> netherMinerals = new ArrayList<Item>(Arrays.asList(Items.QUARTZ, Items.GOLD_NUGGET, Items.NETHERITE_SCRAP));
	private static boolean netherMineralsInited = false;

	public static Item getRandomOverworldMineral() {
		if (!overworldMineralsInited) {
			synchronized (overworldMinerals) {
				if (!overworldMineralsInited) {
					loadCustomMinerals(ConfigHandler.customOverworldMinerals, overworldMinerals);
					overworldMineralsInited = true;
				}
			}
		}
		return overworldMinerals.get(GlobalVariables.random.nextInt(overworldMinerals.size()));
	}

	public static Item getRandomNetherMineral() {
		if (!netherMineralsInited) {
			synchronized (netherMinerals) {
				if (!netherMineralsInited) {
					loadCustomMinerals(ConfigHandler.customNetherMinerals, netherMinerals);
					netherMineralsInited = true;
				}
			}
		}
		return netherMinerals.get(GlobalVariables.random.nextInt(netherMinerals.size()));
	}

	private static void loadCustomMinerals(List<String> customMineralsConf, List<Item> resultList) {
		try {
			if (customMineralsConf == null) {
				return;
			}

			Registry<Item> itemRegistry = BuiltInRegistries.ITEM;
			Set<Item> customMineralsResult = new HashSet<>();

			for (String customMineralTag : customMineralsConf) {
				String[] customMineral = customMineralTag.trim().split(":");

				if (customMineral.length != 2) {
					continue;
				}

				Item mineralItem = itemRegistry.get(ResourceLocation
						.fromNamespaceAndPath(customMineral[0], customMineral[1]));

				if (mineralItem != null && mineralItem != Items.AIR) {
					customMineralsResult.add(mineralItem);
				}
			}

			if (!customMineralsResult.isEmpty()) {
				customMineralsResult.addAll(resultList);
				resultList.clear();
				resultList.addAll(customMineralsResult);
			}
		} catch (Exception e) {
			LOGGER.error("Failed to load custom minerals: {}", customMineralsConf, e);
		}
	}

}
