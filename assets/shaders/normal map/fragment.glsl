precision mediump float;

uniform vec2 u_tiling;
uniform vec2 u_offset;
uniform vec4 u_Color;
uniform sampler2D u_Texture;
uniform sampler2D u_NormalMap;

varying vec2 v_TexCoordinate;
 
void main()
{
	vec2 coords = u_tiling * v_TexCoordinate + u_offset;
 	gl_FragColor = u_Color * texture2D(u_NormalMap, coords);
}