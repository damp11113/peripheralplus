package dev.dpsoftware.peripheralplus;

public class Quat {
    public double x, y, z, w;

    public Quat(double x, double y, double z, double w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    // Constructor for your manual input format
    public static Quat fromManualInput(double x, double y, double z, double w) {
        return new Quat(x, y, z, w);
    }

    // Create quaternion from Euler angles (in degrees)
    public static Quat fromEuler(double pitch, double yaw, double roll) {
        // Convert to radians and apply Y offset (-90°)
        double adjustedYaw = yaw - 90.0;

        double pitchRad = Math.toRadians(pitch) * 0.5;
        double yawRad = Math.toRadians(adjustedYaw) * 0.5;
        double rollRad = Math.toRadians(roll) * 0.5;

        double cy = Math.cos(yawRad);
        double sy = Math.sin(yawRad);
        double cp = Math.cos(pitchRad);
        double sp = Math.sin(pitchRad);
        double cr = Math.cos(rollRad);
        double sr = Math.sin(rollRad);

        double w = (cr * cp * cy + sr * sp * sy);
        double x = (sr * cp * cy - cr * sp * sy);
        double y = (cr * sp * cy + sr * cp * sy);
        double z = (cr * cp * sy - sr * sp * cy);

        return new Quat(x, y, z, w);
    }

    // Normalize the quaternion
    public Quat normalize() {
        double magnitude = Math.sqrt(x * x + y * y + z * z + w * w);
        if (magnitude > 0) {
            return new Quat(x / magnitude, y / magnitude, z / magnitude, w / magnitude);
        }
        return new Quat(0, 0, 0, 1);
    }

    // Rotate a 3D vector using this quaternion
    public Vec3 rotate(Vec3 vector) {
        double qx = this.x, qy = this.y, qz = this.z, qw = this.w;
        double vx = vector.x, vy = vector.y, vz = vector.z;

        // Quaternion rotation formula: v' = q * v * q^-1
        double x2 = qx + qx;
        double y2 = qy + qy;
        double z2 = qz + qz;
        double xx = qx * x2;
        double xy = qx * y2;
        double xz = qx * z2;
        double yy = qy * y2;
        double yz = qy * z2;
        double zz = qz * z2;
        double wx = qw * x2;
        double wy = qw * y2;
        double wz = qw * z2;

        double rotX = vx * (1.0 - (yy + zz)) + vy * (xy - wz) + vz * (xz + wy);
        double rotY = vx * (xy + wz) + vy * (1.0 - (xx + zz)) + vz * (yz - wx);
        double rotZ = vx * (xz - wy) + vy * (yz + wx) + vz * (1.0 - (xx + yy));

        return new Vec3(rotX, rotY, rotZ);
    }
}