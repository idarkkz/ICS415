import core.Camera;
import math.Vec3;
import scene.HittableList;
import scene.Sphere;
import materials.Lambertian;
import materials.Metal;
import materials.Dielectric;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Main {
    public static void main(String[] args) {
        HittableList world = new HittableList();

        Lambertian groundMaterial = new Lambertian(new Vec3(0.5, 0.5, 0.5));
        world.add(new Sphere(new Vec3(0, -1000, 0), 1000, groundMaterial));

        for (int a = -11; a < 11; a++) {
            for (int b = -11; b < 11; b++) {
                double chooseMat = Math.random();
                Vec3 center = new Vec3(a + 0.9 * Math.random(), 0.2, b + 0.9 * Math.random());

                if (center.subtract(new Vec3(4, 0.2, 0)).length() > 0.9) {
                    if (chooseMat < 0.8) {
                        Vec3 albedo = Vec3.random().multiply(Vec3.random());
                        world.add(new Sphere(center, 0.2, new Lambertian(albedo)));
                    } else if (chooseMat < 0.95) {
                        Vec3 albedo = Vec3.random(0.5, 1);
                        double fuzz = Math.random() * 0.5;
                        world.add(new Sphere(center, 0.2, new Metal(albedo, fuzz)));
                    } else {
                        world.add(new Sphere(center, 0.2, new Dielectric(1.5)));
                    }
                }
            }
        }

        world.add(new Sphere(new Vec3(0, 1, 0), 1.0, new Dielectric(1.5)));
        world.add(new Sphere(new Vec3(-4, 1, 0), 1.0, new Lambertian(new Vec3(0.4, 0.2, 0.1))));
        world.add(new Sphere(new Vec3(4, 1, 0), 1.0, new Metal(new Vec3(0.7, 0.6, 0.5), 0.0)));

        Camera cam = new Camera();
        cam.aspectRatio = 16.0 / 9.0;
        cam.imageWidth = 1920;
        cam.samplesPerPixel = 10;
        cam.maxDepth = 20;

        cam.vfov = 20;
        cam.lookFrom = new Vec3(13, 2, 3);
        cam.lookAt = new Vec3(0, 0, 0);
        cam.vup = new Vec3(0, 1, 0);
        cam.defocusAngle = 0.6;
        cam.focusDist = 10.0;

        try (PrintWriter out = new PrintWriter(new FileWriter("image.ppm"))) {
            cam.render(world, out);
        } catch (IOException e) {
            System.err.println("Failed to write image: " + e.getMessage());
        }
    }
}
