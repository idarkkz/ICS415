package scene;

import core.Ray;
import math.Interval;
import math.Vec3;
import materials.Material;

public class Sphere implements Hittable {
    private final Vec3 center;
    private final double radius;
    private final Material mat;

    public Sphere(Vec3 center, double radius, Material mat) {
        this.center = center;
        this.radius = Math.max(radius, 0);
        this.mat = mat;
    }

    @Override
    public boolean hit(Ray r, Interval rayT, HitRecord rec) {
        Vec3 oc = center.subtract(r.origin());
        double a = r.direction().lengthSquared();
        double h = Vec3.dot(r.direction(), oc);
        double c = oc.lengthSquared() - radius * radius;

        double discriminant = h * h - a * c;
        if (discriminant < 0) return false;

        double sqrtd = Math.sqrt(discriminant);
        double root = (h - sqrtd) / a;
        if (!rayT.surrounds(root)) {
            root = (h + sqrtd) / a;
            if (!rayT.surrounds(root)) return false;
        }

        rec.t = root;
        rec.p = r.at(rec.t);
        Vec3 outwardNormal = rec.p.subtract(center).divide(radius);
        rec.setFaceNormal(r, outwardNormal);
        rec.mat = mat;

        return true;
    }
}
