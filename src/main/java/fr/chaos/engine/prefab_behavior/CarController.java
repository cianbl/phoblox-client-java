package fr.chaos.engine.prefab_behavior;

import org.joml.Vector3f;


public class CarController {
    public Vector3f position = new Vector3f(0.0f, 0.0f, 0.0f);
    public float rotationY = 0;

    public float speed = 0;
    public float maxSpeed = 100; 
    public float acceleration = 20;
    public float deceleration = 30; 
    public float turnSpeed = 50; 

    public float inputForward = 0; 
    public float inputTurn = 0; 

    public void update(float deltaTime) {
        if (inputForward != 0) {
            speed += inputForward * acceleration * deltaTime;
        } else {
            if (speed > 0) {
                speed -= deceleration * deltaTime;
                if (speed < 0) speed = 0;
            } else if (speed < 0) {
                speed += deceleration * deltaTime;
                if (speed > 0) speed = 0;
            }
        }

        if (speed > maxSpeed) speed = maxSpeed;
        if (speed < -maxSpeed/2) speed = -maxSpeed/2; 

        rotationY += inputTurn * turnSpeed * deltaTime * (speed != 0 ? Math.signum(speed) : 1);

        position.x += Math.sin(Math.toRadians(rotationY)) * speed * deltaTime;
        position.z += Math.cos(Math.toRadians(rotationY)) * speed * deltaTime;
    }

    public Vector3f getCameraPosition() {
        Vector3f camOffset = new Vector3f(0.0f, 5.0f, -10.0f); 
        float rad = (float)Math.toRadians(rotationY);
        return new Vector3f(
            position.x + camOffset.z * (float)Math.sin(rad),
            position.y + camOffset.y,
            position.z + camOffset.z * (float)Math.cos(rad)
        );
    }
}
