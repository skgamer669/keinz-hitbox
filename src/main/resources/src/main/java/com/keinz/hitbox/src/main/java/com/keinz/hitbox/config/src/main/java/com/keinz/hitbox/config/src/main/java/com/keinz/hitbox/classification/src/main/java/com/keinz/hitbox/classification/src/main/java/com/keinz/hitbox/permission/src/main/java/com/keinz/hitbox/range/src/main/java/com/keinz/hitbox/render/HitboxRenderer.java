package com.keinz.hitbox.render;

import com.keinz.hitbox.classification.ClassificationService;
import com.keinz.hitbox.classification.PlayerStatus;
import com.keinz.hitbox.config.ConfigManager;
import com.keinz.hitbox.config.KeinzConfig;
import com.keinz.hitbox.range.RangeUtil;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

/**
 * Draws an additional colored line-box around clan-unrecognized players.
 * This is purely additive: it never disables, replaces, or hides vanilla
 * hitbox rendering (F3+B), and it never changes any entity's actual
 * bounding box, collision, or attack reach — it only draws lines using the
 * entity's existing getBoundingBox().
 *
 * Friendly players (and the local player) get no overlay at all. This
 * intentionally does nothing for non-player entities (mobs, item frames,
 * etc.) per spec.
 */
public final class HitboxRenderer {

	private HitboxRenderer() {
	}

	public static void onRender(WorldRenderContext context) {
		KeinzConfig config = ConfigManager.get();
		if (!config.enabled) {
			return;
		}

		MinecraftClient client = MinecraftClient.getInstance();
		if (client.world == null || client.player == null) {
			return;
		}

		VertexConsumerProvider.Immediate consumers = client.getBufferBuilders().getEntityVertexConsumers();
		VertexConsumer buffer = consumers.getBuffer(RenderLayer.getLines());
		MatrixStack matrices = context.matrixStack();
		Vec3d camera = context.camera().getPos();

		for (AbstractClientPlayerEntity player : client.world.getPlayers()) {
			PlayerStatus status = ClassificationService.classify(player, config);
			if (status != PlayerStatus.ENEMY) {
				continue; // friendly / self: no overlay, vanilla rendering handles it
			}

			int colorArgb = config.colors.enemy;
			if (config.changeEnemyColorInRange && RangeUtil.isInAttackRange(client.player, player)) {
				colorArgb = config.colors.inRange;
			}

			float r = ((colorArgb >> 16) & 0xFF) / 255f;
			float g = ((colorArgb >> 8) & 0xFF) / 255f;
			float b = (colorArgb & 0xFF) / 255f;

			Box worldBox = player.getBoundingBox();
			Box relativeBox = worldBox.offset(-camera.x, -camera.y, -camera.z);

			matrices.push();
			WorldRenderer.drawBox(
					matrices,
					buffer,
					relativeBox,
					r, g, b, 1.0f
			);
			matrices.pop();
		}

		consumers.draw(RenderLayer.getLines());
	}
        }
