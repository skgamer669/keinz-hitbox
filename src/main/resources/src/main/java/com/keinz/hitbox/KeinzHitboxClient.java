package com.keinz.hitbox;

import com.keinz.hitbox.command.KeinzCommand;
import com.keinz.hitbox.config.ConfigManager;
import com.keinz.hitbox.render.HitboxRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KeinzHitboxClient implements ClientModInitializer {

	private static final Logger LOGGER = LoggerFactory.getLogger("keinz-hitbox");

	@Override
	public void onInitializeClient() {
		ConfigManager.load();

		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
				KeinzCommand.register(dispatcher));

		WorldRenderEvents.AFTER_ENTITIES.register(HitboxRenderer::onRender);

		LOGGER.info("Keinz AS Enemy Hitbox loaded.");
		LOGGER.info("Clanmates in roster: {}", ConfigManager.get().friendlyPlayers.size());
		LOGGER.info("Authorized roster managers: {}", ConfigManager.get().authorizedUsers.size());
	}
}
