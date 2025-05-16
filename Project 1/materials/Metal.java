package materials;

import core.Ray;
import math.Vec3;
import scene.HitRecord;

public class Metal implements Material {
    private final Vec3 albedo;
    private final double fuzz;

    public Metal(Vec3 albedo, double fuzz) {
        this.albedo = albedo;
        this.fuzz = Math.min(fuzz, 1.0);
    }

    @Override
    public boolean scatter(Ray rIn, HitRecord rec, ScatterRecord srec) {
        Vec3 reflected = Vec3.reflect(rIn.direction().unit(), rec.normal);
        reflected = reflected.add(Vec3.randomUnitVector().multiply(fuzz));
        srec.scattered = new Ray(rec.p, reflected);
        srec.attenuation = albedo;
        return Vec3.dot(srec.scattered.direction(), rec.normal) > 0;
    }
}
