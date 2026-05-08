#version 330 core

layout (location = 0) in vec3 aPos;
layout (location = 1) in vec2 aUv;
layout (location = 2) in vec4 iModelCol0;
layout (location = 3) in vec4 iModelCol1;
layout (location = 4) in vec4 iModelCol2;
layout (location = 5) in vec4 iModelCol3;
layout (location = 6) in vec3 iTintColor;

uniform mat4 projection;
uniform mat4 view;

out vec2 vUv;
out vec3 vTintColor;

void main() {
    mat4 model = mat4(iModelCol0, iModelCol1, iModelCol2, iModelCol3);
    gl_Position = projection * view * model * vec4(aPos, 1.0);
    vUv = aUv;
    vTintColor = iTintColor;
}
