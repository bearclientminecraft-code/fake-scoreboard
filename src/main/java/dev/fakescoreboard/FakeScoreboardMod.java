package dev.fakescoreboard;

import net.fabricmc.api.ClientModInitializer;

public class FakeScoreboardMod implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ScoreboardConfig.load();
		ScoreboardCommand.register();
		ScoreboardHud.register();
	}
}
