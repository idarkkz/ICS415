package core;
 
import math.Vec3;

public class Ray {
    private final Vec3 origin;
    private final Vec3 direction;

    public Ray(Vec3 origin, Vec3 direction) {
        this.origin = origin;
        this.direction = direction;
    }

    public Vec3 origin() {
        return origin;
    }

    public Vec3 direction() {
        return direction;
    }

    public Vec3 at(double t) {
        return origin.add(direction.multiply(t));
    }
}
