precision mediump float;
// uniform bool u_useGlobalColor;
// uniform vec4 u_Color;
uniform sampler2D u_TextureUnit;
varying vec2 v_TextureCoordinates;
void main()
{
    // if (u_useGlobalColor) {
    //     gl_FragColor = u_Color;
    // }
    // else {
    //     gl_FragColor = texture2D(u_TextureUnit, v_TextureCoordinates);
    // }
    gl_FragColor = texture2D(u_TextureUnit, v_TextureCoordinates);
}