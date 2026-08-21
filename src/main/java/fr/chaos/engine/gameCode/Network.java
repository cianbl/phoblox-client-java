package fr.chaos.engine.gameCode;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
public class Network{
    private String server_url = "";
    private static final ExecutorService NETWORK_POOL = Executors.newFixedThreadPool(2);

    public Network(String URL){
        server_url = URL;
    }

    public String getMap(String gameName){
        try{
            return runMultiThreadedAndReturnString(() -> HTTPRequest(server_url + "get_game_map?gameName=" + gameName));
        }
        catch(Exception e){
            System.out.println("Error while requesting map file. DAMN. the error is: " + e.getLocalizedMessage());
            return "ERROR.";
        }
    }
    public CompletableFuture<String> getPlayersPositions(String gameName){
        try{
            return CompletableFuture.supplyAsync(() ->HTTPRequest(server_url + "get_all_players_position?gameName=" + gameName));
        }
        catch(Exception e){
            System.out.println("Error while requesting player position. DAMN. the error is: " + e.getLocalizedMessage());
            CompletableFuture<String> errorReturn = new CompletableFuture<>();
            return errorReturn;
        }
    }
    public String joinGame(String gameName){
        try{
            return runMultiThreadedAndReturnString(() ->HTTPRequest(server_url + "join_game?gameName=" + gameName));
        }
        catch(Exception e){
            System.out.println("Error while joining game position. DAMN");
            return "ERROR.";
        }
    }
    public void quitGame(int userID, String gameName){
        HTTPRequest(server_url + "quit_game?userID=" + userID + "&gameName=" + gameName);
    }
    public void updatePlayerPosition(float posX, float posY, float posZ, int userID, String gameName){
        runMultiThreaded(() ->HTTPRequest(server_url + "update_player_position?posX=" + posX + "&posY=" + posY + "&posZ=" + posZ + "&playerID=" + userID + "&gameName=" + gameName));
    }

    public void runMultiThreaded(Runnable multiThreadedFunction){
        NETWORK_POOL.execute( () -> {
            multiThreadedFunction.run();
        });
    }

    public String runMultiThreadedAndReturnString(Callable<String> task) throws Exception {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        try {
            Future<String> future = executor.submit(task);
            return future.get();
        } finally {
            executor.shutdown();
        }
    }

    private String HTTPRequest(String url){
        try{
            URL connUrl = new URL(url);
            URLConnection connexion = connUrl.openConnection();
            InputStream is = connexion.getInputStream();
            // Source - https://stackoverflow.com/a/35446009// Posted by Slava Vedenin
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            for (int length; (length = is.read(buffer)) != -1; ) {
                result.write(buffer, 0, length);
            }
            is.close();
            return result.toString("UTF-8");
        } catch (IOException e) {
            throw new NetworkException(e);
        }
    }
}

