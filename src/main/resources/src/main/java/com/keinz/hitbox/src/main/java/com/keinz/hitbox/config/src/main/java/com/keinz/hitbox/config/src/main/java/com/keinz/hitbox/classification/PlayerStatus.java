package com.keinz.hitbox.classification;

/**
 * FRIEND players get no overlay at all (vanilla-only). Everyone else is
 * ENEMY, since on a contested public server an unrecognized player is not
 * assumed neutral. (The original "unknown = green" concept from a
 * scoreboard-team design doesn't apply here — see ClassificationService.)
 */
public enum PlayerStatus {
	FRIEND,
	ENEMY
}
