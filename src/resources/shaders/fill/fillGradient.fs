#version 330

//0=solid color
//1=linear gradient
//2=radial gradient
uniform int fillType;


uniform vec4 unifColor;
uniform vec4 gradientA;
uniform vec4 gradientB;
uniform vec4 colGradientA;
uniform vec4 colGradientB;


in VertexData{
      vec3 normal;
} VertexIn;
in vec4 FragPos;

void main(void)
{
if (fillType==1){
    float dist=length(FragPos-gradientA)/length(gradientB-gradientA);
    gl_FragColor = mix(colGradientA,colGradientB,vec4(dist,dist,dist,dist));
}
if (fillType==0) {
    gl_FragColor=unifColor;
}
}
