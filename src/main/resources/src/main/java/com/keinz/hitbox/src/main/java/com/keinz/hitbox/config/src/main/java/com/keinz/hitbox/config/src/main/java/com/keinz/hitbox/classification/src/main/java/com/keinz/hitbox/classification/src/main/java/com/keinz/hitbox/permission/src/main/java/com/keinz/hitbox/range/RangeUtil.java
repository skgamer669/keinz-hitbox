package com.keinz.hitbox.range;

import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;

/**
 * Reads the local player's actual attack/interaction range attribute rather
 * than hardcoding a distance, per the spec (combat range differs by version
 * and by effects/enchantments on the player).
 *
 * VERIFY BEFORE BUILDING: Mojang's 1.20.5+ combat rework exposes attack
 * distance as an entity attribute. The exact Yarn-mapped identifier for
 * 1.21.1 is EntityAttributes.PLAYER_ENTITY_INTERACTION_RANGE in the mappings
 * generation used when this file was written, but Yarn field names do shift
 * between builds. If this doesn't compile in your IDE:
 *   1. Open EntityAttributes.java (Ctrl/Cmd-click through from an import),
 *   2. find the constant for "entity_interaction_range" (its registry key
 *      won't change even if the Java field name does),
 *   3. swap the constant below to match.
 * As a last resort, FALLBACK_RANGE is used if the attribute lookup fails.
 */
public final class RangeUtil {

	/** Vanilla default player attack range as of the 1.20.5+ combat rework. */
	private static final double FALLBACK_RANGE = 3.0;

	private RangeUtil() {
	}

	public static double getAttackRange(PlayerEntity player) {
		try {
			return player.getAttributeValue(EntityAttributes.PLAYER_ENTITY_INTERACTION_RANGE);
		} catch (Throwable t) {
			return FALLBACK_RANGE;
		}
	}

	public static boolean isInAttackRange(PlayerEntity self, net.minecraft.entity.Entity target) {
		double range = getAttackRange(self);
		double distSq = self.squaredDistanceTo(target);
		return distSq <= range * range;
	}
}
