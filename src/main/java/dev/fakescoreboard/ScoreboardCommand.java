package dev.fakescoreboard;

import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

/**
 * Перехватывает команду /sb на клиенте (на сервер она не уходит).
 */
public final class ScoreboardCommand {
	private ScoreboardCommand() {
	}

	public static void register() {
		ClientSendMessageEvents.ALLOW_COMMAND.register(ScoreboardCommand::handle);
	}

	private static void reply(String text) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.player != null) {
			mc.player.displayClientMessage(Component.literal(text.replace('&', '\u00a7')), false);
		}
	}

	private static boolean handle(String command) {
		String[] p = command.trim().split("\\s+", 3);
		if (!p[0].equalsIgnoreCase("sb")) {
			return true; // не наша команда, отправляем как обычно
		}

		ScoreboardConfig c = ScoreboardConfig.INSTANCE;
		String sub = p.length > 1 ? p[1].toLowerCase() : "help";
		String value = p.length > 2 ? p[2] : "";

		switch (sub) {
			case "name", "money", "shards", "kills", "deaths", "time" -> {
				if (value.isBlank()) {
					reply("&cUsage: /sb " + sub + " <text>");
					return false;
				}
				switch (sub) {
					case "name" -> c.name = value.equals("-") ? "" : value;
					case "money" -> c.money = value;
					case "shards" -> c.shards = value;
					case "kills" -> c.kills = value;
					case "deaths" -> c.deaths = value;
					case "time" -> c.time = value;
					default -> {
					}
				}
				reply("&aOK: " + sub + " = " + value);
			}
			case "toggle" -> {
				c.enabled = !c.enabled;
				reply(c.enabled ? "&aScoreboard ON" : "&cScoreboard OFF");
			}
			case "pos" -> {
				String[] xy = value.trim().split("\\s+");
				try {
					c.x = Integer.parseInt(xy[0]);
					c.y = Integer.parseInt(xy[1]);
					reply("&aPosition: " + c.x + " " + c.y);
				} catch (Exception e) {
					reply("&cUsage: /sb pos <x> <y>");
					return false;
				}
			}
			case "reset" -> {
				ScoreboardConfig.INSTANCE = new ScoreboardConfig();
				reply("&aReset to defaults");
			}
			default -> {
				reply("&e/sb name|money|shards|kills|deaths|time <text>");
				reply("&e/sb toggle | /sb pos <x> <y> | /sb reset");
				return false;
			}
		}

		ScoreboardConfig.save();
		return false; // отменяем отправку на сервер
	}
        }
