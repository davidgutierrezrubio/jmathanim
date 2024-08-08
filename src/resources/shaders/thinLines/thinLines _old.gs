#version 330

uniform float Thickness;
uniform uint capStyle;//0=rounded,
uniform vec2 Viewport;//Size of the screen
uniform float MiterLimit;

layout(lines_adjacency) in;
layout(triangle_strip, max_vertices = 85) out;

in VertexData{
    vec4 mColor;
} VertexIn[4];

out VertexData{
    vec4 mColor;
} VertexOut;

 
vec2 toScreenSpace(vec4 vertex)
{
    return vec2( vertex.xy / vertex.w )*Viewport;
}
float toZValue(vec4 vertex)
{
    return (vertex.z/vertex.w);
}

// void generateCap(vec2 point, float zValue, float th,int numPoints) {
    // for (int i=0;i<numPoints;i++) {
    // float ang=i*2*3.141592653/numPoints;
    // float ang2=(i+1)*2*3.141592653/numPoints;
    // gl_Position=vec4(point / Viewport, zValue, 1.0 );
	// EmitVertex();
    // gl_Position=vec4((point+vec2(cos(ang),sin(ang))*th) / Viewport, zValue, 1.0 );
	// EmitVertex();
	// gl_Position=vec4((point+vec2(cos(ang2),sin(ang2))*th) / Viewport, zValue, 1.0 );
    // EmitVertex();
	// EndPrimitive();
// }
// }
void generateCapOld(vec2 p,vec2 n0,float zValue,float th, int numPoints) {

float delta=3.141592653/numPoints;//Step angle
mat2 rot=mat2(cos(delta),-sin(delta),sin(delta),cos(delta));
vec2 n=n0;
for (int i=0;i<numPoints;i++) {
	gl_Position=vec4(p/Viewport,zValue,1.0);
	EmitVertex();
	gl_Position=vec4((p+n)/Viewport,zValue,1.0);
	EmitVertex();
	n=rot*n;
	gl_Position=vec4((p+n)/Viewport,zValue,1.0);
	EmitVertex();
	EndPrimitive();
	}
	}
	
//Generate a rounded cap at point p
//Between (normalized) vectors n0 and n1, with given zValue, thickness and number of points to use
void generateRoundedCap(vec2 p,vec2 n0,vec2 n1,float zValue,float th, int numPoints) {
float angle=acos(dot(n0,n1));

float delta=angle/numPoints;//Step angle
mat2 rot=mat2(cos(delta),-sin(delta),sin(delta),cos(delta));
vec2 n=n0;
for (int i=0;i<numPoints;i++) {
	gl_Position=vec4(p/Viewport,zValue,1.0);
	EmitVertex();
	gl_Position=vec4((p+n*th)/Viewport,zValue,1.0);
	EmitVertex();
	n=rot*n;
	gl_Position=vec4((p+n*th)/Viewport,zValue,1.0);
	EmitVertex();
	EndPrimitive();
	}
	}

void main(void)
	{
	// 4 points
	vec4 Points[4];
	Points[0] = gl_in[0].gl_Position;
	Points[1] = gl_in[1].gl_Position;
	Points[2] = gl_in[2].gl_Position;
	Points[3] = gl_in[3].gl_Position;

	// 4 attached colors
	vec4 colors[4];
	colors[0] = VertexIn[0].mColor;
	colors[1] = VertexIn[1].mColor;
	colors[2] = VertexIn[2].mColor;
	colors[3] = VertexIn[3].mColor;

	// screen coords
	vec2 points[4];
	points[0] = toScreenSpace(Points[0]);
	points[1] = toScreenSpace(Points[1]);
	points[2] = toScreenSpace(Points[2]);
	points[3] = toScreenSpace(Points[3]);

	// deepness values
	float zValues[4];
	zValues[0] = toZValue(Points[0]);
	zValues[1] = toZValue(Points[1]);
	zValues[2] = toZValue(Points[2]);
	zValues[3] = toZValue(Points[3]);

	float th=Thickness*.6666;//Manually chosen constant to (more or less) match JavaFX thickness
	vec2 v0=normalize(points[2]-points[1]);
	vec2 n0=vec2(v0.y,-v0.x);

	vec2 v2;
	vec2 n2;
	if (length(points[2]-points[3])==0) {
	n2=n0;
	}
	else {
	v2=normalize(points[3]-points[2]);
	n2=vec2(v2.y,-v2.x);//Normal unit vector from p2 to p3
	}
	float thSegment=th;
	gl_Position = vec4( (points[1]-n0*thSegment) / Viewport, zValues[1], 1.0 );
	EmitVertex();
	gl_Position = vec4( (points[1]+n0*thSegment) / Viewport, zValues[1], 1.0 );
	EmitVertex();

	gl_Position = vec4((points[2]-n0*thSegment) / Viewport, zValues[1], 1.0 );
	EmitVertex();

	gl_Position = vec4( (points[2]+n0*thSegment) / Viewport, zValues[1], 1.0 );
	EmitVertex();
	EndPrimitive(); 	

    //Rounded butt, if thickness>1
    if (th>1) {
	
		generateRoundedCap(points[1],n0,-n0,zValues[1],th,10);
		generateRoundedCap(points[2],-n0,n0,zValues[2],th,10);
    }
        
    //Straight joined
}

    
