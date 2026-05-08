#version 330 core

layout (location = 0) in vec2 aPos;
layout (location = 1) in vec2 aUv;

uniform vec4 uRect; // x, y, width, height in normalized screen space (top-left origin)

out vec2 vUv;

void main() {
    vec2 screenPos = vec2(
        uRect.x + aPos.x * uRect.z,
        uRect.y + aPos.y * uRect.w
    );

    float ndcX = screenPos.x * 2.0 - 1.0;
    float ndcY = 1.0 - screenPos.y * 2.0;

    gl_Position = vec4(ndcX, ndcY, 0.0, 1.0);
    vUv = aUv;
}
