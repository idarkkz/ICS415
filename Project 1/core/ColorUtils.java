package core;

import java.io.PrintWriter;
import math.Interval;
import math.Vec3;

public class ColorUtils {

    public static double linearToGamma(double linearComponent) {
        return linearComponent > 0 ? Math.sqrt(linearComponent) : 0;
    }

    public static void writeColor(PrintWriter out, Vec3 color, int samplesPerPixel) {
        double r = color.x();
        double g = color.y();
        double b = color.z();

        double scale = 1.0 / samplesPerPixel;
        r = linearToGamma(r * scale);
        g = linearToGamma(g * scale);
        b = linearToGamma(b * scale);

        Interval intensity = new Interval(0.0, 0.999);
        int rByte = (int)(256 * intensity.clamp(r));
        int gByte = (int)(256 * intensity.clamp(g));
        int bByte = (int)(256 * intensity.clamp(b));

        out.printf("%d %d %d\n", rByte, gByte, bByte);
    }
}
