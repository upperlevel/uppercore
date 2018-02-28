package xyz.upperlevel.uppercore.particle.exceptions;

/**
 * Represents a runtime exception that is thrown if packet instantiation fails.
 */
public class PacketInstantiationException extends RuntimeException {
    /**
     * Construct a new packet instantiation exception.
     *
     * @param message message that will be logged
     * @param cause cause of the exception
     */
    public PacketInstantiationException(String message, Throwable cause) {
        super(message, cause);
    }
}