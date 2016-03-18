#version 330

#include <view_block.glsl>

in vec4 vertexPosition;
in vec4 vertexColor;
in vec2 vertexTexCoord;

out vec4 vsColor;
out vec2 vsTexCoord;

void main() {
	vsColor = vertexColor;
	vsTexCoord = vertexTexCoord;
	gl_Position = view.viewProjMatrix * vertexPosition;
}
