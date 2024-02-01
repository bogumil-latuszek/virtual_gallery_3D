precision mediump float;

uniform vec4 u_Color;
varying vec4 v_Color;
uniform bool u_useGlobalColor;
void main()
{
    gl_FragColor = v_Color;
    if (u_useGlobalColor) {
        gl_FragColor = u_Color;
    }
}