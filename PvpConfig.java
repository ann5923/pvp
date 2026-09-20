package pl.pvphelper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Files;
import java.nio.file.Path;

public final class PvpConfig {
	private PvpConfig() {}

	public static boolean filledHitbox = true;
	public static boolean autoHit = true;
	public static int colorIndex = 0;
	public static int alphaPercent = 30; // 10..80

	public static final String[] COLOR_NAMES = {"Czerwony", "Zielony", "Niebieski", "Fioletowy", "Bialy"};
	public static final int[][] COLORS = {
			{255, 40, 40}, {40, 255, 80}, {60, 120, 255}, {200, 70, 255}, {255, 255, 255}
	};
	/** Kolor gdy celownik jest dokladnie na graczu (czyli trafisz). */
	public static final int[] TARGET_COLOR = {255, 220, 0};

	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	private static final class Data {
		boolean filledHitbox = true;
		boolean autoHit = true;
		int colorIndex = 0;
		int alphaPercent = 30;
	}

	private static Path file() {
		return FabricLoader.getInstance().getConfigDir().resolve("pvphelper.json");
	}

	public static void load() {
		try {
			Path f = file();
			if (!Files.exists(f)) return;
			Data d = GSON.fromJson(Files.readString(f), Data.class);
			if (d == null) return;
			filledHitbox = d.filledHitbox;
			autoHit = d.autoHit;
			colorIndex = Math.floorMod(d.colorIndex, COLORS.length);
			alphaPercent = Math.max(10, Math.min(80, d.alphaPercent));
		} catch (Exception ignored) {
		}
	}

	public static void save() {
		try {
			Data d = new Data();
			d.filledHitbox = filledHitbox;
			d.autoHit = autoHit;
			d.colorIndex = colorIndex;
			d.alphaPercent = alphaPercent;
			Files.writeString(file(), GSON.toJson(d));
		} catch (Exception ignored) {
		}
	}
}
