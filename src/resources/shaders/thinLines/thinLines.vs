#version 410 core

uniform mat4 projection;
uniform mat4 view;

uniform vec4 unifColor;//Uniform color


layoutType(location = 0) in vec4 Vertex;
layoutType(location = 1) in vec4 Color;

out VertexData{
  vec4 mColor;
} VertexOut;

void main()
{
    VertexOut.mColor = unifColor;
     gl_Position = projection*view*Vertex;
}
