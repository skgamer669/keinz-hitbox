package com.keinz.hitbox.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Loads/saves {@link KeinzConfig} to <run>/config/keinz-hitbox.json.
 *
 * This file is meant to be hand-editable. To add a clanmate manually without
 * using the in-game command, add an entry to "friendlyPlayers" like:
 *   { "uuid": "xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx", "lastKnownName": "SomeName" }
 * To authorize yourself/officers to manage the roster in-game, add your UUID
 * (not username) to "authorizedUsers". Use a UUID lookup tool (e.g. an
 * official Mojang API mirror) to find a player's UUID from their username.
 */
public final class ConfigManager {

	private static final Logger LOGGER = LoggerFactory.getLogger("keinz-hitbox");
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	private static KeinzConfig instance;
	private static Path configPath;

	private ConfigManager() {
	}

	public static synchronized KeinzConfig get() {
		if (instance == null) {
			load();
		}
		return instance;
	}

	private static Path resolvePath() {
		if (configPath == null) {
			configPath = FabricLoader.getInstance().getConfigDir().resolve("keinz-hitbox.json");
		}
		return configPath;
	}

	public static synchronized void load() {
		Path path = resolvePath();
		if (Files.exists(path)) {
			try {
				String json = Files.readString(path, StandardCharsets.UTF_8);
				KeinzConfig loaded = GSON.fromJson(json, KeinzConfig.class);
				instance = loaded != null ? loaded : new KeinzConfig();
			} catch (IOException | RuntimeException e) {
				LOGGER.error("Failed to read keinz-hitbox.json, using defaults. Fix or delete the file to reset.", e);
				instance = new KeinzConfig();
			}
		} else {
			instance = new KeinzConfig();
			save();
		}
	}

	public static synchronized void save() {
		if (instance == null) {
			return;
		}
		Path path = resolvePath();
		try {
			Files.createDirectories(path.getParent());
			Files.writeString(path, GSON.toJson(instance), StandardCharsets.UTF_8);
		} catch (IOException e) {
			LOGGER.error("Failed to save keinz-hitbox.json", e);
		}
	}
}
