#version 330
layout(lines_adjacency) in;
layout(triangle_strip, max_vertices = 85) out;

in VertexData{
    vec4 mColor;
} VertexIn[4];

out VertexData{
    vec2 mTexCoord;
    vec4 mColor;
} VertexOut;

vec4 cubic_bezier(vec4 A, vec4 B, vec4 C, vec4 D, float t)
{
vec4 E = mix(A, B, t);
vec4 F = mix(B, C, t);
vec4 G = mix(C, D, t);

vec4 H = mix(E, F, t);
vec4 I = mix(F, G, t);

vec4 P = mix(H, I, t);

return P;
}

vec4 tangent_bezier(vec4 A, vec4 B, vec4 C, vec4 D, float t)
{
vec4 E = mix(A, B, t);
vec4 F = mix(B, C, t);
vec4 G = mix(C, D, t);

vec4 H = mix(E, F, t);
vec4 I = mix(F, G, t);

vec4 ta = normalize(H-I);

return ta;
}






void main(void) {
   vec4 cp1 = gl_in[0].gl_Position;//cp1
   vec4 p1 = gl_in[1].gl_Position;//p1
   vec4 p2 = gl_in[2].gl_Position;//p2
   vec4 cp2 = gl_in[3].gl_Position;//cp2
	
	int nSegments=15;
	
	float delta;
	
	for (int i=0; i<=nSegments; ++i){
	delta=float(i)*1.0 / float(nSegments);
	vec4 newP=cubic_bezier(p1, cp1, cp2, p2, delta);
	
	//Proof
	vec4 A=newP+vec4(0., .2, 0., 1.);
	vec4 B=newP+vec4(0., -.2, 0., 1.);
	vec4 C=newP+vec4(.2, .2, 0., 1.);
	vec4 D=newP+vec4(.2, -.2 ,0., 1.);
	
	gl_Position = A;
    EmitVertex();
	gl_Position = B;
    EmitVertex();
	gl_Position = C;
    EmitVertex();
	gl_Position = D;
    EmitVertex();
	EndPrimitive();
	}
	
	
 }