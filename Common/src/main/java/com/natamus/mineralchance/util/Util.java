package com.natamus.mineralchance.util;

import com.natamus.collective.data.GlobalVariables;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Util {
	private static final List<Item> overworldminerals = new ArrayList<Item>(Arrays.asList(Items.DIAMOND, Items.GOLD_NUGGET, Items.IRON_NUGGET, Items.LAPIS_LAZULI, Items.REDSTONE, Items.EMERALD));
	private static final List<Item> netherminerals = new ArrayList<Item>(Arrays.asList(Items.QUARTZ, Items.GOLD_NUGGET, Items.NETHERITE_SCRAP));
	
	public static Item getRandomOverworldMineral() {
		return overworldminerals.get(GlobalVariables.random.nextInt(overworldminerals.size()));
	}
	public static Item getRandomNetherMineral() {
		return netherminerals.get(GlobalVariables.random.nextInt(netherminerals.size()));
	}
}
