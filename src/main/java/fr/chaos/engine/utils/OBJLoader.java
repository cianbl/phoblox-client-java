package fr.chaos.engine.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class OBJLoader {

    // === Données temporaires de parsing ===
    // (utilisées uniquement pendant le load)

    public ArrayList<Float> vertices = new ArrayList<>();
    public ArrayList<Float> uvs = new ArrayList<>();
    public ArrayList<Float> faces = new ArrayList<>();

    public OBJLoader() {
        // constructeur vide
    }

    // === Point d’entrée principal ===
    public float[] load(String path) {
        StringBuilder model = openModel(path);
        parser(model);
        float[] mesh = new float[faces.size()];
        for (int i = 0; i < faces.size(); i++) {
            mesh[i] = faces.get(i);
        }
        return mesh;
    }

    
    private void parser(StringBuilder model) {
        String[] lines = model.toString().split("\n");
        for(int i = 0; i < lines.length; i++){
            if(lines[i].startsWith("#") || lines[i].isEmpty()){
                System.out.println("Comment/Empty Line");
            } else if (lines[i].startsWith("v ")){
                //System.out.println("Vertex");
                parseVertex(lines[i]);
            } else if (lines[i].startsWith("vt ")){
                //System.out.println("UV");
                parseTexCoord(lines[i]);
            } else if (lines[i].startsWith("f ")){
                //System.out.println("Face");
                parseFace(lines[i]);
            }
        }
    }

    // === Parsing bas niveau ===
    private void parseVertex(String line) {
        // Oh that is really not pretty at all
        String[] str_vertexs = line.split(" ");
        ArrayList<Float> line_vertices = new ArrayList<>();
        for (int i = 1; i < str_vertexs.length; i++) {
            line_vertices.add(Float.parseFloat(str_vertexs[i]));
        }
        for (int i = 0; i < line_vertices.size(); i++) {
            vertices.add(line_vertices.get(i));
        }
    }

    private void parseTexCoord(String line) {
        String[] tokens = line.trim().split("\\s+");
        // tokens[0] = "vt", tokens[1..2] = u, v
        for (int i = 1; i < tokens.length && i <= 2; i++) {
            uvs.add(Float.parseFloat(tokens[i]));
        }
    }

    private void parseFace(String line) {
        String[] tokens = line.trim().split("\\s+");
        if (tokens.length != 4) {
            // pas un tri -> on ignore
            System.err.println("Non-triangle face ignored: " + line);
            return;
        }

        for (int i = 1; i < 4; i++) { // 3 sommets
            String[] indices = tokens[i].split("/");
            if (indices.length < 2 || indices[0].isEmpty() || indices[1].isEmpty()) {
                System.err.println("Unsupported face format (need v/vt): " + line);
                return;
            }

            int vIndex  = Integer.parseInt(indices[0]) - 1; // OBJ est 1-based
            int vtIndex = Integer.parseInt(indices[1]) - 1;

            int vp = vIndex * 3;
            int tp = vtIndex * 2;

            if (vp + 2 >= vertices.size() || tp + 1 >= uvs.size()) {
                System.err.println("Face index out of range: " + line);
                return;
            }

            float x = vertices.get(vp);
            float y = vertices.get(vp + 1);
            float z = vertices.get(vp + 2);

            float u = uvs.get(tp);
            float v = - uvs.get(tp + 1);

            // même layout que cubeVertices : x, y, z, u, v
            faces.add(x);
            faces.add(y);
            faces.add(z);
            faces.add(u);
            faces.add(v);
        }
    }






    public static StringBuilder openModel(String filename) {
        StringBuilder obj = new StringBuilder();
        
        try (InputStream is = OBJLoader.class.getResourceAsStream("/" + filename);
            BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            
            String line;
            while ((line = br.readLine()) != null) {
                obj.append(line).append("\n");
            }
            
        } catch (IOException | NullPointerException e) {
            System.out.println("Error reading Model: " + filename);
            e.printStackTrace();
        }
        
        return obj;
    }
}

