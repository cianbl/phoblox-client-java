package fr.chaos.engine.core;

import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.MemoryUtil;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Engine {
    private long window;
    public static int windowHeight = 720;
    public static int windowWidth = 1280;
    public static int lockFrames = 1;
    public static String WindowTitle = "Phoblox Client - Chaos Engine";

    public void run(){
        System.out.println("Starting ChaosEngine...");
        init();
        loop();

        glfwDestroyWindow(window);
        glfwTerminate();
    }

    private void init(){
        // Callback d'erreur pour debug
        GLFWErrorCallback.create((error, description) -> {
            System.err.printf("GLFW Error %d: %s%n", error, MemoryUtil.memUTF8(description));
        }).set();

        if (!glfwInit())
            throw new IllegalStateException("Failed to init GLFW");

        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        // Tentative 1: OpenGL hardware
        window = glfwCreateWindow(windowWidth, windowHeight, WindowTitle, NULL, NULL);
        
        if (window == NULL) {
            System.err.println("Hardware OpenGL try failed, attempting software");
            
            // Tentative 2: Force software OpenGL
            glfwTerminate(); // Nettoie proprement
            System.setProperty("org.lwjgl.opengl.Display.allowSoftwareOpenGL", "true");
            
            if (!glfwInit()) {
                throw new IllegalStateException("Failed to restart GLFW when switching to Software OpenGL");
            }
            
            // Force OpenGL 2.1 pour compatibilité software
            glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 2);
            glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 1);
            
            window = glfwCreateWindow(windowWidth, windowHeight, WindowTitle + " (Running in \"Compat\" Mode)", NULL, NULL);
            
            if (window == NULL) {
                throw new RuntimeException("Failed to create Software OpenGL Window");
            } else {
                System.out.println("Tried fallback mode. this will probably not fix ANYTHING, but we never know tbh.");
            }
        } else {
            System.out.println("Working OpenGL Hardware");
        }

        GLFW.glfwSetFramebufferSizeCallback(window, (win, width, height) -> {
            GL11.glViewport(0, 0, width, height);
        });

        // Reste du code inchangé...
        GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        if (vidmode != null)
            glfwSetWindowPos(window, (vidmode.width() - windowWidth) / 2, (vidmode.height() - windowHeight) / 2);

        glfwMakeContextCurrent(window);
        glfwSwapInterval(lockFrames);
        System.out.println(lockFrames);
        glfwShowWindow(window);

        GL.createCapabilities();
        glClearColor(0.1f, 0.1f, 0.3f, 1.0f);
        Renderer.init();
    }

    private void loop() {
        double lastTime = glfwGetTime();
        int frames = 0;
        while (!glfwWindowShouldClose(window)) {
            double currentTime = glfwGetTime();
            frames++;

            if (currentTime - lastTime >= 1.0) { 
                System.out.println("FPS: " + frames);
                frames = 0;
                lastTime = currentTime;
            }
            glClear(GL_COLOR_BUFFER_BIT);
            Renderer.update(window);
            Renderer.render();
            glfwSwapBuffers(window);
            glfwPollEvents();


            if (glfwGetKey(window, GLFW_KEY_ESCAPE) == GLFW_PRESS)
                //Renderer.onQuit();
                glfwSetWindowShouldClose(window, true);
        }
        Renderer.onQuit();
    }

    public static void main(String[] args){
        for(int i = 0; i == args.length-1; i++){
            System.out.println("argument: " + args[i]);
        }
        if(args.length == 1){
            System.err.println("True");
        }else{
            System.err.println("False");
            System.err.println("n="+args.length);
        }
        if(args.length == 1){
            lockFrames = 0;
        }
        new Engine().run();
    }
}
