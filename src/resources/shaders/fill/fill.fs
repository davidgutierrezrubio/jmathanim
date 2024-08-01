#version 330

uniform vec4 unifColor;

in VertexData{
      vec3 normal;
} VertexIn;
in vec4 FragPos;

void main(void)
{
    vec3 ambient=vec3(1.,1.,1.);
    vec3 lightColor=vec3(1.,1.,1.);
    vec3 lightPos=vec3(0.,1.,3.);
    vec3 norm = normalize(VertexIn.normal);
    vec3 lightDir = normalize(lightPos - FragPos.xyz/FragPos.a);
    float diff = abs(dot(norm, lightDir));
    // float diff = max(dot(norm, lightDir), 0.0);
    vec3 diffuse =  diff*lightColor;

//Diffuse+ambient
vec3 result = (.0*ambient+1.*diffuse)*unifColor.rgb;
gl_FragColor = vec4(result, unifColor.a);
//gl_FragColor = vec4(FragPos.z,FragPos.z,FragPos.z,1.);


//Simple plain color
// gl_FragColor=unifColor;


//Vertical gradient to blue
//float m=smoothstep(0,1,FragPos.z/FragPos.a);
//vec3 result = mix(unifColor.xyz,vec3(0.,0.,1.),m);
//gl_FragColor = vec4(result, unifColor.a);
}
