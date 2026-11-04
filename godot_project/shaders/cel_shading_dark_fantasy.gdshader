shader_type spatial;
render_mode diffuse_toon, specular_toon, cull_back;

uniform vec4 base_color : source_color = vec4(0.18, 0.22, 0.32, 1.0);
uniform vec4 rune_glow_color : source_color = vec4(0.15, 0.65, 1.0, 1.0);
uniform float rune_emission_strength : hint_range(0.0, 5.0) = 2.0;
uniform float roughness : hint_range(0.0, 1.0) = 0.45;
uniform float metallic : hint_range(0.0, 1.0) = 0.2;
uniform float rim_threshold : hint_range(0.0, 1.0) = 0.55;

void fragment() {
	ALBEDO = base_color.rgb;
	METALLIC = metallic;
	ROUGHNESS = roughness;
	
	// Efeito de iluminação de borda (Rim Light) estilizado
	float rim = 1.0 - dot(NORMAL, VIEW);
	if (rim > rim_threshold) {
		EMISSION = rune_glow_color.rgb * (rim - rim_threshold) * rune_emission_strength;
	} else {
		EMISSION = vec3(0.0);
	}
}
