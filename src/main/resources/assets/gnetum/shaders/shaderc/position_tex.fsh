#version 330
#extension GL_ARB_separate_shader_objects : require

#include <minecraft:dynamictransforms.glsl>

uniform sampler2D Sampler0;

layout(location = 0) in vec2 texCoord0;

layout(location = 0) out vec4 FragColor;

void main() {
    vec4 color = texture(Sampler0, texCoord0);
    FragColor = color * ColorModulator;
}