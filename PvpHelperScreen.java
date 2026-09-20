package pl.pvphelper;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

public class PvpHelperScreen extends Screen {

	public PvpHelperScreen() {
		super(Component.literal("PvP Helper"));
	}

	private static Component onOff(String name, boolean v) {
		return Component.literal(name + ": " + (v ? "ON" : "OFF"));
	}

	private static Component alphaText() {
		return Component.literal("Przezroczystosc wypelnienia: " + PvpConfig.alphaPercent + "%");
	}

	@Override
	protected void init() {
		int x = this.width / 2 - 110;
		int y = this.height / 2 - 60;

		addRenderableWidget(Button.builder(onOff("Wypelniony hitbox", PvpConfig.filledHitbox), b -> {
			PvpConfig.filledHitbox = !PvpConfig.filledHitbox;
			b.setMessage(onOff("Wypelniony hitbox", PvpConfig.filledHitbox));
		}).bounds(x, y, 220, 20).build());

		addRenderableWidget(Button.builder(
				Component.literal("Kolor: " + PvpConfig.COLOR_NAMES[PvpConfig.colorIndex]), b -> {
					PvpConfig.colorIndex = (PvpConfig.colorIndex + 1) % PvpConfig.COLORS.length;
					b.setMessage(Component.literal("Kolor: " + PvpConfig.COLOR_NAMES[PvpConfig.colorIndex]));
				}).bounds(x, y + 24, 220, 20).build());

		addRenderableWidget(new AbstractSliderButton(x, y + 48, 220, 20, alphaText(),
				(PvpConfig.alphaPercent - 10) / 70.0) {
			@Override
			protected void updateMessage() {
				setMessage(alphaText());
			}

			@Override
			protected void applyValue() {
				PvpConfig.alphaPercent = 10 + (int) Math.round(this.value * 70.0);
			}
		});

		addRenderableWidget(Button.builder(onOff("Auto-hit (1x na naladowanie)", PvpConfig.autoHit), b -> {
			PvpConfig.autoHit = !PvpConfig.autoHit;
			b.setMessage(onOff("Auto-hit (1x na naladowanie)", PvpConfig.autoHit));
		}).bounds(x, y + 72, 220, 20).build());

		addRenderableWidget(Button.builder(Component.literal("Gotowe"), b -> onClose())
				.bounds(x, y + 104, 220, 20).build());
	}

	@Override
	public boolean keyPressed(int key, int scancode, int modifiers) {
		if (key == GLFW.GLFW_KEY_BACKSLASH) { // ten sam klawisz zamyka menu
			onClose();
			return true;
		}
		return super.keyPressed(key, scancode, modifiers);
	}

	@Override
	public void onClose() {
		PvpConfig.save();
		super.onClose();
	}

	@Override
	public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
		super.render(g, mouseX, mouseY, partialTick);
		g.drawCenteredString(this.font, this.title, this.width / 2, this.height / 2 - 88, 0xFFFFFF);
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}
}
