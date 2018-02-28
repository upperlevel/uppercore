package xyz.upperlevel.uppercore.particle.exceptions;

/**
 * Represents a runtime exception that is thrown either if the displayed particle effect is not colorable or if the particle color type is incorrect.
 */
public class ParticleColorException extends RuntimeException {

    /**
     * Construct a new particle color exception.
     *
     * @param message message that will be logged
     */
    public ParticleColorException(String message) {
        super(message);
    }
}
