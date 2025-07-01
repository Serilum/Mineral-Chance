package com.natamus.mineralchance.events;

import com.natamus.collective.data.GlobalVariables;
import com.natamus.collective.fakeplayer.FakePlayer;
import com.natamus.collective.functions.CompareBlockFunctions;
import com.natamus.collective.functions.MessageFunctions;
import com.natamus.collective.functions.WorldFunctions;
import com.natamus.collective.services.Services;
import com.natamus.mineralchance.config.ConfigHandler;
import com.natamus.mineralchance.util.Util;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class MiningEvent {
	public static void onBlockBreak(Level level, Player player, BlockPos blockPos, BlockState blockState, BlockEntity blockEntity) {
		if (level.isClientSide) {
			return;
		}
		
		if (ConfigHandler.ignoreFakePlayers) {
			if (player instanceof FakePlayer) {
				return;
			}
		}
		
		if (player.isCreative()) {
			return;
		}

		ItemStack handStack = player.getItemInHand(InteractionHand.MAIN_HAND);
		if (!Services.TOOLFUNCTIONS.isPickaxe(handStack)) {
			return;
		}

		if (ConfigHandler.disableMineralDropsWithSilkTouch) {
			if (EnchantmentHelper.getItemEnchantmentLevel(level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.SILK_TOUCH), handStack) >= 1) {
				return;
			}
		}
		
		Block block = blockState.getBlock();
		if (!CompareBlockFunctions.isStoneBlock(block) && !CompareBlockFunctions.isNetherStoneBlock(block)) {
			return;
		}
		
		Item randomMineralItem;
		if (WorldFunctions.isOverworld(level)) {
			if (!ConfigHandler.enableOverworldMinerals) {
				return;
			}
			if (CompareBlockFunctions.isNetherStoneBlock(block)) {
				return;
			}
			if (GlobalVariables.random.nextDouble() > ConfigHandler.extraMineralChanceOnOverworldStoneBreak) {
				return;
			}
			randomMineralItem = Util.getRandomOverworldMineral();
		}
		else if (WorldFunctions.isNether(level)) {
			if (!ConfigHandler.enableNetherMinerals) {
				return;
			}
			if (!CompareBlockFunctions.isNetherStoneBlock(block)) {
				return;
			}
			if (GlobalVariables.random.nextDouble() > ConfigHandler.extraMineralChanceOnNetherStoneBreak) {
				return;
			}
			randomMineralItem = Util.getRandomNetherMineral();
		}
		else {
			return;
		}
		
		ItemEntity mineralItemEntity = new ItemEntity(level, blockPos.getX()+0.5, blockPos.getY()+0.5, blockPos.getZ()+0.5, new ItemStack(randomMineralItem, 1));
		level.addFreshEntity(mineralItemEntity);
		
		if (ConfigHandler.sendMessageOnMineralFind) {
			if (((ServerPlayer)player).connection != null) {
				MessageFunctions.sendMessage(player, ConfigHandler.foundMineralMessage, ChatFormatting.DARK_GREEN);
			}
		}
	}
}
