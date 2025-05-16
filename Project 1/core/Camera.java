package core;

import materials.Material;
import materials.ScatterRecord;
import math.Interval;
import math.Vec3;
import scene.HitRecord;
import scene.Hittable;

import java.io.PrintWriter;

public class Camera {
    public double aspectRatio = 1.0;
    public int imageWidth = 100;
    public int samplesPerPixel = 10;
    public int maxDepth = 10;

    public double vfov = 90;
    public Vec3 lookFrom = new Vec3(0, 0, 0);
    public Vec3 lookAt = new Vec3(0, 0, -1);
    public Vec3 vup = new Vec3(0, 1, 0);

    public double defocusAngle = 0;
    public double focusDist = 10;

    private int imageHeight;
    private Vec3 center;
    private Vec3 pixel00Loc;
    private Vec3 pixelDeltaU;
    private Vec3 pixelDeltaV;
    private Vec3 u, v, w;
    private Vec3 defocusDiskU, defocusDiskV;

    public void render(Hittable world, PrintWriter out) {
        initialize();

        out.printf("P3\n%d %d\n255\n", imageWidth, imageHeight);

        for (int j = 0; j < imageHeight; j++) {
            System.err.printf("\rScanlines remaining: %d ", imageHeight - j);
            for (int i = 0; i < imageWidth; i++) {
                Vec3 pixelColor = new Vec3(0, 0, 0);
                for (int s = 0; s < samplesPerPixel; s++) {
                    Ray r = getRay(i, j);
                    pixelColor = pixelColor.add(rayColor(r, maxDepth, world));
                }
                ColorUtils.writeColor(out, pixelColor, samplesPerPixel);
            }
        }

        System.err.println("\rDone.");
    }

    private void initialize() {
        imageHeight = (int)(imageWidth / aspectRatio);
        imageHeight = Math.max(imageHeight, 1);

        center = lookFrom;

        double theta = Math.toRadians(vfov);
        double h = Math.tan(theta / 2);
        double viewportHeight = 2 * h * focusDist;
        double viewportWidth = viewportHeight * ((double) imageWidth / imageHeight);

        w = lookFrom.subtract(lookAt).unit();
        u = Vec3.cross(vup, w).unit();
        v = Vec3.cross(w, u);

        Vec3 viewportU = u.multiply(viewportWidth);
        Vec3 viewportV = v.multiply(-viewportHeight);

        pixelDeltaU = viewportU.divide(imageWidth);
        pixelDeltaV = viewportV.divide(imageHeight);

        Vec3 viewportUpperLeft = center.subtract(w.multiply(focusDist))
            .subtract(viewportU.divide(2))
            .subtract(viewportV.divide(2));

        pixel00Loc = viewportUpperLeft
            .add(pixelDeltaU.multiply(0.5))
            .add(pixelDeltaV.multiply(0.5));

        double defocusRadius = focusDist * Math.tan(Math.toRadians(defocusAngle / 2));
        defocusDiskU = u.multiply(defocusRadius);
        defocusDiskV = v.multiply(defocusRadius);
    }

    private Ray getRay(int i, int j) {
        Vec3 offset = new Vec3(Math.random() - 0.5, Math.random() - 0.5, 0);
        Vec3 pixelSample = pixel00Loc
            .add(pixelDeltaU.multiply(i + offset.x()))
            .add(pixelDeltaV.multiply(j + offset.y()));

        Vec3 rayOrigin = (defocusAngle <= 0) ? center : defocusDiskSample();
        Vec3 rayDirection = pixelSample.subtract(rayOrigin);
        return new Ray(rayOrigin, rayDirection);
    }

    private Vec3 defocusDiskSample() {
        Vec3 p = Vec3.randomInUnitDisk();
        return center
            .add(defocusDiskU.multiply(p.x()))
            .add(defocusDiskV.multiply(p.y()));
    }

    private Vec3 rayColor(Ray r, int depth, Hittable world) {
        if (depth <= 0) return new Vec3(0, 0, 0);

        HitRecord rec = new HitRecord();
        if (world.hit(r, new Interval(0.001, Double.POSITIVE_INFINITY), rec)) {
            ScatterRecord srec = new ScatterRecord();
            if (rec.mat.scatter(r, rec, srec)) {
                return srec.attenuation.multiply(rayColor(srec.scattered, depth - 1, world));
            }
            return new Vec3(0, 0, 0);
        }

        Vec3 unitDirection = r.direction().unit();
        double a = 0.5 * (unitDirection.y() + 1.0);
        return new Vec3(1.0, 1.0, 1.0).multiply(1.0 - a)
            .add(new Vec3(0.5, 0.7, 1.0).multiply(a));
    }
}
