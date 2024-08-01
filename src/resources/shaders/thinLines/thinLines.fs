#version 330

uniform vec4 unifColor;//Uniform color
uniform vec2 resolution;//Screen resolution

in VertexData{
     vec4 mColor;
} VertexIn;

void main(void)
{
    gl_FragColor = unifColor;
    //gl_FragColor = vec4(0.,0.,0.,.5);//unifColor;
}
