package com.serilum.mineralchance.util;

import com.natamus.collective.data.GlobalVariables;
import com.natamus.collective.functions.CompareBlockFunctions;
import com.natamus.collective.functions.ConfigFunctions;
import com.natamus.collective.functions.WorldFunctions;
import com.natamus.collective.objects.IdAmount;
import com.natamus.collective.services.Services;
import com.serilum.mineralchance.config.ConfigHandler;
import com.serilum.mineralchance.data.Variables;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

public class Util {
	public static boolean isMineralBlock(Level level, BlockState blockState) {
		return isOverworldMineralBlock(level, blockState) || isNetherMineralBlock(level, blockState);
	}

	public static boolean isOverworldMineralBlock(Level level, BlockState blockState) {
		return CompareBlockFunctions.isStoneBlock(blockState.getBlock()) || isInBlockList(level, ConfigHandler.extraOverworldMineralBlocks, blockState);
	}

	public static boolean isNetherMineralBlock(Level level, BlockState blockState) {
		return CompareBlockFunctions.isNetherStoneBlock(blockState.getBlock()) || isInBlockList(level, ConfigHandler.extraNetherMineralBlocks, blockState);
	}

	public static boolean isInBlockList(Level level, String blockList, BlockState blockState) {
		if (blockList.isBlank()) {
			return false;
		}

		Identifier blockId = level.registryAccess().lookupOrThrow(Registries.BLOCK).getKey(blockState.getBlock());
		if (ConfigFunctions.isInIdList(blockList, blockId.toString())) {
			return true;
		}

		for (String rawEntry : blockList.split(",")) {
			String entry = rawEntry.strip();
			if (!entry.startsWith("#")) {
				continue;
			}

			Identifier tagId = Identifier.tryParse(entry.substring(1));
			if (tagId != null && blockState.is(TagKey.create(Registries.BLOCK, tagId))) {
				return true;
			}
		}
		return false;
	}

	public static boolean isMiningTool(Level level, ItemStack itemStack, BlockState blockState) {
		return Services.TOOLFUNCTIONS.isPickaxe(itemStack) || itemStack.isCorrectToolForDrops(blockState) || isInItemList(level, ConfigHandler.extraMiningTools, itemStack);
	}

	public static boolean isInItemList(Level level, String itemList, ItemStack itemStack) {
		if (itemList.isBlank()) {
			return false;
		}

		Identifier itemId = level.registryAccess().lookupOrThrow(Registries.ITEM).getKey(itemStack.getItem());
		if (ConfigFunctions.isInIdList(itemList, itemId.toString())) {
			return true;
		}

		for (String rawEntry : itemList.split(",")) {
			String entry = rawEntry.strip();
			if (!entry.startsWith("#")) {
				continue;
			}

			Identifier tagId = Identifier.tryParse(entry.substring(1));
			if (tagId != null && itemStack.is(TagKey.create(Registries.ITEM, tagId))) {
				return true;
			}
		}
		return false;
	}

	public static boolean hasSilkTouch(Level level, ItemStack itemStack) {
		return getEnchantmentLevel(level, Enchantments.SILK_TOUCH, itemStack) >= 1;
	}

	public static double getFortuneChanceMultiplier(Level level, ItemStack itemStack) {
		int fortuneLevel = getEnchantmentLevel(level, Enchantments.FORTUNE, itemStack);
		return 1.0 + (fortuneLevel * ConfigHandler.fortuneChanceMultiplierPerLevel);
	}

	private static int getEnchantmentLevel(Level level, ResourceKey<Enchantment> enchantment, ItemStack itemStack) {
		ItemEnchantments itemEnchantments = itemStack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
		return itemEnchantments.getLevel(level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(enchantment));
	}

