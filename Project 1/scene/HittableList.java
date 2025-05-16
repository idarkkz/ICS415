package scene;

import core.Ray;
import math.Interval;

import java.util.ArrayList;
import java.util.List;

public class HittableList implements Hittable {
    private final List<Hittable> objects = new ArrayList<>();

    public void add(Hittable object) {
        objects.add(object);
    }

    public void clear() {
        objects.clear();
    }

    @Override
    public boolean hit(Ray r, Interval rayT, HitRecord rec) {
        HitRecord tempRec = new HitRecord();
        boolean hitAnything = false;
        double closestSoFar = rayT.max;

        for (Hittable object : objects) {
            if (object.hit(r, new Interval(rayT.min, closestSoFar), tempRec)) {
                hitAnything = true;
                closestSoFar = tempRec.t;
                rec.p = tempRec.p;
                rec.normal = tempRec.normal;
                rec.mat = tempRec.mat;
                rec.t = tempRec.t;
                rec.frontFace = tempRec.frontFace;
            }
        }

        return hitAnything;
    }
}
