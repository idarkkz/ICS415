package scene;

import core.Ray;
import math.Interval;
import materials.Material;
import math.Vec3;

public interface Hittable {
    boolean hit(Ray r, Interval rayT, HitRecord rec);
}
