package pl.pvphelper;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

/** Rysuje WYPELNIONY (nie tylko obrys) prawdziwy hitbox innych graczy. */
public final class HitboxRenderer {
	private HitboxRenderer() {}

	private static final double MAX_DIST = 64.0;

	public static void register() {
		WorldRenderEvents.AFTER_ENTITIES.register(HitboxRenderer::render);
	}

	private static void render(WorldRenderContext ctx) {
		if (!PvpConfig.filledHitbox) return;
		Minecraft mc = Minecraft.getInstance();
		if (mc.level == null || mc.player == null) return;

		MultiBufferSource consumers = ctx.consumers();
		PoseStack pose = ctx.matrixStack();
		if (consumers == null || pose == null) return;

		Vec3 cam = ctx.camera().getPosition();
		float pt = ctx.tickCounter().getGameTimeDeltaPartialTick(false);
		Entity looked = mc.hitResult instanceof EntityHitResult ehr ? ehr.getEntity() : null;
		int alpha = Math.round(PvpConfig.alphaPercent * 2.55f);

		pose.pushPose();
		pose.translate(-cam.x, -cam.y, -cam.z);
		Matrix4f mat = pose.last().pose();
		VertexConsumer vc = consumers.getBuffer(RenderType.debugQuads());

		for (Player p : mc.level.players()) {
			if (p == mc.player || p.isSpectator()) continue;
			if (p.distanceToSqr(mc.player) > MAX_DIST * MAX_DIST) continue;

			double dx = Mth.lerp((double) pt, p.xo, p.getX()) - p.getX();
			double dy = Mth.lerp((double) pt, p.yo, p.getY()) - p.getY();
			double dz = Mth.lerp((double) pt, p.zo, p.getZ()) - p.getZ();
			AABB box = p.getBoundingBox().move(dx, dy, dz);

			int[] c = (p == looked) ? PvpConfig.TARGET_COLOR : PvpConfig.COLORS[PvpConfig.colorIndex];
			fillBox(vc, mat, box, c[0], c[1], c[2], alpha);
		}

		pose.popPose();
		if (consumers instanceof MultiBufferSource.BufferSource bs) {
			bs.endBatch(RenderType.debugQuads());
		}
	}

	private static void v(VertexConsumer vc, Matrix4f m, double x, double y, double z, int r, int g, int b, int a) {
		vc.addVertex(m, (float) x, (float) y, (float) z).setColor(r, g, b, a);
	}

	private static void fillBox(VertexConsumer vc, Matrix4f m, AABB b, int r, int g, int bl, int a) {
		double x1 = b.minX, y1 = b.minY, z1 = b.minZ, x2 = b.maxX, y2 = b.maxY, z2 = b.maxZ;
		// dol
		v(vc, m, x1, y1, z1, r, g, bl, a); v(vc, m, x2, y1, z1, r, g, bl, a);
		v(vc, m, x2, y1, z2, r, g, bl, a); v(vc, m, x1, y1, z2, r, g, bl, a);
		// gora
		v(vc, m, x1, y2, z1, r, g, bl, a); v(vc, m, x1, y2, z2, r, g, bl, a);
		v(vc, m, x2, y2, z2, r, g, bl, a); v(vc, m, x2, y2, z1, r, g, bl, a);
		// polnoc (z1)
		v(vc, m, x1, y1, z1, r, g, bl, a); v(vc, m, x1, y2, z1, r, g, bl, a);
		v(vc, m, x2, y2, z1, r, g, bl, a); v(vc, m, x2, y1, z1, r, g, bl, a);
		// poludnie (z2)
		v(vc, m, x1, y1, z2, r, g, bl, a); v(vc, m, x2, y1, z2, r, g, bl, a);
		v(vc, m, x2, y2, z2, r, g, bl, a); v(vc, m, x1, y2, z2, r, g, bl, a);
		// zachod (x1)
		v(vc, m, x1, y1, z1, r, g, bl, a); v(vc, m, x1, y1, z2, r, g, bl, a);
		v(vc, m, x1, y2, z2, r, g, bl, a); v(vc, m, x1, y2, z1, r, g, bl, a);
		// wschod (x2)
		v(vc, m, x2, y1, z1, r, g, bl, a); v(vc, m, x2, y2, z1, r, g, bl, a);
		v(vc, m, x2, y2, z2, r, g, bl, a); v(vc, m, x2, y1, z2, r, g, bl, a);
	}
}
