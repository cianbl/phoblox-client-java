package fr.chaos.engine.graphics;

import static org.lwjgl.opengl.GL11.GL_REPEAT;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glDeleteTextures;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;

import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBImage;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.file.Files;

public class Texture {
    private int id;
    private int width;
    private int height;
    private int channels;
    private String filePath;

    public Texture(String filePath) {
        this.filePath = filePath;
        
        id = glGenTextures();
        
        glBindTexture(GL_TEXTURE_2D, id);
        
        
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        
        IntBuffer w = BufferUtils.createIntBuffer(1);
        IntBuffer h = BufferUtils.createIntBuffer(1);
        IntBuffer c = BufferUtils.createIntBuffer(1);

        ByteBuffer image;
        try {
            File tmp = Files.createTempFile("tex", ".png").toFile(); tmp.deleteOnExit();
            Files.copy(getClass().getClassLoader().getResourceAsStream(filePath), tmp.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            image = STBImage.stbi_load(tmp.getAbsolutePath(), w, h, c, 4);
        } catch (IOException e) {
            throw new RuntimeException("IO Exception while loading texture: " + e.getMessage(), e);
        }
        
        if (image == null) {
            String error = STBImage.stbi_failure_reason();
            System.err.println("[TEXTURE] FAILED: " + error);
            throw new RuntimeException("Failed to load texture: " + error);
        }
        
        this.width = w.get(0);
        this.height = h.get(0);
        this.channels = c.get(0);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, image);
        glGenerateMipmap(GL_TEXTURE_2D);
        STBImage.stbi_image_free(image);
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public void bind() {
        glBindTexture(GL_TEXTURE_2D, id);
    }

    public void unbind() {
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public void cleanup() {
        glDeleteTextures(id);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getChannels() {
        return channels;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setChannels(int channels) {
        this.channels = channels;
    }
}
