#version 330

uniform sampler2D colorMap;

in vec4 vsColor;
in vec2 vsTexCoord;

out vec4 fragColor;

void main() {
	fragColor = vsColor + texture(colorMap, vsTexCoord);
}
