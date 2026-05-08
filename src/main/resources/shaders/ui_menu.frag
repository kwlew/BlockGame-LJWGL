#version 330 core

in vec2 vUv;
out vec4 FragColor;

uniform sampler2D uTexture;
uniform vec4 uTint;

void main() {
    vec4 sampled = texture(uTexture, vUv);
    FragColor = sampled * uTint;
}
