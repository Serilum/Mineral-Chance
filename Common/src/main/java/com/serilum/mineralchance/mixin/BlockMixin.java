package com.serilum.mineralchance.mixin;

import com.serilum.mineralchance.events.MiningEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Block.class, priority = 1001)
public class BlockMixin {
	@Inject(method = "playerDestroy(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/item/ItemStack;)V", at = @At(value = "HEAD"))
	public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, BlockEntity blockEntity, ItemStack destroyedWith, CallbackInfo ci) {
		MiningEvent.onPlayerDestroy(level, player, pos, state, destroyedWith);
	}

	@Inject(method = "wasExploded(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/Explosion;)V", at = @At(value = "HEAD"))
	public void wasExploded(Level level, BlockPos pos, Explosion explosion, CallbackInfo ci) {
		MiningEvent.onBlockExploded(level, pos, ((Block)(Object)this).defaultBlockState());
	}
}
