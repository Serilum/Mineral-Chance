package com.natamus.mineralchance.forge.events;

import com.natamus.collective.functions.WorldFunctions;
import com.natamus.mineralchance.events.MiningEvent;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;

import java.lang.invoke.MethodHandles;

public class ForgeMiningEvent {
	public static void registerEventsInBus() {
		// BusGroup.DEFAULT.register(MethodHandles.lookup(), ForgeMiningEvent.class);

		BlockEvent.BreakEvent.BUS.addListener(ForgeMiningEvent::onBlockBreak);
	}

	@SubscribeEvent
	public static void onBlockBreak(BlockEvent.BreakEvent e) {
		Level level = WorldFunctions.getWorldIfInstanceOfAndNotRemote(e.getLevel());
		if (level == null) {
			return;
		}

		MiningEvent.onBlockBreak(level, e.getPlayer(), e.getPos(), e.getState(), null);
	}
}
