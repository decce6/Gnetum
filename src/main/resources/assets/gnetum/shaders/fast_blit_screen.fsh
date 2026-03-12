#version 330

uniform sampler2D InSampler;

in vec2 texCoord;

out vec4 fragColor;

void main() {
    vec4 color = texture(InSampler, texCoord);
    if (color.a == 0.0) {
        /*
        * Discard transparent pixels. This appears to improve performance by a bit.
        * Since we're not using depth test at all, discarding fragments shouldn't negatively impact performance.
        */
        discard;
    }
    fragColor = color;
}
