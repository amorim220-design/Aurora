shader_type fog;

uniform vec4 fog_color : source_color = vec4(0.08, 0.12, 0.22, 1.0);
uniform float fog_density : hint_range(0.0, 0.2) = 0.035;
uniform float height_falloff : hint_range(0.0, 1.0) = 0.15;

void fog() {
	DENSITY = fog_density * clamp(exp(-WORLD_POSITION.y * height_falloff), 0.0, 1.0);
	ALBEDO = fog_color.rgb;
	EMISSION = vec3(0.02, 0.05, 0.1);
}
