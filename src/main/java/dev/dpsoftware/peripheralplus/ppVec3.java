package dev.dpsoftware.peripheralplus;

import net.minecraft.world.phys.Vec3;

public class ppVec3 {
    public double x, y, z;

    public ppVec3(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vec3 mcVec3() {
        return new Vec3(x, y, z);
    }

    // Rotate around X axis
    public ppVec3 rotateX(double angle) {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        return new ppVec3(
                x,
                y * cos - z * sin,
                y * sin + z * cos
        );
    }

    // Rotate around Y axis
    public ppVec3 rotateY(double angle) {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        return new ppVec3(
                x * cos + z * sin,
                y,
                -x * sin + z * cos
        );
    }

    // Rotate around Z axis
    public ppVec3 rotateZ(double angle) {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        return new ppVec3(
                x * cos - y * sin,
                x * sin + y * cos,
                z
        );
    }

    // Rotate around arbitrary axis (normalized vector)
    public ppVec3 rotateAroundAxis(ppVec3 axis, double angle) {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        double oneMinusCos = 1.0 - cos;

        // Rodrigues' rotation formula
        double ax = axis.x, ay = axis.y, az = axis.z;

        return new ppVec3(
                (cos + ax * ax * oneMinusCos) * x +
                        (ax * ay * oneMinusCos - az * sin) * y +
                        (ax * az * oneMinusCos + ay * sin) * z,

                (ay * ax * oneMinusCos + az * sin) * x +
                        (cos + ay * ay * oneMinusCos) * y +
                        (ay * az * oneMinusCos - ax * sin) * z,

                (az * ax * oneMinusCos - ay * sin) * x +
                        (az * ay * oneMinusCos + ax * sin) * y +
                        (cos + az * az * oneMinusCos) * z
        );
    }

    // Rotate with yaw, pitch, roll (Euler angles)
    public ppVec3 rotateYawPitchRoll(double yaw, double pitch, double roll) {
        return this.rotateY(yaw).rotateX(pitch).rotateZ(roll);
    }

    // Helper method to normalize this vector (useful for axis rotation)
    public ppVec3 normalize() {
        double length = Math.sqrt(x * x + y * y + z * z);
        if (length == 0) return new ppVec3(0, 0, 0);
        return new ppVec3(x / length, y / length, z / length);
    }

    // Helper method to get length
    public double length() {
        return Math.sqrt(x * x + y * y + z * z);
    }
}