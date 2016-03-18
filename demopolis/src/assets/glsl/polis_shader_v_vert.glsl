#version 330

#define MAX_LIGHTS 1

#include <view_block.glsl>

#include <light_block.glsl>

struct Material {
	vec3 ambientColor;
	vec3 diffuseColor;
	float alpha;
};

struct VertexData {
	vec4 color;					// vertex diffuse color
};

uniform Material material;

in vec4 vertexPosition;
in vec4 vertexNormal;

out VertexData vd;

void main() {
	vec3 scatteredLight = vec3(0);
	
	vec3 position = vec4(view.viewMatrix * vertexPosition).xyz;
	vec3 normal = normalize(view.normalMatrix * vertexNormal.xyz);

	for (int i = 0; i < MAX_LIGHTS; ++i) {
		float type = lights[i].trss.x;
		if (type == 0)
			continue;

		vec3 lightDirection = lights[i].position;

		float ndotl = dot(normal, lightDirection);
		float diffuseFactor = max(ndotl, 0);

		scatteredLight += material.ambientColor * lights[i].ambientColor + material.diffuseColor * lights[i].color * diffuseFactor;
	}

	vec4 rgba = vec4(min(scatteredLight, vec3(1.0)), material.alpha);
	vd.color = rgba;
	
	gl_Position = view.viewProjMatrix * vertexPosition;
}
