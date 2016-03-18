#version 330

struct VertexData {
	vec4 color;					// vertex diffuse color
};

in VertexData vd;

out vec4 fragColor;

void main() {
	fragColor = vd.color;
}
