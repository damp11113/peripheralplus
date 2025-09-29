package dev.dpsoftware.peripheralplus;

import org.joml.Quaternionf;

public class ppQuat {
    public double x, y, z, w;

    public ppQuat(double x, double y, double z, double w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    // Constructor for your manual input format
    public static ppQuat fromManualInput(double x, double y, double z, double w) {
        return new ppQuat(x, y, z, w);
    }

    // Create quaternion from Euler angles (in degrees)
    // The original implementation seems to use a non-standard rotation or coordinate system.
    public static ppQuat fromEuler(double pitch, double yaw, double roll) {
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

        return new ppQuat(x, y, z, w);
    }

    public static ppQuat fromXYZ(float angleX, float angleY, float angleZ, int i) {
        double xRad = Math.toRadians(angleX);
        double yRad = Math.toRadians(angleY);
        double zRad = Math.toRadians(angleZ);

        // Create individual rotation quaternions for each axis
        ppQuat qx = ppQuat.fromAxisAngle(xRad, 1.0, 0.0, 0.0); // Rotation around X (Pitch)
        ppQuat qy = ppQuat.fromAxisAngle(yRad, 0.0, 1.0, 0.0); // Rotation around Y (Yaw)
        ppQuat qz = ppQuat.fromAxisAngle(zRad, 0.0, 0.0, 1.0); // Rotation around Z (Roll)

        // Combine rotations in Z-Y-X order: Q = Q_z * (Q_y * Q_x)
        // Note: Quaternion multiplication is performed right-to-left in terms of rotation application.
        ppQuat q = qz.multiply(qy).multiply(qx);

        return q.normalize();
    }

    private static ppQuat fromAxisAngle(double angleRad, double x, double y, double z) {
        double halfAngle = angleRad / 2.0;
        double s = Math.sin(halfAngle);
        double c = Math.cos(halfAngle);

        // The axis (x, y, z) should be normalized, but since we are using unit vectors (1,0,0) etc.,
        // we skip the normalization here for performance.
        return new ppQuat(x * s, y * s, z * s, c);
    }

    public ppQuat multiply(ppQuat other) {
        double newW = this.w * other.w - this.x * other.x - this.y * other.y - this.z * other.z;
        double newX = this.w * other.x + this.x * other.w + this.y * other.z - this.z * other.y;
        double newY = this.w * other.y - this.x * other.z + this.y * other.w + this.z * other.x;
        double newZ = this.w * other.z + this.x * other.y - this.y * other.x + this.z * other.w;
        return new ppQuat(newX, newY, newZ, newW);
    }

    // Normalize the quaternion
    public ppQuat normalize() {
        double magnitude = Math.sqrt(x * x + y * y + z * z + w * w);
        if (magnitude > 1e-6) { // Check against a small epsilon instead of 0
            return new ppQuat(x / magnitude, y / magnitude, z / magnitude, w / magnitude);
        }
        return new ppQuat(0, 0, 0, 1);
    }

    public Quaternionf mcQuat() {
        Quaternionf mcQuat = new Quaternionf();
        mcQuat.x = (float) x;
        mcQuat.y = (float) y;
        mcQuat.z = (float) z;
        mcQuat.w = (float) w;
        return mcQuat;
    }

    public static ppQuat fromMCQuat(Quaternionf mcQuat) {
        return new ppQuat(mcQuat.x, mcQuat.y, mcQuat.z, mcQuat.w);
    }

    // Rotate a 3D vector using this quaternion
    // Assumes ppVec3 is available in the same package
    public ppVec3 rotate(ppVec3 vector) {
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

        // NOTE: This assumes ppVec3 has a constructor ppVec3(double x, double y, double z)
        // If ppVec3 is not defined, this class will not compile.
        return new ppVec3(rotX, rotY, rotZ);
    }
}
