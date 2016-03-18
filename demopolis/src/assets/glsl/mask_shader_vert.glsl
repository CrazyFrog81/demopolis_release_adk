#version 330

#include <view_block.glsl>

in vec4 vertexPosition;
in vec2 vertexTexCoord;

out vec2 vsTexCoord;

void main() {
	vsTexCoord = vertexTexCoord;
	gl_Position = view.viewProjMatrix * vertexPosition;
}
