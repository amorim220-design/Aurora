shader_type spatial;
render_mode blend_add, depth_draw_opaque, cull_disabled, unshaded;

uniform vec4 arcane_core_color : source_color = vec4(0.2, 0.75, 1.0, 1.0);
uniform vec4 arcane_outer_color : source_color = vec4(0.05, 0.35, 0.95, 0.8);
uniform float pulse_speed = 3.5;

void fragment() {
	vec2 center = UV - vec2(0.5);
	float dist = length(center) * 2.0;
	if (dist > 1.0) {
		discard;
	}
	
	float glow = pow(1.0 - dist, 1.8);
	float pulse = 0.85 + 0.15 * sin(TIME * pulse_speed);
	vec3 color = mix(arcane_core_color.rgb, arcane_outer_color.rgb, dist);
	
	ALBEDO = color * pulse;
	ALPHA = glow * pulse * COLOR.a;
}
