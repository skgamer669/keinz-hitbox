package com.keinz.hitbox.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * Plain data model persisted to config/keinz-hitbox.json.
 *
 * Classification is roster-based, not scoreboard-based: on public servers you
 * don't control, the server may not expose a scoreboard team for your clan at
 * all, so scoreboard team name matching is not a reliable signal. Instead you
 * maintain a local list of clanmates (by UUID) here; anyone not on the list is
 * treated as an enemy.
 */
public class KeinzConfig {

	public boolean enabled = false;

	/** Your clanmates. Order doesn't matter; lookups are by UUID. */
	public List<ClanMember> friendlyPlayers = new ArrayList<>();

	/** UUIDs allowed to run /keinz team add|remove|clear. Populate this yourself. */
	public List<String> authorizedUsers = new ArrayList<>();

	public ColorConfig colors = new ColorConfig();

	/** If true, an enemy's overlay turns colors.inRange while in attack range. */
	public boolean changeEnemyColorInRange = true;

	public static class ClanMember {
		public String uuid;
		public String lastKnownName;

		public ClanMember() {
		}

		public ClanMember(UUID uuid, String lastKnownName) {
			this.uuid = uuid.toString();
			this.lastKnownName = lastKnownName;
		}
	}

	public static class ColorConfig {
		/** 0xRRGGBB */
		public int enemy = 0xFF3B30;   // red
		public int inRange = 0xFFFFFF; // white
	}

	public boolean isFriendly(UUID playerId) {
		String target = playerId.toString().toLowerCase(Locale.ROOT);
		for (ClanMember member : friendlyPlayers) {
			if (member.uuid != null && member.uuid.toLowerCase(Locale.ROOT).equals(target)) {
				return true;
			}
		}
		return false;
	}

	public boolean isAuthorized(UUID playerId) {
		String target = playerId.toString().toLowerCase(Locale.ROOT);
		for (String uuid : authorizedUsers) {
			if (uuid != null && uuid.toLowerCase(Locale.ROOT).equals(target)) {
				return true;
			}
		}
		return false;
	}
          }
