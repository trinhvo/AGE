#version 330

void main(){
    float depth = gl_FragCoord.z;

	float dx = dFdx(depth);
	float dy = dFdy(depth);
	float moment2 = depth * depth + 0.25 * (dx * dx + dy * dy);

    gl_FragColor = vec4(depth, moment2, 0.0, 1.0);

	//gl_FragColor = vec4(1.0 - depth, 1.0 - moment2, 0.0, 0.0);

    //float depth = gl_FragCoord.z;

    //float dx = dFdx(depth);
    //float dy = dFdy(depth);

    //float m = depth * depth + 0.25 * (dx * dx + dy * dy);

}