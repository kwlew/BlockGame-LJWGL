package dev.kwlew.game.engine.client;

import dev.kwlew.game.engine.client.player.PlayerController;
import dev.kwlew.game.engine.client.render.BlockPlatformRenderer;
import dev.kwlew.game.engine.client.world.PlatformScene;
import dev.kwlew.game.window.Window;

public final class InGameClient {

    private final PlatformScene scene = new PlatformScene();
    private final PlayerController playerController = new PlayerController();
    private final BlockPlatformRenderer renderer = new BlockPlatformRenderer();

    public void init() {
        renderer.init();
    }

    public void setActive(Window window, boolean active) {
        playerController.setCapture(window, active);
    }

    public void tick(Window window, float tickDeltaSeconds) {
        playerController.update(window, scene.world(), tickDeltaSeconds);
    }

    public void render(Window window, float alpha) {
        renderer.render(window, playerController.interpolatedSnapshot(alpha), scene);
    }

    public void cleanup() {
        renderer.cleanup();
    }
}
