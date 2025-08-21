#version 410 core

uniform float Thickness;
uniform int capStyle;//0=rounded, 1=butt, 2=square
uniform vec2 Viewport;//Size of the screen
uniform float MiterLimit;
uniform vec3 eye;
uniform vec3 lookAt;

layoutType(lines_adjacency) in;
layoutType(triangle_strip, max_vertices = 85) out;

in VertexData{
    vec4 mColor;
} VertexIn[4];

out VertexData{
    vec4 mColor;
} VertexOut;

out vec4 pointA;
out vec4 pointB;

 
vec2 toScreenSpace(vec4 vertex)
{
    return vec2( vertex.xy / vertex.w )*Viewport;
}
float toZValue(vec4 vertex)
{
    return (vertex.z/vertex.w);
}
	
//Generate a rounded cap at point p
//Between (normalized) vectors n0 and n1, with given zValue, thickness and number of points to use
void generateRoundedCap(vec2 p,vec2 n0,vec2 n1,float zValue,float th, int numPoints) {
float angle=acos(dot(n0,n1));

float delta=angle/numPoints;//Step angle
mat2 rot=mat2(cos(delta),sin(delta),-sin(delta),cos(delta));
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

	pointA=Points[1];
	pointB=Points[2];
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

	float th=Thickness*.3333;//Manually chosen constant to (more or less) match JavaFX thickness
	float dRef=distance(eye,lookAt);
	float d1=abs(Points[1].z);//distance(eye,Points[1].xyz);
	float d2=abs(Points[2].z);//distance(eye,Points[2].xyz);
	
	
	
	vec2 v1=points[2]-points[1];
	vec2 n1=normalize(vec2(-v1.y,v1.x));//Normal unit vector from p1 to p2 rotated 90º clockwise

	vec2 v0=points[1]-points[0];;
	vec2 n0;
	if (v0.x==0 && v0.y==0) {
		n0=-n1;
	}
	else {
			n0=normalize(vec2(-v0.y,v0.x));//Normal unit vector from p0 to p1 rotated 90º clockwise
	}


	vec2 v2=points[3]-points[2];;
	vec2 n2;
	if (v2.x==0 && v2.y==0) {
		n2=-n1;
	}
	else {
		n2=normalize(vec2(-v2.y,v2.x));//Normal unit vector from p2 to p3 rotated 90º clockwise
		}
	float th1=th*dRef/d1;
	float th2=th*dRef/d2;
	gl_Position = vec4( (points[1]-n1*th1) / Viewport, zValues[1], 1.0 );
	EmitVertex();
	gl_Position = vec4( (points[1]+n1*th1) / Viewport, zValues[1], 1.0 );
	EmitVertex();

	gl_Position = vec4((points[2]-n1*th2) / Viewport, zValues[2], 1.0 );
	EmitVertex();

	gl_Position = vec4( (points[2]+n1*th2) / Viewport, zValues[2], 1.0 );
	EmitVertex();
	EndPrimitive(); 	
	
	int numPoints;
	if (capStyle==0) 
	{
		numPoints=10;
	}
	if (capStyle==1) 
	{
		numPoints=1;
	}
    //Rounded butt, if thickness>1
	float cross01=sign(v0.x*v1.y-v0.y*v1.x); //>0 is a "turn left", <0 a "turn right", 0 "straight" OR coincident!
	float cross12=sign(v1.x*v2.y-v1.y*v2.x); //>0 is a "turn left", <0 a "turn right", 0 "straight" OR coincident!
    if (th>1) {
	
	if (cross01<0) {
		generateRoundedCap(points[1],n1,n0,zValues[1],th1,10);
		}
	if (cross01>0)
		{
		generateRoundedCap(points[1],-n0,-n1,zValues[1],th1,10);
		}
	if (cross01==0) {
	if ((v0.x == 0.0 && v0.y == 0.0))
		generateRoundedCap(points[1],-n0,n0,zValues[1],th1,numPoints);
	}
	
	if (cross12==0) {
		if ((v2.x == 0.0 && v2.y == 0.0))
			generateRoundedCap(points[2],-n1,n1,zValues[2],th2,numPoints);
		}
    }
        
    //Straight joined
}

    
