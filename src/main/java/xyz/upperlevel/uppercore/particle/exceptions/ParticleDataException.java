package xyz.upperlevel.uppercore.particle.exceptions;

/**
 * Represents a runtime exception that is thrown either if the displayed particle effect requires data and has none or vice-versa or if the data type is incorrect.
 */
public class ParticleDataException extends RuntimeException {

    /**
     * Construct a new particle data exception.
     *
     * @param message message that will be logged
     */
    public ParticleDataException(String message) {
        super(message);
    }
}
