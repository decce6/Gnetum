#version 330

#moj_import <minecraft:dynamictransforms.glsl>

uniform sampler2D Sampler0;

in vec2 texCoord0;

out vec4 FragColor;

void main() {
    vec4 color = texture(Sampler0, texCoord0);
    FragColor = color * ColorModulator;
}