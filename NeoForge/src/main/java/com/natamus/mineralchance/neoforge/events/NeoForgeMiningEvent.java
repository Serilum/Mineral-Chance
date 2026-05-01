package com.natamus.mineralchance.neoforge.events;

import com.natamus.collective.functions.WorldFunctions;
import com.natamus.mineralchance.events.MiningEvent;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.level.block.BreakBlockEvent;

public class NeoForgeMiningEvent {
	@SubscribeEvent
	public static void onBlockBreak(BreakBlockEvent e) {
		Level level = WorldFunctions.getWorldIfInstanceOfAndNotRemote(e.getLevel());
		if (level == null) {
			return;
		}

		MiningEvent.onBlockBreak(level, e.getPlayer(), e.getPos(), e.getState(), null);
	}
}
