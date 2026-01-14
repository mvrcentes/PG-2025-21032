#version 300 es

uniform mat4 u_ModelView;
uniform mat4 u_ModelViewProjection;

layout(location = 0) in vec4 a_Position;
layout(location = 1) in vec2 a_TexCoord;
layout(location = 2) in vec3 a_Normal;

out vec2 v_TexCoord;
out vec3 v_NormalVS; // normal in view space

void main() {
    v_TexCoord = a_TexCoord;
    // Approximate normal transform (assumes uniform scale). For non-uniform, use inverse-transpose.
    v_NormalVS = mat3(u_ModelView) * a_Normal;
    gl_Position = u_ModelViewProjection * a_Position;
}
