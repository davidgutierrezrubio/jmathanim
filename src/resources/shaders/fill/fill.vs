#version 330
//Default simple shader with no geometry shader
uniform mat4 projection;
uniform mat4 view;
uniform vec4 unifColor;

layoutType(location = 0) in vec4 Vertex;
layoutType(location = 1) in vec4 NormalVec;

out VertexData{
    vec3 normal;
} VertexOut;
out vec4 FragPos;

void main()
{
    VertexOut.normal = (NormalVec).xyz;
    gl_Position = projection*view*Vertex;
    FragPos=Vertex;
}
