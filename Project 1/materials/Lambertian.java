package materials;

import core.Ray;
import math.Vec3;
import scene.HitRecord;

public class Lambertian implements Material {
    private final Vec3 albedo;

    public Lambertian(Vec3 albedo) {
        this.albedo = albedo;
    }

    @Override
    public boolean scatter(Ray rIn, HitRecord rec, ScatterRecord srec) {
        Vec3 scatterDirection = rec.normal.add(Vec3.randomUnitVector());

        if (scatterDirection.nearZero()) {
            scatterDirection = rec.normal;
        }

        srec.scattered = new Ray(rec.p, scatterDirection);
        srec.attenuation = albedo;
        return true;
    }
}
