#define MATERIAL_LAMBERTIAN 0
#define MATERIAL_METAL 1
#define MATERIAL_DIELECTRIC 2
#define MAX_RECURSION (3 + min(iFrame, 3))

float PI = 3.14159265359;

// --- RNG by Nimitz ---
uint hash(uvec2 p) {
    p = 1103515245U*((p>>1U)^(p.yx));
    uint h = 1103515245U*(p.x^(p.y>>3U));
    return h^(h>>16);
}

float hash1(inout float seed) {
    uint n = hash(floatBitsToUint(vec2(seed+=.1, seed+=.1)));
    return float(n) / float(0xffffffffU);
}

vec2 hash2(inout float seed) {
    uint n = hash(floatBitsToUint(vec2(seed+=.1, seed+=.1)));
    uvec2 rz = uvec2(n, n*48271U);
    return vec2(rz & uvec2(0x7fffffffU)) / float(0x7fffffff);
}

vec3 hash3(inout float seed) {
    uint n = hash(floatBitsToUint(vec2(seed+=.1, seed+=.1)));
    uvec3 rz = uvec3(n, n*16807U, n*48271U);
    return vec3(rz & uvec3(0x7fffffffU)) / float(0x7fffffff);
}

// --- Ray structures ---
struct Ray {
    vec3 origin;
    vec3 direction;
};

struct Hit {
    float t;
    vec3 point;
    vec3 normal;
    vec3 albedo;
    int materialType;
    float fuzz;
    float refIdx;
};

// --- Utility ---
float schlick(float cosine, float ior) {
    float r0 = (1.0 - ior) / (1.0 + ior);
    r0 *= r0;
    return r0 + (1.0 - r0) * pow(1.0 - cosine, 5.0);
}

vec3 randomUnitVector(inout float seed) {
    return normalize(hash3(seed) * 2.0 - 1.0);
}

vec2 randomInUnitDisk(inout float seed) {
    vec2 h = hash2(seed) * vec2(1.0, 2.0 * PI);
    return sqrt(h.x) * vec2(cos(h.y), sin(h.y));
}

// --- Hit logic ---
vec3 rayAt(Ray ray, float t) {
    return ray.origin + t * ray.direction;
}

bool hitSphere(vec3 center, float radius, Ray ray, out Hit hit, vec3 albedo, int materialType, float fuzz, float refIdx) {
    vec3 oc = ray.origin - center;
    float a = dot(ray.direction, ray.direction);
    float b = dot(oc, ray.direction);
    float c = dot(oc, oc) - radius * radius;
    float discriminant = b * b - a * c;

    if (discriminant < 0.0) return false;

    float sqrtD = sqrt(discriminant);
    float root = (-b - sqrtD) / a;
    if (root < 0.001) root = (-b + sqrtD) / a;
    if (root < 0.001) return false;

    hit.t = root;
    hit.point = rayAt(ray, root);
    hit.normal = normalize(hit.point - center);
    hit.albedo = albedo;
    hit.materialType = materialType;
    hit.fuzz = fuzz;
    hit.refIdx = refIdx;
    return true;
}

