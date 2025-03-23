package com.natamus.mineralchance;

import com.natamus.collective.check.RegisterMod;
import com.natamus.collective.check.ShouldLoadCheck;
import com.natamus.mineralchance.neoforge.config.IntegrateNeoForgeConfig;
import com.natamus.mineralchance.neoforge.events.NeoForgeMiningEvent;
import com.natamus.mineralchance.util.Reference;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(Reference.MOD_ID)
public class ModNeoForge {

	private static final Logger LOGGER = LoggerFactory.getLogger("Util");

	public ModNeoForge(IEventBus modEventBus) {
		LOGGER.warn("Mineral Chance Mod Loaded");
		if (!ShouldLoadCheck.shouldLoad(Reference.MOD_ID)) {
			return;
		}

		modEventBus.addListener(this::loadComplete);

		setGlobalConstants();
		ModCommon.init();

		IntegrateNeoForgeConfig.registerScreen(ModLoadingContext.get());

		RegisterMod.register(Reference.NAME, Reference.MOD_ID, Reference.VERSION, Reference.ACCEPTED_VERSIONS);
	}

	private void loadComplete(final FMLLoadCompleteEvent event) {
		NeoForge.EVENT_BUS.register(NeoForgeMiningEvent.class);
	}

	private static void setGlobalConstants() {

	}
}