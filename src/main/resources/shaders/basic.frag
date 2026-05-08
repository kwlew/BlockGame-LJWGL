#version 330 core

in vec2 vUv;
in vec3 vTintColor;
out vec4 FragColor;
uniform sampler2D diffuseTexture;

void main() {
    vec4 sampled = texture(diffuseTexture, vUv);
    FragColor = vec4(sampled.rgb * vTintColor, 1.0);
}
