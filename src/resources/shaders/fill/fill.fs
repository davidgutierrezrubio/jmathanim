#version 330

uniform vec4 unifColor;

in VertexData{
      vec4 mColor;
} VertexIn;

void main(void)
{
    gl_FragColor = unifColor;
}
