package fr.chaos.engine.graphics;

import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

public class Mesh {

    public static float[] cubeVertices = {
        -0.5f, -0.5f,  0.5f,  0f, 0f,
        0.5f, -0.5f,  0.5f,  1f, 0f,
        0.5f,  0.5f,  0.5f,  1f, 1f,
        0.5f,  0.5f,  0.5f,  1f, 1f,
        -0.5f,  0.5f,  0.5f,  0f, 1f,
        -0.5f, -0.5f,  0.5f,  0f, 0f,


        -0.5f, -0.5f, -0.5f,  1f, 0f,
        -0.5f,  0.5f, -0.5f,  1f, 1f,
        0.5f,  0.5f, -0.5f,  0f, 1f,
        0.5f,  0.5f, -0.5f,  0f, 1f,
        0.5f, -0.5f, -0.5f,  0f, 0f,
        -0.5f, -0.5f, -0.5f,  1f, 0f,


        -0.5f,  0.5f,  0.5f,  1f, 1f,
        -0.5f,  0.5f, -0.5f,  0f, 1f,
        -0.5f, -0.5f, -0.5f,  0f, 0f,
        -0.5f, -0.5f, -0.5f,  0f, 0f,
        -0.5f, -0.5f,  0.5f,  1f, 0f,
        -0.5f,  0.5f,  0.5f,  1f, 1f,


        0.5f,  0.5f,  0.5f,  0f, 1f,
        0.5f, -0.5f, -0.5f,  1f, 0f,
        0.5f,  0.5f, -0.5f,  1f, 1f,
        0.5f, -0.5f, -0.5f,  1f, 0f,
        0.5f,  0.5f,  0.5f,  0f, 1f,
        0.5f, -0.5f,  0.5f,  0f, 0f,

        -0.5f,  0.5f, -0.5f,  0f, 1f,
        -0.5f,  0.5f,  0.5f,  0f, 0f,
        0.5f,  0.5f,  0.5f,  1f, 0f,
        0.5f,  0.5f,  0.5f,  1f, 0f,
        0.5f,  0.5f, -0.5f,  1f, 1f,
        -0.5f,  0.5f, -0.5f,  0f, 1f,


        -0.5f, -0.5f, -0.5f,  0f, 0f,
        0.5f, -0.5f, -0.5f,  1f, 0f,
        0.5f, -0.5f,  0.5f,  1f, 1f,
        0.5f, -0.5f,  0.5f,  1f, 1f,
        -0.5f, -0.5f,  0.5f,  0f, 1f,
        -0.5f, -0.5f, -0.5f,  0f, 0f,

    };

    private int vaoId;
    private int vboId;
    private int vertexCount;
    public  Vector3f position;
    public Vector3f rotation;
    private Vector3f scale;
    private Texture texture;

    public Mesh(float[] vertices, Vector3f position, Vector3f rotation, Vector3f scale, Texture texture) {
        vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);

        vboId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboId);

        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertices.length);
        vertexBuffer.put(vertices).flip();
        vertexCount = vertices.length / 5;

        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

        glVertexAttribPointer(0, 3, GL_FLOAT, false, 5 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);  

        glVertexAttribPointer(1, 2, GL_FLOAT, false, 5 * Float.BYTES,  3 * Float.BYTES);
        glEnableVertexAttribArray(1);  

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(vaoId);

        this.position = position;
        this.rotation = new Vector3f((float) Math.toRadians(rotation.x), (float) Math.toRadians(rotation.y), (float) Math.toRadians(rotation.z));
        this.scale = scale;
        this.texture = texture;
    }

    public void draw() {
        texture.bind();
        glBindVertexArray(vaoId);
        glDrawArrays(GL_TRIANGLES, 0, vertexCount);
        glBindVertexArray(0);
    }

    public Vector3f getPosition(){
        return(position);
    }

    public void cleanup() {
        //we are in java no need to be memory safe lol
        //biggest lie of 2026
    }
    public Matrix4f getModelMatrix(){
        Matrix4f matrix = new Matrix4f();
        matrix.translate(position);
        matrix.rotateXYZ(rotation);
        matrix.scale(scale);
        return matrix;
    }

    public int getVaoId() {
        return vaoId;
    }

    public int getVertexCount() {
        return vertexCount;
    }
}
