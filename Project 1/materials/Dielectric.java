package materials;

import core.Ray;
import math.Vec3;
import scene.HitRecord;

public class Dielectric implements Material {
    private final double refractionIndex;

    public Dielectric(double refractionIndex) {
        this.refractionIndex = refractionIndex;
    }

    @Override
    public boolean scatter(Ray rIn, HitRecord rec, ScatterRecord srec) {
        srec.attenuation = new Vec3(1.0, 1.0, 1.0);
        double ri = rec.frontFace ? (1.0 / refractionIndex) : refractionIndex;

        Vec3 unitDirection = rIn.direction().unit();
        double cosTheta = Math.min(Vec3.dot(unitDirection.negate(), rec.normal), 1.0);
        double sinTheta = Math.sqrt(1.0 - cosTheta * cosTheta);

        boolean cannotRefract = ri * sinTheta > 1.0;
        Vec3 direction;

        if (cannotRefract || reflectance(cosTheta, ri) > Math.random()) {
            direction = Vec3.reflect(unitDirection, rec.normal);
        } else {
            direction = Vec3.refract(unitDirection, rec.normal, ri);
        }

        srec.scattered = new Ray(rec.p, direction);
        return true;
    }

    private static double reflectance(double cosine, double refIdx) {
        double r0 = (1 - refIdx) / (1 + refIdx);
        r0 = r0 * r0;
        return r0 + (1 - r0) * Math.pow(1 - cosine, 5);
    }
}
