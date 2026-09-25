package com.keinz.hitbox.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.keinz.hitbox.config.ConfigManager;
import com.keinz.hitbox.config.KeinzConfig;
import com.keinz.hitbox.permission.PermissionManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.Text;

import java.util.List;
import java.util.UUID;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

/**
 * All subcommands run client-side only (this is a client-only mod: there's
 * no server component to talk to). "team add/remove/clear" additionally
 * require the executor's UUID to be in the authorizedUsers list — "team
 * list" and "hitbox *" are open to everyone since they don't mutate shared
 * state (the roster is local to your own client anyway).
 */
public final class KeinzCommand {

	private KeinzCommand() {
	}

	public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
		dispatcher.register(literal("keinz")
				.then(literal("team")
						.then(literal("list").executes(KeinzCommand::teamList))
						.then(literal("add")
								.then(argument("player", StringArgumentType.word())
										.executes(ctx -> teamAdd(ctx.getSource(), StringArgumentType.getString(ctx, "player")))))
						.then(literal("remove")
								.then(argument("player", StringArgumentType.word())
										.executes(ctx -> teamRemove(ctx.getSource(), StringArgumentType.getString(ctx, "player")))))
						.then(literal("clear").executes(KeinzCommand::teamClear)))
				.then(literal("hitbox")
						.then(literal("on").executes(ctx -> setEnabled(ctx.getSource(), true)))
						.then(literal("off").executes(ctx -> setEnabled(ctx.getSource(), false)))
						.then(literal("status").executes(KeinzCommand::hitboxStatus))));
	}

	private static int teamList(com.mojang.brigadier.context.CommandContext<FabricClientCommandSource> ctx) {
		KeinzConfig config = ConfigManager.get();
		FabricClientCommandSource source = ctx.getSource();
		if (config.friendlyPlayers.isEmpty()) {
			source.sendFeedback(Text.literal("Keinz AS: no clanmates configured yet."));
			return 1;
		}
		source.sendFeedback(Text.literal("Keinz AS Clan Roster:"));
		int i = 1;
		for (KeinzConfig.ClanMember member : config.friendlyPlayers) {
			source.sendFeedback(Text.literal(String.format("%d. %s (%s)", i++, member.lastKnownName, member.uuid)));
		}
		return 1;
	}

	private static int teamAdd(FabricClientCommandSource source, String playerName) {
		KeinzConfig config = ConfigManager.get();
		if (!PermissionManager.isAuthorized(config)) {
			source.sendError(Text.literal("You do not have permission to manage Keinz teams."));
			return 0;
		}

		UUID resolved = resolveUuid(source, playerName);
		if (resolved == null) {
			source.sendError(Text.literal("Could not find '" + playerName
					+ "' — they must be visible in your tab list (on the same server) to be added."));
			return 0;
		}

		if (config.isFriendly(resolved)) {
			source.sendError(Text.literal("Team already exists."));
			return 0;
		}

		config.friendlyPlayers.add(new KeinzConfig.ClanMember(resolved, playerName));
		ConfigManager.save();
		source.sendFeedback(Text.literal("Added " + playerName + " to the clan roster."));
		return 1;
	}

	private static int teamRemove(FabricClientCommandSource source, String playerName) {
		KeinzConfig config = ConfigManager.get();
		if (!PermissionManager.isAuthorized(config)) {
			source.sendError(Text.literal("You do not have permission to manage Keinz teams."));
			return 0;
		}

		boolean removed = config.friendlyPlayers.removeIf(
				m -> m.lastKnownName != null && m.lastKnownName.equalsIgnoreCase(playerName));

		if (!removed) {
			source.sendError(Text.literal("Team not found."));
			return 0;
		}

		ConfigManager.save();
		source.sendFeedback(Text.literal("Removed " + playerName + " from the clan roster."));
		return 1;
	}

	private static int teamClear(com.mojang.brigadier.context.CommandContext<FabricClientCommandSource> ctx) {
		FabricClientCommandSource source = ctx.getSource();
		KeinzConfig config = ConfigManager.get();
		if (!PermissionManager.isAuthorized(config)) {
			source.sendError(Text.literal("You do not have permission to manage Keinz teams."));
			return 0;
		}
		config.friendlyPlayers.clear();
		ConfigManager.save();
		source.sendFeedback(Text.literal("Cleared the clan roster."));
		return 1;
	}

	private static int setEnabled(FabricClientCommandSource source, boolean enabled) {
		KeinzConfig config = ConfigManager.get();
		config.enabled = enabled;
		ConfigManager.save();
		source.sendFeedback(Text.literal("Keinz hitbox overlay: " + (en
