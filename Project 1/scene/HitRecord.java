package scene;

import core.Ray;
import materials.Material;
import math.Vec3;

public class HitRecord {
    public Vec3 p;
    public Vec3 normal;
    public Material mat;
    public double t;
    public boolean frontFace;

    public void setFaceNormal(Ray r, Vec3 outwardNormal) {
        frontFace = Vec3.dot(r.direction(), outwardNormal) < 0;
        normal = frontFace ? outwardNormal : outwardNormal.negate();
    }
}
