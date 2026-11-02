package com.serilum.mineralchance.config;

import com.natamus.collective.config.DuskConfig;
import com.serilum.mineralchance.util.Reference;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ConfigHandler extends DuskConfig {
	public static HashMap<String, List<String>> configMetaData = new HashMap<String, List<String>>();

	@Entry(min = 0, max = 1.0) public static double extraMineralChanceOnOverworldStoneBreak = 0.02;
	@Entry(min = 0, max = 1.0) public static double extraMineralChanceOnNetherStoneBreak = 0.01;
	@Entry public static boolean enableOverworldMinerals = true;
	@Entry public static boolean enableNetherMinerals = true;
	@Entry(registry = "item", weights = true) public static String overworldMineralDrops = "minecraft:iron_nugget=10,minecraft:gold_nugget=8,minecraft:redstone=6,minecraft:lapis_lazuli=6,minecraft:emerald=2,minecraft:diamond=1";
	@Entry(registry = "item", weights = true) public static String netherMineralDrops = "minecraft:quartz=10,minecraft:gold_nugget=10,minecraft:netherite_scrap=1";
	@Entry(registry = "block") public static String extraOverworldMineralBlocks = "";
	@Entry(registry = "block") public static String extraNetherMineralBlocks = "";
	@Entry public static boolean enableMineralsInOtherDimensions = true;
	@Entry(registry = "dimension") public static String dimensionsWithoutMinerals = "";
	@Entry(min = 0, max = 10.0) public static double fortuneChanceMultiplierPerLevel = 0.5;
	@Entry public static boolean explosionsCanDropMinerals = false;
	@Entry(registry = "item", filter = "tools") public static String extraMiningTools = "";
	@Entry public static boolean disableMineralDropsWithSilkTouch = true;
	@Entry public static boolean sendMessageOnMineralFind = true;
	@Entry public static String foundMineralMessage = "You've found a mineral hidden in the block!";
	@Entry public static boolean ignoreFakePlayers = true;

	public static void initConfig() {
		configMetaData.put("extraMineralChanceOnOverworldStoneBreak", Arrays.asList(
			"The chance a mineral is dropped when an overworld stone block is broken. By default 1/50."
		));
		configMetaData.put("extraMineralChanceOnNetherStoneBreak", Arrays.asList(
			"The chance a mineral is dropped when a nether stone block is broken. By default 1/100."
		));
		configMetaData.put("enableOverworldMinerals", Arrays.asList(
			"If enabled, mining overworld stone blocks has a chance to drop an overworld mineral."
		));
		configMetaData.put("enableNetherMinerals", Arrays.asList(
			"If enabled, mining nether stone blocks has a chance to drop a nether mineral."
		));
		configMetaData.put("overworldMineralDrops", Arrays.asList(
			"The minerals overworld stone can drop. A higher weight drops more often. Item tags work too, starting with a '#'."
		));
		configMetaData.put("netherMineralDrops", Arrays.asList(
			"The minerals nether stone can drop. A higher weight drops more often. Item tags work too, starting with a '#'."
		));
		configMetaData.put("extraOverworldMineralBlocks", Arrays.asList(
			"Extra blocks that can drop overworld minerals. Block tags work too, starting with a '#'."
		));
		configMetaData.put("extraNetherMineralBlocks", Arrays.asList(
			"Extra blocks that can drop nether minerals. Block tags work too, starting with a '#'."
		));
		configMetaData.put("enableMineralsInOtherDimensions", Arrays.asList(
			"If enabled, stone blocks in other dimensions, such as the end, can drop minerals too."
		));
		configMetaData.put("dimensionsWithoutMinerals", Arrays.asList(
			"Dimensions in which no minerals are dropped."
		));
		configMetaData.put("fortuneChanceMultiplierPerLevel", Arrays.asList(
			"How much each Fortune level increases the mineral chance. 0.5 means +50% per level."
		));
		configMetaData.put("explosionsCanDropMinerals", Arrays.asList(
			"If enabled, stone blocks destroyed by explosions can drop minerals too."
		));
		configMetaData.put("extraMiningTools", Arrays.asList(
			"Extra items that count as a pickaxe, such as mining lasers. Item tags work too, starting with a '#'."
		));
		configMetaData.put("disableMineralDropsWithSilkTouch", Arrays.asList(
			"If enabled, minerals won't drop from stone mined with the silk touch enchantment."
		));
		configMetaData.put("sendMessageOnMineralFind", Arrays.asList(
			"If enabled, sends a message when a mineral is found to the player who broke the stone block."
		));
		configMetaData.put("foundMineralMessage", Arrays.asList(
			"The message sent to the player who found a hidden mineral when 'sendMessageOnMineralFind' is enabled."
		));
		configMetaData.put("ignoreFakePlayers", Arrays.asList(
			"If enabled, minerals won't be dropped if the player is a fake. For example when a mod breaks a block as a simulated player."
		));

		DuskConfig.init(Reference.NAME, Reference.MOD_ID, ConfigHandler.class);
	}
}