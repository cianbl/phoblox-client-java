package fr.chaos.engine.gameCode;

import fr.chaos.engine.graphics.Mesh;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import org.joml.Vector3f;

import java.lang.reflect.Type;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.LinkedList;

public class NetworkLogicManager {
    private static boolean init = false;
    public static String gameID;
    public static int userID;
    public static Mesh player;
    public static Network net;
    private static String GlobalJson;
    private static boolean canUpdate;
    public static void initVariables(String gid, int uid, Mesh plr, Network network){
        gameID = gid;
        userID = uid;
        player = plr;
        net = network;
        init = true;
    }

    public static void MainLoop(){
        if(init){
            sendPlayerPosition();
            updatePlayers();
        } else{
            throw new RuntimeException("NetworkLogicManager: tryed to run Main Loop without initialising first.");
        }

    }

    public static void sendPlayerPosition(){
        net.updatePlayerPosition(player.getPosition().x, player.getPosition().y, player.getPosition().z, userID, gameID);
    }

    static Map<Long, Vector3f> targetPositions = new HashMap<>();;
    public static void updatePlayers(){
        targetPositions.clear();
        net.getPlayersPositions(gameID).thenAccept(json -> {GlobalJson = json;}).whenComplete((result, ex) -> {canUpdate = true; if (ex != null) { ex.printStackTrace();}});
        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Type type = new TypeToken<Map<String, Map<String, Float>>>(){}.getType();
        if(GlobalJson != null){
            Map<String, Map<String, Float>> players = gson.fromJson(GlobalJson, type);

            for (Map.Entry<String, Map<String, Float>> entry : players.entrySet())
            {
                long playerID = Long.parseLong(entry.getKey());
                if(playerID != userID){
                    float x = entry.getValue().get("x");
                    float y = entry.getValue().get("y");
                    float z = entry.getValue().get("z");
                    targetPositions.put(playerID, new Vector3f(x,y,z));
                }
            }
            
            List<Long> keys = new LinkedList<Long>(targetPositions.keySet());

            if(Main.players.size() != 0 && targetPositions.size() == Main.players.size()){
                for (Map.Entry<Long, Mesh> player : Main.players.entrySet()){
                    //child.position = targetPositions[long.Parse(child.gameObject.name)];
                    Mesh newPlayer = player.getValue();
                    newPlayer.position = targetPositions.get(player.getKey());
                    Main.players.replace(player.getKey(), newPlayer);
                }
            } else if (Main.players.size() != 0 && targetPositions.size() > Main.players.size()) {
                int difference = targetPositions.size() - Main.players.size();
                for (int i = 0; i < difference; i++) {
                    Main.createPlayer(keys.get(keys.size() - difference + i));
                }
                for (Map.Entry<Long, Mesh> player : Main.players.entrySet()) {
                    Mesh newPlayer = player.getValue();
                    newPlayer.position = targetPositions.get(player.getKey());
                    Main.players.replace(player.getKey(), newPlayer);
                }
            } else if (Main.players.size() != 0 && targetPositions.size() < Main.players.size()){
                Main.players.keySet().removeIf(id -> !keys.contains(id));
                for (Map.Entry<Long, Mesh> player : Main.players.entrySet()){
                    //child.position = targetPositions[long.Parse(child.gameObject.name)];
                    Mesh newPlayer = player.getValue();
                    newPlayer.position = targetPositions.get(player.getKey());
                    Main.players.replace(player.getKey(), newPlayer);
                }
            } else if (Main.players.size() == 0 && targetPositions.size() > 0){
                for (int i = 0; i < targetPositions.size(); i++){
                    //GameObject NetCh = Instantiate(NetworkChild, NetworkChildsContainer.transform);
                    //NetCh.name = Convert.ToString(keys[i]);
                    Main.createPlayer(keys.get(i));
                }
                for (Map.Entry<Long, Mesh> player : Main.players.entrySet()){
                    //child.position = targetPositions[long.Parse(child.gameObject.name)];
                    Mesh newPlayer = player.getValue();
                    newPlayer.position = targetPositions.get(player.getKey());
                    Main.players.replace(player.getKey(), newPlayer);
                }
            }
            canUpdate = false;
        }
        
    }



}