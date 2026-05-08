package dev.kwlew.game.engine.loop;

public record LoopSettings(int maxFps) {

    private static final int TARGET_TPS = 20;

    public LoopSettings {
        if (maxFps < 0) {
            throw new IllegalArgumentException("maxFps must be >= 0");
        }
    }

    public int targetTps() {
        return TARGET_TPS;
    }

    public double tickStepSeconds() {
        return 1.0d / TARGET_TPS;
    }

    public double frameStepSeconds() {
        return maxFps == 0 ? 0.0d : 1.0d / maxFps;
    }

    public boolean hasFrameCap() {
        return maxFps > 0;
    }
}
