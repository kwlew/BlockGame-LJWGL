package dev.kwlew.game.engine.debug;

public class FpsCounter {

    private int frames;
    private int fps;
    private double accumulator;

    public void frame(double deltaSeconds) {
        frames++;
        accumulator += deltaSeconds;

        if (accumulator >= 1.0) {
            fps = (int) Math.round(frames / accumulator);
            frames = 0;
            accumulator = 0.0;
        }
    }

    public int getFps() {
        return fps;
    }
}