package pl.pvphelper;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.lwjgl.glfw.GLFW;

public class PvpHelperClient implements ClientModInitializer {

	private static KeyMapping openGuiKey;

	@Override
	public void onInitializeClient() {
		PvpConfig.load();

		// klawisz "\" otwiera menu
		openGuiKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
				"key.pvphelper.open_gui", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_BACKSLASH, "category.pvphelper"));

		HitboxRenderer.register();
		ClientTickEvents.END_CLIENT_TICK.register(PvpHelperClient::onTick);
	}

	private static void onTick(Minecraft mc) {
		if (mc.player == null || mc.level == null) return;

		while (openGuiKey.consumeClick()) {
			if (mc.screen == null) mc.setScreen(new PvpHelperScreen());
		}

		if (PvpConfig.autoHit) tryAutoHit(mc);
	}

	/** Jedno uderzenie, gdy celownik jest na graczu i miecz jest w pelni naladowany. */
	private static void tryAutoHit(Minecraft mc) {
		Player me = mc.player;
		if (mc.screen != null || mc.gameMode == null) return;
		if (!(mc.hitResult instanceof EntityHitResult hit) || hit.getType() != HitResult.Type.ENTITY) return;
		if (!(hit.getEntity() instanceof Player target)) return;
		if (target == me || !target.isAlive() || target.isSpectator()) return;
		if (!me.getMainHandItem().is(ItemTags.SWORDS)) return;
		if (me.getAttackStrengthScale(0.0f) < 1.0f) return; // miecz sie jeszcze laduje

		mc.gameMode.attack(me, target);
		me.swing(InteractionHand.MAIN_HAND);
		// po ataku cooldown spada do 0 - kolejny hit dopiero po naladowaniu
	}
}
