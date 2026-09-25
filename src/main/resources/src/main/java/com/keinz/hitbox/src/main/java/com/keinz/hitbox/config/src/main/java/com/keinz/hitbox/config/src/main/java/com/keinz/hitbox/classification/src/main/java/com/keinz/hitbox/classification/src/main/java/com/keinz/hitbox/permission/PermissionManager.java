package com.keinz.hitbox.permission;

import com.keinz.hitbox.config.KeinzConfig;
import net.minecraft.client.MinecraftClient;

import java.util.UUID;

/**
 * Authorization is by UUID only, never by display name. If the local
 * player's UUID is unavailable for any reason, this fails closed (denies).
 */
public final class PermissionManager {

	private PermissionManager() {
	}

	public static boolean isAuthorized(KeinzConfig config) {
		MinecraftClient client = MinecraftClient.getInstance();
		if (client.player == null) {
			return false; // fail safe: no player context, no permission
		}
		UUID id = client.player.getUuid();
		if (id == null) {
			return false; // fail safe
		}
		return config.isAuthorized(id);
	}
}
