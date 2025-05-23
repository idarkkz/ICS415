void mainImage(out vec4 fragColor, in vec2 fragCoord) {
    vec4 data = texelFetch(iChannel0, ivec2(fragCoord), 0);
    fragColor = vec4(sqrt(data.rgb / max(data.a, 1.0)), 1.0); // gamma corrected
}
