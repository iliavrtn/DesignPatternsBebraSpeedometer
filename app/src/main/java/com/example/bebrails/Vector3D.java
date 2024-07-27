package com.example.bebrails;

public class Vector3D {
    private float x;
    private float y;
    private float z;

    public Vector3D(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getZ() {
        return z;
    }

    public void setZ(float z) {
        this.z = z;
    }

    public Vector3D add(Vector3D other) {
        return new Vector3D(this.x + other.x, this.y + other.y, this.z + other.z);
    }

    public Vector3D subtract(Vector3D other) {
        return new Vector3D(this.x - other.x, this.y - other.y, this.z - other.z);
    }

    public Vector3D scale(float scalar) {
        return new Vector3D(this.x * scalar, this.y * scalar, this.z * scalar);
    }

    public float dotProduct(Vector3D other) {
        return this.x * other.x + this.y * other.y + this.z * other.z;
    }

    public Vector3D crossProduct(Vector3D other) {
        float crossX = this.y * other.z - this.z * other.y;
        float crossY = this.z * other.x - this.x * other.z;
        float crossZ = this.x * other.y - this.y * other.x;
        return new Vector3D(crossX, crossY, crossZ);
    }

    public float magnitude() {
        return (float) Math.sqrt(x * x + y * y + z * z);
    }

    public Vector3D normalize() {
        float magnitude = magnitude();
        return new Vector3D(x / magnitude, y / magnitude, z / magnitude);
    }

    @Override
    public String toString() {
        return "Vector3D(" + x + ", " + y + ", " + z + ")";
    }
}
