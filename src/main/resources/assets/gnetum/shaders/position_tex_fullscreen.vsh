#version 330

#moj_import <minecraft:dynamictransforms.glsl>
#moj_import <minecraft:projection.glsl>

// Keep the position and uv attributes even though we don't use them.
// This is needed due to the Blaze3D limitation of "Cannot build mesh with no position element"
in vec3 Position;
in vec2 UV0;

out vec2 texCoord0;

// Output positions for fullscreen triangle, ignoring input vertices. By doing so we ensure the framebuffer texture does not get scaled. We also get slightly improved performance with this approach thanks to fewer fragment shader invocations.
// Fixes https://github.com/decce6/Gnetum/issues/96
// See https://stackoverflow.com/a/59739538 for drawing a fullscreen quad with a triangle
void main() {
    const vec2 vertices[3] = vec2[3] (
        vec2(-1,-1),
        vec2(3,-1),
        vec2(-1, 3)
    );
    gl_Position = vec4(vertices[gl_VertexID % 3], 0.0, 1.0);
    texCoord0 = 0.5 * gl_Position.xy + vec2(0.5);
}
