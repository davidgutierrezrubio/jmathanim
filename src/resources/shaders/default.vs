#version 330
uniform mat4 projection;
uniform mat4 modelMat;
out vec4 Color;
flat out vec4 controlPoints;
layout (location=0) in vec3 VertexPosition;
layout (location=1) in vec4 VertexColor;
layout (location=2) in vec3 at_ControlPoints;

void main()
{
    gl_Position = projection*modelMat*vec4(VertexPosition,1.0);
    Color=VertexColor;
    controlPoints=projection*modelMat*vec4(at_ControlPoints,1.0);
}