package fr.chaos.engine.depreciated;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

// This class is deprecated. Please don't use it. Or maybe do it, but at your own risks. 
public class ShaderUtils{
    public static int vertexShader;
    public static int fragmentShader;
    public static int shaderProgram;
    public static CharSequence openShader(String shaderName)
    {
        CharSequence shader = new StringBuffer("");
        try (BufferedReader br = new BufferedReader(new FileReader("src/main/resources/" + shaderName))) {
            String line;
            while ((line = br.readLine()) != null) {
                shader = new StringBuilder(shader + "\n" +line);
            }
        } catch (IOException e) {
            System.out.println("Error reading file.");
        }

        return(shader);
    }

    public static void createShaders(){
        CharSequence vertShaderSource = openShader("vertex.glsl");
        vertexShader = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexShader, vertShaderSource);
        glCompileShader(vertexShader);
        CharSequence fragShaderSource = openShader("fragment.glsl");
        fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentShader, fragShaderSource);
        glCompileShader(fragmentShader);
        int success = glGetShaderi(vertexShader, GL_COMPILE_STATUS);
        if (success == GL_FALSE) {
            System.out.println(glGetShaderInfoLog(vertexShader));
        }
        success = glGetShaderi(fragmentShader, GL_COMPILE_STATUS);
        if (success == GL_FALSE) {
            System.out.println(glGetShaderInfoLog(fragmentShader));
        }

    }

    public static void setupShaderProgram(){
        shaderProgram = glCreateProgram();
        glAttachShader(shaderProgram, vertexShader);
        glAttachShader(shaderProgram, fragmentShader);
        glLinkProgram(shaderProgram);
        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);
    }

}