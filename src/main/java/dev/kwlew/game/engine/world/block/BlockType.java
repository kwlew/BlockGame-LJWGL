package dev.kwlew.game.engine.world.block;

public record BlockType(short id, boolean solid, boolean opaque, float hardness) {
}