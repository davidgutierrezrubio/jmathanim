#version 330

uniform vec4 unifColor;//Uniform color
uniform vec2 resolution;//Screen resolution
in vec4 pointA;
in vec4 pointB;
out vec4 FragColor;

in VertexData{
     vec4 mColor;
} VertexIn;

void main(void)
{
float viewportWidth=resolution.x;
float viewportHeight=resolution.y;

    // Convertir a coordenadas de pantalla
    // vec2 screenA = (pointA.xy / pointA.w) * 0.5 + 0.5;
    // vec2 screenB = (pointB.xy / pointB.w) * 0.5 + 0.5;

	// screenA=screenA*resolution;
	// screenB=screenB*resolution;
	
    // Posición del fragmento en coordenadas de pantalla
    // vec2 fragPos = gl_FragCoord.xy;


    // Calcular interpolación espacial (t) a lo largo de la línea
    // float t = dot(fragPos - screenA, screenB - screenA) / dot(screenB - screenA, screenB - screenA);
    // t = clamp(t, 0.0, 1.0);
    
   // vec4 interp=mix(pointA,pointB,t);
//no needed at all!!!!! Keep it just in case...
	// gl_FragDepth=clamp((interp.z/interp.w + 1.0) * 0.5, 0.0, 1.0);
	
    FragColor = unifColor;//mix(vec4(1.,0.,0.,1.),vec4(0.,0.,1.,1.),t);
}
