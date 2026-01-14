#version 300 es
precision mediump float;

uniform sampler2D u_Texture;

// Lighting uniforms
uniform vec3 u_LightDirVS;   // normalized light direction in view space
uniform float u_Ambient;     // ambient intensity [0..1]
uniform float u_Diffuse;     // diffuse intensity [0..1]

in vec2 v_TexCoord;
in vec3 v_NormalVS;

layout(location = 0) out vec4 o_FragColor;

void main() {
    vec2 texCoord = vec2(v_TexCoord.x, 1.0 - v_TexCoord.y);
    vec3 base = texture(u_Texture, texCoord).rgb;

    // Normalize interpolated normal
    vec3 N = normalize(v_NormalVS);
    vec3 L = normalize(u_LightDirVS);
    float ndotl = max(dot(N, L), 0.0);

    float light = clamp(u_Ambient + u_Diffuse * ndotl, 0.0, 1.5);
    vec3 color = base * light;
    o_FragColor = vec4(color, 1.0);
}
