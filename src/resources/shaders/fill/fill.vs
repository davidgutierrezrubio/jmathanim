#version 330
//Default simple shader with no geometry shader
uniform mat4 projection;
uniform mat4 modelMatrix;
uniform vec4 unifColor;

uniform vec4 zFighting;

layout(location = 0) in vec4 Vertex;
layout(location = 1) in vec4 NormalVec;

out VertexData{
    vec3 normal;
} VertexOut;
out vec4 FragPos;

void main()
{
    VertexOut.normal = (NormalVec).xyz;
    gl_Position = projection*(Vertex+zFighting);
    FragPos=Vertex;
}
