#version 460
layout (lines) in;
layout (triangles, max_vertices = 6) out;
flat in vec4 controlPoints[];
void main(){
 //   gl_Position=gl_in[0].gl_Position;
   // EmitVertex();
//gl_Position=gl_in[1].gl_Position;
  //  EmitVertex();
  // EndPrimitive();

gl_Position=controlPoints[1];
EmitVertex();
gl_Position=controlPoints[1];
EmitVertex();
gl_Position=controlPoints[1]+vec4(.2,.0,.0,1.);
EmitVertex();
EndPrimitive();

gl_Position=controlPoints[0];
EmitVertex();
gl_Position=controlPoints[0];
EmitVertex();
gl_Position=controlPoints[0]+vec4(.2,.0,.0,1.);
EmitVertex();
EndPrimitive();



}