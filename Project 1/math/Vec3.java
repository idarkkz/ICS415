package math;

import java.util.Random;

public class Vec3 {
    private static final Random rand = new Random();
    private final double x, y, z;

    public Vec3() {
        this(0, 0, 0);
    }

    public Vec3(double x, double y, double z) {
        this.x = x; this.y = y; this.z = z;
    }

    public double x() { return x; }
    public double y() { return y; }
    public double z() { return z; }

    public Vec3 add(Vec3 v) {
        return new Vec3(x + v.x, y + v.y, z + v.z);
    }

    public Vec3 subtract(Vec3 v) {
        return new Vec3(x - v.x, y - v.y, z - v.z);
    }

    public Vec3 multiply(double t) {
        return new Vec3(t * x, t * y, t * z);
    }

    public Vec3 multiply(Vec3 v) {
        return new Vec3(x * v.x, y * v.y, z * v.z);
    }

    public Vec3 divide(double t) {
        return this.multiply(1 / t);
    }

    public double length() {
        return Math.sqrt(lengthSquared());
    }

    public double lengthSquared() {
        return x * x + y * y + z * z;
    }

    public boolean nearZero() {
        double s = 1e-8;
        return (Math.abs(x) < s) && (Math.abs(y) < s) && (Math.abs(z) < s);
    }

    public static Vec3 random() {
        return new Vec3(rand.nextDouble(), rand.nextDouble(), rand.nextDouble());
    }

    public static Vec3 random(double min, double max) {
        return new Vec3(
            min + (max - min) * rand.nextDouble(),
            min + (max - min) * rand.nextDouble(),
            min + (max - min) * rand.nextDouble()
        );
    }

    public static Vec3 randomInUnitDisk() {
        while (true) {
            Vec3 p = new Vec3(random(-1, 1).x, random(-1, 1).y, 0);
            if (p.lengthSquared() < 1) return p;
        }
    }

    public static Vec3 randomUnitVector() {
        while (true) {
            Vec3 p = random(-1, 1);
            double lenSq = p.lengthSquared();
            if (lenSq > 1e-160 && lenSq <= 1.0) return p.divide(Math.sqrt(lenSq));
        }
    }

    public static Vec3 reflect(Vec3 v, Vec3 n) {
        return v.subtract(n.multiply(2 * dot(v, n)));
    }

    public static Vec3 refract(Vec3 uv, Vec3 n, double etaiOverEtat) {
        double cosTheta = Math.min(dot(uv.negate(), n), 1.0);
        Vec3 rOutPerp = uv.add(n.multiply(cosTheta)).multiply(etaiOverEtat);
        Vec3 rOutParallel = n.multiply(-Math.sqrt(Math.abs(1.0 - rOutPerp.lengthSquared())));
        return rOutPerp.add(rOutParallel);
    }

    public Vec3 negate() {
        return new Vec3(-x, -y, -z);
    }

    public static double dot(Vec3 u, Vec3 v) {
        return u.x * v.x + u.y * v.y + u.z * v.z;
    }

    public static Vec3 cross(Vec3 u, Vec3 v) {
        return new Vec3(
            u.y * v.z - u.z * v.y,
            u.z * v.x - u.x * v.z,
            u.x * v.y - u.y * v.x
        );
    }

    public Vec3 unit() {
        return this.divide(this.length());
    }
}
