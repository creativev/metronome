package me.creativei.metronome.exception;

public class InvisibleBeatPlayedException extends RuntimeException {
    public InvisibleBeatPlayedException() {
        super("Invisible beat played");
    }
}
