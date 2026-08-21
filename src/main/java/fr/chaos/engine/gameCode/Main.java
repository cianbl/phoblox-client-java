package fr.chaos.engine.gameCode;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.joml.Vector4f;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glEnable;

import java.util.Map;
import java.util.HashMap;

import fr.chaos.engine.core.Engine;
import fr.chaos.engine.core.ShaderProgram;
import fr.chaos.engine.graphics.Camera;
import fr.chaos.engine.graphics.Mesh;
import fr.chaos.engine.graphics.Texture;

import static org.lwjgl.glfw.GLFW.*;

import org.apache.commons.lang3.ArrayUtils;

import javax.swing.JOptionPane;

public class Main {
    public static ShaderProgram shader;
    public static Mesh[] cubes;
    public static Vector3i[] colors;
    public static Texture bogusTexture;
    public static Camera camera;
    public static String map;
    public static Network net;
    public static Mesh player;
    public static Coroutine updatePlayerPos;
    public static int playerID;
    public static String userProvidedGame;
    public static Map<Long, Mesh> players = new HashMap<>(); 
    public static void init(){    
        Engine.WindowTitle = "Phoblox Client - Java";
        shader = new ShaderProgram("vertex.glsl", "uniform_color.glsl");
        bogusTexture = new Texture("wall.png");
        camera = new Camera(new Vector3f(0f, 0f, 0), new Vector3f(0f,0f,0f), 70, (float) Engine.windowWidth/Engine.windowHeight, 0.01f, 100f);
        String userProvidedURL = JOptionPane.showInputDialog("What is the server's IP and port? (leave empty for default.)");
        if(userProvidedURL == null || userProvidedURL.isEmpty()){
            net = new Network("http://192.168.1.69:15385/");
        } else{
            System.out.println("userProvidedURL equals: " + userProvidedURL);
            net = new Network("http://" + userProvidedURL + "/");
        }
        userProvidedGame = JOptionPane.showInputDialog("What is the game name?");
        map = net.getMap(userProvidedGame);
        player = new Mesh(Mesh.cubeVertices, new Vector3f(0f, 0f, 0f), new Vector3f(0f, 0f, 0f), new Vector3f(1f, 1f, 1f), bogusTexture);
        Utils.parseMap(map);
        playerID = Integer.parseInt(net.joinGame(userProvidedGame).replaceAll("[^0-9]", ""));
        updatePlayerPos = new Coroutine(() -> NetworkLogicManager.MainLoop(), 0.01f);
        NetworkLogicManager.initVariables(userProvidedGame, playerID, player, net);
    }
    
    public static void render(){
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glEnable(GL_DEPTH_TEST);
        shader.use();
        bogusTexture.bind();
        shader.setUniform("view", camera.getViewMatrix());
        shader.setUniform("projection", camera.getProjectionMatrix());
        renderMeshes();
        renderPlayers();
    }

    public static void createMesh(Vector3f position, Vector3f rotation, Vector3f scale, Vector3i color){
        cubes = ArrayUtils.add(cubes, new Mesh(Mesh.cubeVertices, position, rotation, scale, bogusTexture));
        colors = ArrayUtils.add(colors, color);
    }

    public static void createPlayer(long id){
        players.put(id, new Mesh(Mesh.cubeVertices, new Vector3f(0,0,0), new Vector3f(0, 0, 0), new Vector3f(0.01f, 0.015f, 0.01f), bogusTexture));
    }

    public static void renderMeshes(){
        for(int i = 0; i < cubes.length; i++){
            shader.setUniform("model", cubes[i].getModelMatrix());
            shader.setUniformVec4("u_Color", new Vector4f((float) colors[i].x / 255, (float) colors[i].y / 255, (float) colors[i].z / 255, 1f));
            cubes[i].draw();
        }
    }

    public static void renderPlayers(){
        for(Map.Entry<Long, Mesh> child : Main.players.entrySet()){
            shader.setUniform("model", child.getValue().getModelMatrix());
            shader.setUniformVec4("u_Color", new Vector4f(127 / 255, 127 / 255, 127 / 255, 1f));
            child.getValue().draw();
        }
    }

    static boolean rmbWasDown;
    static double lastMx, lastMy;
    static final float sens = 0.12f; // deg/pixel
    static final float maxPitch = 89f;

    public static void update(long window) {
        player.position = camera.position;
        updatePlayerPos.UpdateCoroutine();
        characterController(window);
    }

    public static void onQuit(){
        System.out.println("Quiting Game...");
        net.quitGame(playerID, userProvidedGame);
        System.exit(0);
    }

    public static void characterController(long window){

        float deltaTime = 0.016f;
        float speed = 0.1f * deltaTime;

        Vector3f pos = camera.getPosition();
        Vector3f rot = camera.getRotation(); // deg: x=pitch, y=yaw

        // --- Mouse look (RMB only) ---
        boolean rmbDown = glfwGetMouseButton(window, GLFW_MOUSE_BUTTON_RIGHT) == GLFW_PRESS; // [web:17]
        if (rmbDown) {
            glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED); // [web:16]

            double[] mx = new double[1], my = new double[1];
            glfwGetCursorPos(window, mx, my); // [web:17]

            if (!rmbWasDown) {
                lastMx = mx[0];
                lastMy = my[0];
            } else {
                double dx = mx[0] - lastMx;
                double dy = my[0] - lastMy;
                lastMx = mx[0];
                lastMy = my[0];

                rot.y -= (float) dx * sens;   // yaw
                rot.x -= (float) dy * sens;   // pitch

                if (rot.x >  maxPitch) rot.x =  maxPitch;
                if (rot.x < -maxPitch) rot.x = -maxPitch;
            }
        } else if (rmbWasDown) {
            glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_NORMAL); // [web:16]
        }
        rmbWasDown = rmbDown;

        // --- Movement using view matrix axes (matches render) --- 
        Matrix4f view = camera.getViewMatrix(); // ta view matrix (avec -rot et -pos)

        // axes caméra en world (extraits de la matrice)
        Vector3f right   = new Vector3f(view.m00(), view.m10(), view.m20()).normalize();
        Vector3f forward = new Vector3f(-view.m02(), -view.m12(), -view.m22()).normalize();

        // (optionnel) si tu veux un freecam "vrai": W/S inclut pitch => forward tel quel.
        // si tu veux un FPS "sol": décommente:
        // forward.y = 0; forward.normalize();

        if (glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS) { pos.x += forward.x * speed; pos.y += forward.y * speed; pos.z += forward.z * speed; }
        if (glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS) { pos.x -= forward.x * speed; pos.y -= forward.y * speed; pos.z -= forward.z * speed; }
        if (glfwGetKey(window, GLFW_KEY_D) == GLFW_PRESS) { pos.x += right.x   * speed; pos.z += right.z   * speed; }
        if (glfwGetKey(window, GLFW_KEY_A) == GLFW_PRESS) { pos.x -= right.x   * speed; pos.z -= right.z   * speed; }

        if (glfwGetKey(window, GLFW_KEY_SPACE) == GLFW_PRESS) pos.y += speed;
        if (glfwGetKey(window, GLFW_KEY_LEFT_SHIFT) == GLFW_PRESS) pos.y -= speed;
    }

}
