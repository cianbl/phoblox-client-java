//Default Scene

package fr.chaos.engine.core;

import fr.chaos.engine.gameCode.Main;

public class Renderer{
    public static void init(){    
        Main.init();
    }
    
    public static void render(){
        Main.render();
    }

    public static void update(long window){
        Main.update(window);
    }

    public static void onQuit(){
        Main.onQuit();
    }

}
