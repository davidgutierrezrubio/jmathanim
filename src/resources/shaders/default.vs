#version 330
uniform mat4 projection;
uniform mat4 modelMat;

//Adapted from https://github.com/vicrucann/shader-3dcurve
//Code from Victoria Rudakova
layout(location = 0) in vec4 Vertex;
layout(location = 1) in vec4 Color;

out VertexData{
    vec4 mColor;
} VertexOut;

void main()
{
    VertexOut.mColor = Color;
     gl_Position = projection*modelMat*Vertex;
}
