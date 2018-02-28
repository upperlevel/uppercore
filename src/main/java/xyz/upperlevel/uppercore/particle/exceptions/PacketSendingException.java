package xyz.upperlevel.uppercore.particle.exceptions;

/**
 * Represents a runtime exception that is thrown if packet sending fails.
 */
public class PacketSendingException extends RuntimeException {
    /**
     * Construct a new packet sending exception.
     *
     * @param message message that will be logged
     * @param cause cause of the exception
     */
    public PacketSendingException(String message, Throwable cause) {
        super(message, cause);
    }
}
