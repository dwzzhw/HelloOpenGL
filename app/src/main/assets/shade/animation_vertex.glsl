attribute vec4 vPosition;
attribute vec2 vTexCoordinateAlpha;
attribute vec2 vTexCoordinateRgb;
attribute vec4 vLogoPosition;
attribute vec2 vLogoTexCoordinate;

uniform bool isDrawLogoFlag;
uniform mat4 u_Matrix;

varying vec2 v_TexCoordinateAlpha;
varying vec2 v_TexCoordinateRgb;
varying vec2 v_LogoTexCoordinate;

void main() {
    v_TexCoordinateAlpha = vec2(vTexCoordinateAlpha.x, 1.0 - vTexCoordinateAlpha.y);
    v_TexCoordinateRgb = vec2(vTexCoordinateRgb.x, 1.0 - vTexCoordinateRgb.y);
    v_LogoTexCoordinate = vec2(vLogoTexCoordinate.x, 1.0 - vLogoTexCoordinate.y);

    if(isDrawLogoFlag){
        gl_Position = vLogoPosition;
    } else {
        gl_Position = vPosition;
    }
}