	public static Item getRandomMineral(Level level, BlockState blockState, double chanceMultiplier) {
		if (ConfigFunctions.isInIdList(ConfigHandler.dimensionsWithoutMinerals, WorldFunctions.getWorldDimensionName(level))) {
			return null;
		}

		boolean useNetherMinerals;
		if (WorldFunctions.isOverworld(level)) {
			if (!isOverworldMineralBlock(level, blockState)) {
				return null;
			}
			useNetherMinerals = false;
		}
		else if (WorldFunctions.isNether(level)) {
			if (!isNetherMineralBlock(level, blockState)) {
				return null;
			}
			useNetherMinerals = true;
		}
		else if (ConfigHandler.enableMineralsInOtherDimensions) {
			useNetherMinerals = isNetherMineralBlock(level, blockState);
		}
		else {
			return null;
		}

		if (useNetherMinerals) {
			if (!ConfigHandler.enableNetherMinerals) {
				return null;
			}
			if (GlobalVariables.random.nextDouble() > ConfigHandler.extraMineralChanceOnNetherStoneBreak * chanceMultiplier) {
				return null;
			}
			return getRandomWeightedItem(level, ConfigHandler.netherMineralDrops);
		}

		if (!ConfigHandler.enableOverworldMinerals) {
			return null;
		}
		if (GlobalVariables.random.nextDouble() > ConfigHandler.extraMineralChanceOnOverworldStoneBreak * chanceMultiplier) {
			return null;
		}
		return getRandomWeightedItem(level, ConfigHandler.overworldMineralDrops);
	}

	public static Item getRandomWeightedItem(Level level, String weightedItems) {
		Registry<Item> itemRegistry = level.registryAccess().lookupOrThrow(Registries.ITEM);

		List<List<Item>> entryItems = new ArrayList<>();
		List<Integer> entryWeights = new ArrayList<>();
		int totalWeight = 0;

		for (IdAmount idAmount : ConfigFunctions.getIdAmounts(weightedItems)) {
			List<Item> items = getItemsForEntry(itemRegistry, idAmount.id);
			if (items.isEmpty() || idAmount.maximum <= 0) {
				continue;
			}

			entryItems.add(items);
			entryWeights.add(idAmount.maximum);
			totalWeight += idAmount.maximum;
		}

		if (totalWeight <= 0) {
			return null;
		}

		int roll = GlobalVariables.random.nextInt(totalWeight);
		for (int i = 0; i < entryItems.size(); i++) {
			roll -= entryWeights.get(i);
			if (roll < 0) {
				List<Item> items = entryItems.get(i);
				return items.get(GlobalVariables.random.nextInt(items.size()));
			}
		}
		return null;
	}

	private static List<Item> getItemsForEntry(Registry<Item> itemRegistry, String entry) {
		List<Item> items = new ArrayList<>();

		if (entry.startsWith("#")) {
			Identifier tagId = Identifier.tryParse(entry.substring(1));
			if (tagId == null) {
				return items;
			}

			for (Holder<Item> itemHolder : itemRegistry.getTagOrEmpty(TagKey.create(Registries.ITEM, tagId))) {
				items.add(itemHolder.value());
			}
			return items;
		}

		Identifier itemId = Identifier.tryParse(entry);
		if (itemId != null && itemRegistry.containsKey(itemId)) {
			items.add(itemRegistry.getValue(itemId));
		}
		return items;
	}

	public static String getPositionKey(Level level, BlockPos blockPos) {
		return WorldFunctions.getWorldDimensionName(level) + "@" + blockPos.asLong();
	}

	public static void markHarvestedByPlayerDestroy(Level level, BlockPos blockPos) {
		long gameTime = level.getGameTime();
		if (Variables.playerDestroyTimePerPosition.size() > 1000) {
			Variables.playerDestroyTimePerPosition.values().removeIf(destroyTime -> destroyTime < gameTime - 20);
		}
		Variables.playerDestroyTimePerPosition.put(getPositionKey(level, blockPos), gameTime);
	}

	public static boolean wasHarvestedByPlayerDestroy(Level level, BlockPos blockPos, long breakTime) {
		Long destroyTime = Variables.playerDestroyTimePerPosition.remove(getPositionKey(level, blockPos));
		return destroyTime != null && destroyTime >= breakTime;
	}

	public static boolean isBlockGone(Level level, BlockPos blockPos) {
		return level.getBlockState(blockPos).isAir() || !level.getFluidState(blockPos).isEmpty();
	}
}
