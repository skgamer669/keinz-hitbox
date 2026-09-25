package com.keinz.hitbox.classification;

import com.keinz.hitbox.config.KeinzConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;

import java.util.UUID;

/**
 * Roster-based friend/enemy classification.
 *
 * Never classify the local player themselves, and never classify anyone as
 * FRIEND except by explicit UUID match against the configured roster — no
 * inference from name, skin, or server-provided team data, since that data
 * often isn't available on servers this mod is meant to be used on.
 */
public final class ClassificationService {

	private ClassificationService() {
	}

	public static PlayerStatus classify(AbstractClientPlayerEntity player, KeinzConfig config) {
		MinecraftClient client = MinecraftClient.getInstance();
		if (client.player != null && player.getUuid().equals(client.player.getUuid())) {
			// Never overlay yourself.
			return PlayerStatus.FRIEND;
		}

		UUID id = player.getUuid();
		return config.isFriendly(id) ? PlayerStatus.FRIEND : PlayerStatus.ENEMY;
	}
}
