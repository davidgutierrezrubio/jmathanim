#version 330

uniform vec4 unifColor;//Uniform color
uniform vec2 resolution;//Screen resolution

in VertexData{
     vec4 mColor;
} VertexIn;

void main(void)
{
    gl_FragColor = unifColor;
}
