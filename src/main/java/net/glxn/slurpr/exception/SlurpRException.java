package net.glxn.slurpr.exception;

public class SlurpRException extends RuntimeException {
    public SlurpRException(String message) {
        super(message);
    }

    public SlurpRException(String message, Throwable t) {
        super(message, t);
    }
}
