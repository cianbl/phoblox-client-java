package fr.chaos.engine.gameCode;

public class Coroutine {
    private float timer = 0;
    private float inter = 0;
    private Runnable function;

    public Coroutine(Runnable func, float interval){
        inter = interval;
        function = func;
    }

    public void UpdateCoroutine(){
        if(timer >= inter){
            function.run();
            //System.out.println("Coroutine Ran");
            timer = 0;
        } else{
            timer += 1 * 0.016f;
        }
    }
}
