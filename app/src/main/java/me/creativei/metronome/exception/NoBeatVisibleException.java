package me.creativei.metronome.exception;

public class NoBeatVisibleException extends RuntimeException {
    public NoBeatVisibleException() {
        super("No beats are visible");
    }
}
