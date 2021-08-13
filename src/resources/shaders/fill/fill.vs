#version 330
//Default simple shader with no geometry shader
uniform mat4 projection;
uniform vec4 unifColor;

uniform vec4 zFighting;

layout(location = 0) in vec4 Vertex;
layout(location = 1) in vec4 Color;

out VertexData{
    vec4 mColor;
} VertexOut;

void main()
{
    VertexOut.mColor = Color;
     gl_Position = projection*(Vertex+zFighting);
}
