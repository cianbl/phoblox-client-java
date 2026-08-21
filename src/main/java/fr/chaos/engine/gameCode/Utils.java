package fr.chaos.engine.gameCode;

import org.joml.Vector3f;
import org.joml.Vector3i;

class Utils{

    public static void parseMap(String mapFile){
        mapFile = mapFile.replace(",", "");
        mapFile = mapFile.replace("\"", "");
        mapFile = mapFile.replace("\\n", " ");
        String[] mapData = mapFile.split(" ");
        for(int i = 0; i < mapData.length/13; i++){
            Main.createMesh(
                new Vector3f(Float.parseFloat(mapData[1 + (i * 13)]) / 100, Float.parseFloat(mapData[2 + (i * 13)]) / 100, -Float.parseFloat(mapData[3 + (i * 13)]) / 100),
                new Vector3f(Float.parseFloat(mapData[4 + (i * 13)]), Float.parseFloat(mapData[5 + (i * 13)]), Float.parseFloat(mapData[6 + (i * 13)])), 
                new Vector3f(Float.parseFloat(mapData[7 + (i * 13)]) / 100, Float.parseFloat(mapData[8 + (i * 13)])/ 100, Float.parseFloat(mapData[9 + (i * 13)])/ 100),
                new Vector3i(Integer.parseInt(mapData[10 + (i * 13)]), Integer.parseInt(mapData[11 + (i * 13)]), Integer.parseInt(mapData[12 + (i * 13)]))
            );
        }
        
    }

}