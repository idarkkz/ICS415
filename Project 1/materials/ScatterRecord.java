package materials;

import core.Ray;
import math.Vec3;

public class ScatterRecord {
    public Ray scattered;
    public Vec3 attenuation;

    public ScatterRecord() {
        this.attenuation = new Vec3(0, 0, 0);
        this.scattered = null;
    }
}
