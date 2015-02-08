package co.creativev.metronome.exception;

public class InvisibleBeatPlayedException extends RuntimeException {
    public InvisibleBeatPlayedException() {
        super("Invisible beat played");
    }
}
