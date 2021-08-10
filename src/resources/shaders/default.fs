#version 330

//Adapted from https://github.com/vicrucann/shader-3dcurve
//Code from Victoria Rudakova

in VertexData{
    vec2 mTexCoord;
    vec4 mColor;
} VertexIn;

void main(void)
{
    gl_FragColor = VertexIn.mColor;
}
