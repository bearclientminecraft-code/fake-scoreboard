package dev.fakescoreboard;

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.Identifier;

public final class ScoreboardHud {
	// Иконки-символы (вместо сотен пикселей: так почти нет нагрузки)
	private static final String[] ICONS = { "$", "\u2726", "\u2694", "\u2620", "\u25D4" };
	private static final int[] ICON_COLORS = { 0xFF55FF55, 0xFFAA00FF, 0xFFFF3333, 0xFFFF8800, 0xFFFFCC00 };

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
		int lineH = 10;
		int iconW = 9;
		int margin = 2;

		int maxRow = 0;
		for (String v : values) {
			maxRow = Math.max(maxRow, font.width(v));
		}
		int w = pad * 2 + Math.max(font.width(name), iconW + maxRow);
		int h = pad * 2 + lineH * 6 - 1;

		int sw = mc.getWindow().getGuiScaledWidth();
		int sh = mc.getWindow().getGuiScaledHeight();
		int x = sw - w - margin + c.offX;
		int y = (sh - h) / 2 + c.offY;

		if (c.background) {
			g.fill(x, y, x + w, y + h, 0x78000000);
		}

		g.drawString(font, name, x + pad, y + pad, 0xFFFFFFFF, true);

		for (int i = 0; i < 5; i++) {
			int ry = y + pad + lineH * (i + 1);
			g.drawString(font, ICONS[i], x + pad, ry, ICON_COLORS[i], true);
			g.drawString(font, values[i], x + pad + iconW, ry, 0xFFFFFFFF, true);
		}
	}
}
