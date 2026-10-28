package dev.fakescoreboard;

import java.nio.file.Files;
import java.nio.file.Path;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.fabricmc.loader.api.FabricLoader;

public class ScoreboardConfig {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("fakescoreboard.json");

	public static ScoreboardConfig INSTANCE = new ScoreboardConfig();

	public boolean enabled = true;
	public boolean background = false;
	public String name = "";
	public String money = "72B";
	public String shards = "8.2K";
	public String kills = "894";
	public String deaths = "495";
	public String time = "84d 22h";
	/** Смещение от положения "справа по центру" */
	public int offX = 0;
	public int offY = 0;

	public static void load() {
		try {
			if (Files.exists(FILE)) {
				ScoreboardConfig loaded = GSON.fromJson(Files.readString(FILE), ScoreboardConfig.class);
				if (loaded != null) {
					INSTANCE = loaded;
				}
			}
		} catch (Exception ignored) {
		}
	}

	public static void save() {
		try {
			Files.writeString(FILE, GSON.toJson(INSTANCE));
		} catch (Exception ignored) {
		}
	}
}
