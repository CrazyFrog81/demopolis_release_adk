#version 330

uniform sampler2D colorMap;

in vec2 vsTexCoord;

out vec4 fragColor;

void main() {
	vec4 rgba = texture(colorMap, vsTexCoord);
	if (rgba.a < 0.5)
		discard;
	fragColor = rgba;
}
