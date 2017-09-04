package xyz.upperlevel.uppercore.particle.exceptions;

/*
 * MIT License
 * Copyright (c) 2017 upperlevel
 * Please see LICENSE.txt for the full license
 */

/**
 * Represents a runtime exception that is thrown if the displayed particle effect requires a newer version
 * <p>
 * This class is part of the <b>ParticleEffect Library</b> and follows the same usage conditions
 *
 * @author DarkBlade12
 * @since 1.6
 */
public class ParticleVersionException extends RuntimeException {

    /**
     * Construct a new particle version exception
     *
     * @param message Message that will be logged
     */
    public ParticleVersionException(String message) {
        super(message);
    }
}
