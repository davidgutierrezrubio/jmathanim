#version 330
uniform mat4 projection;
uniform mat4 modelMat;
layout (location=0) in vec3 VertexPosition;
layout (location=1) in vec3 VertexColor;

void main()
{
    gl_Position = projection*modelMat*vec4(VertexPosition,1.0);
}