vec3 rayColor(Ray ray, inout float seed) {
    vec3 color = vec3(1.0);

    for (int bounce = 0; bounce < MAX_RECURSION; ++bounce) {
        float tClosest = 1e20;
        Hit closestHit;
        bool hitAnything = false;

        // Ground
        {
            Hit temp;
            if (hitSphere(vec3(0.0, -1000.0, 0.0), 1000.0, ray, temp, vec3(0.5), MATERIAL_LAMBERTIAN, 0.0, 0.0)) {
                if (temp.t < tClosest) {
                    tClosest = temp.t;
                    closestHit = temp;
                    hitAnything = true;
                }
            }
        }

        // Grid spheres
        for (int a = -11; a <= 11; ++a) {
            for (int b = -11; b <= 11; ++b) {
                vec2 pos = vec2(float(a), float(b));
                float localSeed = float(a * 31 + b * 57);
                vec3 offset = hash3(localSeed);
                vec3 center = vec3(float(a) + 0.9 * offset.x, 0.2, float(b) + 0.9 * offset.y);
                if (length(center - vec3(4.0, 0.2, 0.0)) < 0.9) continue;

                float chooseMat = offset.z;
                vec3 albedo;
                int type;
                float fuzz = 0.0;
                float refIdx = 1.5;

                if (chooseMat < 0.8) {
                    float seed1 = localSeed + 10.0;
                    albedo = hash3(seed1);
                    type = MATERIAL_LAMBERTIAN;
                } else if (chooseMat < 0.95) {
                    float seed2 = localSeed + 30.0;
                    fuzz = 0.5 * hash1(seed2);
                    type = MATERIAL_METAL;
                } else {
                    albedo = vec3(1.0);
                    type = MATERIAL_DIELECTRIC;
                }

                Hit temp;
                if (hitSphere(center, 0.2, ray, temp, albedo, type, fuzz, refIdx)) {
                    if (temp.t < tClosest) {
                        tClosest = temp.t;
                        closestHit = temp;
                        hitAnything = true;
                    }
                }
            }
        }

        // Main spheres
        vec3 centers[3] = vec3[3](vec3(0,1,0), vec3(-4,1,0), vec3(4,1,0));
        vec3 albedos[3] = vec3[3](vec3(1.0), vec3(0.4,0.2,0.1), vec3(0.7,0.6,0.5));
        int types[3] = int[3](MATERIAL_DIELECTRIC, MATERIAL_LAMBERTIAN, MATERIAL_METAL);
        float fuzzes[3] = float[3](0.0, 0.0, 0.0);
        float refs[3] = float[3](1.5, 0.0, 0.0);

        for (int i = 0; i < 3; ++i) {
            Hit temp;
            if (hitSphere(centers[i], 1.0, ray, temp, albedos[i], types[i], fuzzes[i], refs[i])) {
                if (temp.t < tClosest) {
                    tClosest = temp.t;
                    closestHit = temp;
                    hitAnything = true;
                }
            }
        }

        if (hitAnything) {
            if (closestHit.materialType == MATERIAL_LAMBERTIAN) {
                vec3 scatterDir = closestHit.normal + randomUnitVector(seed);
                ray.origin = closestHit.point + 0.001 * scatterDir;
                ray.direction = normalize(scatterDir);
                color *= closestHit.albedo;
            }
            else if (closestHit.materialType == MATERIAL_METAL) {
                vec3 reflected = reflect(normalize(ray.direction), closestHit.normal);
                reflected += closestHit.fuzz * randomUnitVector(seed);
                if (dot(reflected, closestHit.normal) < 0.0) break;
                ray.origin = closestHit.point + 0.001 * reflected;
                ray.direction = normalize(reflected);
                color *= closestHit.albedo;
            }
            else if (closestHit.materialType == MATERIAL_DIELECTRIC) {
                vec3 unitDir = normalize(ray.direction);
                float eta = closestHit.refIdx;
                vec3 normal = closestHit.normal;
                bool frontFace = dot(ray.direction, normal) < 0.0;

                if (!frontFace) {
                    normal = -normal;
                    eta = 1.0 / eta;
                }

                float cosTheta = min(dot(-unitDir, normal), 1.0);
                float sinTheta = sqrt(1.0 - cosTheta * cosTheta);
                bool cannotRefract = eta * sinTheta > 1.0;

                vec3 reflectDir = reflect(unitDir, normal);
                vec3 refractDir = refract(unitDir, normal, eta);

                float reflectProb = schlick(cosTheta, eta);
                vec3 nextDir = (cannotRefract || hash1(seed) < reflectProb)
                    ? reflectDir : refractDir;

                ray.origin = closestHit.point + 0.001 * nextDir;
                ray.direction = normalize(nextDir);
            }

        } else {
            vec3 unitDir = normalize(ray.direction);
            float t = 0.5 * (unitDir.y + 1.0);
            vec3 sky = mix(vec3(1.0), vec3(0.5, 0.7, 1.0), t);
            color *= sky;
            break;
        }
    }

    return color;
}

// DOF camera
Ray getRay(vec2 uv, vec3 origin, vec3 lookAt, float focusDist, float aperture, float fov, vec2 resolution, inout float seed) {
    vec3 w = normalize(origin - lookAt);
    vec3 u = normalize(cross(vec3(0,1,0), w));
    vec3 v = cross(w, u);

    float lensRadius = aperture / 2.0;
    float theta = radians(fov);
    float viewportHeight = 2.0 * tan(theta / 2.0);
    float viewportWidth = viewportHeight * (resolution.x / resolution.y);

    vec3 horizontal = focusDist * viewportWidth * u;
    vec3 vertical = focusDist * viewportHeight * v;
    vec3 lowerLeft = origin - horizontal / 2.0 - vertical / 2.0 - focusDist * w;

    vec2 rd = lensRadius * randomInUnitDisk(seed);
    vec3 offset = u * rd.x + v * rd.y;

    vec3 dir = normalize(lowerLeft + uv.x * horizontal + uv.y * vertical - origin - offset);

    return Ray(origin + offset, dir);
}

void mainImage(out vec4 fragColor, in vec2 fragCoord) {
    vec2 resolution = iResolution.xy;
    vec2 uv = (fragCoord + vec2(0.5)) / resolution;

    float seed = float(hash(floatBitsToUint(fragCoord))) / float(0xffffffffU) + iTime;
    vec3 camPos = vec3(13.0, 2.0, 3.0);
    vec3 lookAt = vec3(0.0);
    float focusDist = 10.0;
    float aperture = 0.1;
    float fov = 20.0;

    Ray ray = getRay(uv, camPos, lookAt, focusDist, aperture, fov, resolution, seed);
    vec3 col = rayColor(ray, seed);

    vec4 prev = texelFetch(iChannel0, ivec2(fragCoord), 0);
    float sampleCount = (iFrame > 0 && texelFetch(iChannel0, ivec2(0), 0).xy == iResolution.xy) ? prev.a + 1.0 : 1.0;
    vec3 accum = (iFrame > 0) ? mix(prev.rgb, col, 1.0 / sampleCount) : col;

    fragColor = vec4(accum, sampleCount);
}
