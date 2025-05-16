package materials;

import core.Ray;
import scene.HitRecord;
import math.Vec3;

public interface Material {
    boolean scatter(Ray rIn, HitRecord rec, ScatterRecord srec);
}
