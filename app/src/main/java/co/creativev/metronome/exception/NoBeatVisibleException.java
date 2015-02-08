package co.creativev.metronome.exception;

public class NoBeatVisibleException extends RuntimeException {
    public NoBeatVisibleException() {
        super("No beats are visible");
    }
}
