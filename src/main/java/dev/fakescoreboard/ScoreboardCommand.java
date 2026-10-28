package dev.fakescoreboard;

import java.util.function.Consumer;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.network.chat.Component;

public final class ScoreboardCommand {
	private ScoreboardCommand() {
	}

	public static void register() {
		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
			ScoreboardConfig unused = ScoreboardConfig.INSTANCE;

			dispatcher.register(ClientCommandManager.literal("sb")
					.then(setter("name", v -> ScoreboardConfig.INSTANCE.name = v.equals("-") ? "" : v))
					.then(setter("money", v -> ScoreboardConfig.INSTANCE.money = v))
					.then(setter("shards", v -> ScoreboardConfig.INSTANCE.shards = v))
					.then(setter("kills", v -> ScoreboardConfig.INSTANCE.kills = v))
					.then(setter("deaths", v -> ScoreboardConfig.INSTANCE.deaths = v))
					.then(setter("time", v -> ScoreboardConfig.INSTANCE.time = v))
					.then(ClientCommandManager.literal("toggle").executes(ctx -> {
						ScoreboardConfig c = ScoreboardConfig.INSTANCE;
						c.enabled = !c.enabled;
						ScoreboardConfig.save();
						reply(ctx.getSource(), c.enabled ? "&aScoreboard ON" : "&cScoreboard OFF");
						return 1;
					}))
					.then(ClientCommandManager.literal("bg").executes(ctx -> {
						ScoreboardConfig c = ScoreboardConfig.INSTANCE;
						c.background = !c.background;
						ScoreboardConfig.save();
						reply(ctx.getSource(), c.background ? "&aBackground ON" : "&cBackground OFF");
						return 1;
					}))
					.then(ClientCommandManager.literal("pos")
							.then(ClientCommandManager.argument("x", IntegerArgumentType.integer())
									.then(ClientCommandManager.argument("y", IntegerArgumentType.integer())
											.executes(ctx -> {
												ScoreboardConfig c = ScoreboardConfig.INSTANCE;
												c.offX = IntegerArgumentType.getInteger(ctx, "x");
												c.offY = IntegerArgumentType.getInteger(ctx, "y");
												ScoreboardConfig.save();
												reply(ctx.getSource(), "&aOffset: " + c.offX + " " + c.offY);
												return 1;
											}))))
					.then(ClientCommandManager.literal("reset").executes(ctx -> {
						ScoreboardConfig.INSTANCE = new ScoreboardConfig();
						ScoreboardConfig.save();
						reply(ctx.getSource(), "&aReset to defaults");
						return 1;
					}))
					.executes(ctx -> {
						reply(ctx.getSource(), "&e/sb name|money|shards|kills|deaths|time <text>");
						reply(ctx.getSource(), "&e/sb toggle | bg | pos <x> <y> | reset");
						return 1;
					}));
		});
	}

	private static LiteralArgumentBuilder<FabricClientCommandSource> setter(String key, Consumer<String> apply) {
		return ClientCommandManager.literal(key)
				.then(ClientCommandManager.argument("value", StringArgumentType.greedyString())
						.executes(ctx -> {
							String value = StringArgumentType.getString(ctx, "value");
							apply.accept(value);
							ScoreboardConfig.save();
							reply(ctx.getSource(), "&aOK: " + key + " = " + value);
							return 1;
						}));
	}

	private static void reply(FabricClientCommandSource source, String text) {
		source.sendFeedback(Component.literal(text.replace('&', '\u00a7')));
	}
}
