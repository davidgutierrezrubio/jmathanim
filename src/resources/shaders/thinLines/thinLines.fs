#version 410 core

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
     vec2 screenA = (pointA.xy / pointA.w) * 0.5 + 0.5;
     vec2 screenB = (pointB.xy / pointB.w) * 0.5 + 0.5;

	 screenA=screenA*resolution;
	 screenB=screenB*resolution;
	
    // Posición del fragmento en coordenadas de pantalla
    vec2 fragPos = gl_FragCoord.xy;
	float dista=distance(pointA,pointB);

    // Calcular interpolación espacial (t) a lo largo de la línea
    float t = dot(fragPos - screenA, screenB - screenA) / dot(screenB - screenA, screenB - screenA);
    t = clamp(t, 0.0, 1.0)*dista;
 float dashSize = .1f;
        float gapSize = .05f;
        float patternLength = dashSize + gapSize;
 
 if (mod(t, patternLength) < dashSize) {
        // Está en un dash
      FragColor = unifColor;
    } else {
	  FragColor = unifColor;
        //discard; 
    }

    
}
