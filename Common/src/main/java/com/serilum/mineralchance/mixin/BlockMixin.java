package com.serilum.mineralchance.mixin;

import com.serilum.mineralchance.events.MiningEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Block.class, priority = 1001)
public class BlockMixin {
	@Inject(method = "playerDestroy(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/item/ItemStack;)V", at = @At(value = "HEAD"))
	public void playerDestroy(ServerLevel level, ServerPlayer player, BlockPos pos, BlockState state, BlockEntity blockEntity, ItemStack destroyedWith, CallbackInfo ci) {
		MiningEvent.onPlayerDestroy(level, player, pos, state, destroyedWith);
	}
}
