package fr.chaos.engine.gameCode;

//yes i know this is a filthy workaround but i don't really give a f*ck bout the error eh.
public class NetworkException extends RuntimeException {
    public NetworkException(Throwable cause) {
        super(cause);
    }
}
