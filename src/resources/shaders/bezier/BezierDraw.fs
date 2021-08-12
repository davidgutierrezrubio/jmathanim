#version 330


in VertexData{
    vec2 mTexCoord;
    vec4 mColor;
} VertexIn;

void main(void)
{
    gl_FragColor = vec4(0.,0.,0.,1.);//VertexIn.mColor;
}