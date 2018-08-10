package xyz.upperlevel.uppercore.registry;

public interface RegistryVisitor {
    VisitResult preVisitRegistry(Registry<?> registry);

    VisitResult visitEntry(String name, Object value);

    VisitResult postVisitRegistry(Registry<?> registry);

    enum VisitResult {
        CONTINUE, SKIP, TERMINATE;
    }
}
