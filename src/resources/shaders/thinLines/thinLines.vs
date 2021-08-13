#version 330
uniform mat4 projection;
uniform vec4 unifColor;

layout(location = 0) in vec4 Vertex;
layout(location = 1) in vec4 Color;

out VertexData{
    vec4 mColor;
} VertexOut;

void main()
{
    VertexOut.mColor = unifColor;
     gl_Position = projection*Vertex;
}
