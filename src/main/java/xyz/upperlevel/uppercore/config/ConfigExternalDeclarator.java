package xyz.upperlevel.uppercore.config;

/**
 * This class is just a signaler, the implementing class must be extremely sure of what she's doing
 * that's to add compile-time checking for external config constructors.
 * <br>Any implementation of this interface must have at least one {@link ConfigConstructor} method
 */
public interface ConfigExternalDeclarator {
}
