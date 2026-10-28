package dev.fakescoreboard;

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.Identifier;

public final class ScoreboardHud {
	// Иконки 8x8 (# = закрашенный пиксель)
	private static final String[] GEM = {
			"...##...",
			"..####..",
			".######.",
			"########",
			".######.",
			"..####..",
			"...##...",
			"........" };
	private static final String[] SWORD = {
			".....###",
			"....###.",
			"...###..",
			"#.###...",
			".###....",
			"..#.....",
			".##.....",
			"#......." };
	private static final String[] SKULL = {
			".######.",
			"########",
			"##.##.##",
			"##.##.##",
			"########",
			".######.",
			"..#.#.#.",
			"..#####." };
	private static final String[] CLOCK = {
			"..####..",
			".#....#.",
			"#..#...#",
			"#..#...#",
			"#..###.#",
			".#....#.",
			"..####..",
			"........" };

	private ScoreboardHud() {
	}

	public static void register() {
		HudElementRegistry.attachElementAfter(
				VanillaHudElements.MISC_OVERLAYS,
				Identifier.fromNamespaceAndPath("fakescoreboard", "stats"),
				ScoreboardHud::render);
	}

	private static String fmt(String s) {
		return s.replace('&', '\u00a7');
	}

	private static void icon(GuiGraphics g, String[] bitmap, int x, int y, int color) {
		for (int r = 0; r < bitmap.length; r++) {
			for (int col = 0; col < bitmap[r].length(); col++) {
				if (bitmap[r].charAt(col) == '#') {
					g.fill(x + col, y + r, x + col + 1, y + r + 1, color);
				}
			}
		}
	}

	private static void render(GuiGraphics g, DeltaTracker delta) {
		ScoreboardConfig c = ScoreboardConfig.INSTANCE;
		Minecraft mc = Minecraft.getInstance();
		if (!c.enabled || mc.player == null || mc.options.hideGui) {
			return;
		}

		Font font = mc.font;
		String name = c.name == null || c.name.isBlank() ? mc.player.getName().getString() : fmt(c.name);
		String[] values = { fmt(c.money), fmt(c.shards), fmt(c.kills), fmt(c.deaths), fmt(c.time) };

		int pad = 3;
		int lineH = 11;
		int iconW = 8;
		int gap = 3;

		int maxRow = 0;
		for (String v : values) {
			maxRow = Math.max(maxRow, font.width(v));
		}
		int w = pad * 2 + Math.max(font.width(name), iconW + gap + maxRow);
		int h = pad * 2 + lineH * 6 - 2;
		int x = c.x;
		int y = c.y;

		// фон
		g.fill(x, y, x + w, y + h, 0x78000000);

		// имя
		g.drawString(font, name, x + pad, y + pad, 0xFFFFFFFF, true);

		// строки со значками
		int ix = x + pad;
		int tx = ix + iconW + gap;
		for (int i = 0; i < 5; i++) {
			int ry = y + pad + lineH * (i + 1);
			switch (i) {
				case 0 -> g.drawString(font, "$", ix + 1, ry, 0xFF55FF55, true);
				case 1 -> icon(g, GEM, ix, ry, 0xFFAA00FF);
				case 2 -> icon(g, SWORD, ix, ry, 0xFFFF3333);
				case 3 -> icon(g, SKULL, ix, ry, 0xFFFF8800);
				default -> icon(g, CLOCK, ix, ry, 0xFFFFCC00);
			}
			g.drawString(font, values[i], tx, ry, 0xFFFFFFFF, true);
		}
	}
}
