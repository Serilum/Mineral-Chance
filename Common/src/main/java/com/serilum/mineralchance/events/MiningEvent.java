package com.serilum.mineralchance.events;

import com.natamus.collective.fakeplayer.FakePlayer;
import com.natamus.collective.functions.MessageFunctions;
import com.natamus.collective.functions.TaskFunctions;
import com.serilum.mineralchance.config.ConfigHandler;
import com.serilum.mineralchance.util.Util;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.BiConsumer;

public class MiningEvent {
	public static void onBlockBreak(Level level, Player player, BlockPos blockPos, BlockState blockState, BlockEntity blockEntity) {
		if (!(level instanceof ServerLevel serverLevel)) {
			return;
		}

		if (!Util.isMineralBlock(level, blockState)) {
			return;
		}

		ItemStack handStack = player.getMainHandItem().copy();
		long breakTime = level.getGameTime();

		TaskFunctions.enqueueCollectiveServerTask(serverLevel.getServer(), () -> {
			if (Util.wasHarvestedByPlayerDestroy(level, blockPos, breakTime)) {
				return;
			}

			if (!Util.isBlockGone(level, blockPos)) {
				return;
			}

			onBlockHarvest(serverLevel, player, blockPos, blockState, handStack);
		}, 1);
	}

	public static void onPlayerDestroy(Level level, Player player, BlockPos blockPos, BlockState blockState, ItemStack destroyedWith) {
		if (!(level instanceof ServerLevel serverLevel)) {
			return;
		}

		if (!Util.isMineralBlock(level, blockState)) {
			return;
		}

		Util.markHarvestedByPlayerDestroy(level, blockPos);
		onBlockHarvest(serverLevel, player, blockPos, blockState, destroyedWith);
	}

	public static void onBlockHarvest(ServerLevel serverLevel, Player player, BlockPos blockPos, BlockState blockState, ItemStack tool) {
		if (ConfigHandler.ignoreFakePlayers) {
			if (player instanceof FakePlayer) {
				return;
			}
		}

		if (player.isCreative()) {
			return;
		}

		if (!Util.isMiningTool(serverLevel, tool, blockState)) {
			return;
		}

		if (ConfigHandler.disableMineralDropsWithSilkTouch) {
			if (Util.hasSilkTouch(serverLevel, tool)) {
				return;
			}
		}

		Item randomMineralItem = Util.getRandomMineral(serverLevel, blockState, Util.getFortuneChanceMultiplier(serverLevel, tool));
		if (randomMineralItem == null) {
			return;
		}

		ItemEntity mineralItemEntity = new ItemEntity(serverLevel, blockPos.getX()+0.5, blockPos.getY()+0.5, blockPos.getZ()+0.5, new ItemStack(randomMineralItem, 1));
		serverLevel.addFreshEntity(mineralItemEntity);

		if (ConfigHandler.sendMessageOnMineralFind) {
			if (player instanceof ServerPlayer serverPlayer && serverPlayer.connection != null) {
				MessageFunctions.sendMessage(player, ConfigHandler.foundMineralMessage, ChatFormatting.DARK_GREEN);
			}
		}
	}

	public static void onExplosionHit(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, Explosion explosion, BiConsumer<ItemStack, BlockPos> dropConsumer) {
		if (!ConfigHandler.explosionsCanDropMinerals) {
			return;
		}

		if (explosion.getBlockInteraction() == Explosion.BlockInteraction.TRIGGER_BLOCK) {
			return;
		}

		if (!Util.isMineralBlock(serverLevel, blockState)) {
			return;
		}

		Item randomMineralItem = Util.getRandomMineral(serverLevel, blockState, 1.0);
		if (randomMineralItem == null) {
			return;
		}

		dropConsumer.accept(new ItemStack(randomMineralItem, 1), blockPos);
	}
}
