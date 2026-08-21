package fr.chaos.engine.gameCode.multithread;

import java.util.concurrent.Executor;

public class MInvoker implements Executor {
    @Override
    public void execute(Runnable r) {
        r.run();
    }
}