package com.example.bebrails;

public class Speedometer {
    public static final float NANOSECONDS_IN_SECOND = 1000000000.0f;
    Vector3D gravity;
    Vector3D velocity = new Vector3D(0.0f,0.0f,0.0f);
    private long lastUpdateTime = 0;
    private static final float SPEED_THRESHOLD = 0.1f;
    private static final float NOISE_THRESHOLD = 0.1f;

    void updateGravity(Vector3D gravity) {
        this.gravity = gravity;
    }

    void updateAcceleration(Vector3D acceleration) {
        if(gravity == null){
            return;
        }
        long currentTime = System.nanoTime();
        float deltaTime = (currentTime - lastUpdateTime) / NANOSECONDS_IN_SECOND;

        if(lastUpdateTime == 0) {
            lastUpdateTime = currentTime;
            return;
        }
        lastUpdateTime = currentTime;

        acceleration = acceleration.subtract(gravity);

        if (acceleration.magnitude()>NOISE_THRESHOLD) {
            velocity = velocity.add(acceleration.scale(deltaTime));
        }
    }

    float getSpeed() {
        return velocity.magnitude();
    }


}
