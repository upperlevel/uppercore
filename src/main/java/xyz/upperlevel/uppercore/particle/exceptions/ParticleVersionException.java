package xyz.upperlevel.uppercore.particle.exceptions;

/**
 * Represents a runtime exception that is thrown if the displayed particle effect requires a newer server version.
 */
public class ParticleVersionException extends RuntimeException {

    /**
     * Construct a new particle version exception.
     *
     * @param message message that will be logged
     */
    public ParticleVersionException(String message) {
        super(message);
    }
}
