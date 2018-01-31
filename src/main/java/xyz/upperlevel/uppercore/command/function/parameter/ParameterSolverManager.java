package xyz.upperlevel.uppercore.command.function.parameter;

import java.util.HashMap;
import java.util.Map;

public class ParameterSolverManager {
    private Map<Class<?>, ParameterAdapter> solvers = new HashMap<>();

    public void addSolver(ParameterAdapter solver) {
        for (Class<?> type : solver.getTypes()) {
            solvers.put(type, solver);
        }
    }

    public ParameterAdapter getSolver(Class<?> type) {
        return solvers.get(type);
    }
}
