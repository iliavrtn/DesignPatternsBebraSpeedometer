package com.example.bebrails;

public class Vector3D {
    private final float x;
    private final float y;
    private final float z;

    public Vector3D(float x, float y, float z) {
        this.x = x;
        this.y = y;
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


    public float magnitude() {
        return (float) Math.sqrt(x * x + y * y + z * z);
    }

}
