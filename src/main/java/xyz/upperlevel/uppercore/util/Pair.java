package xyz.upperlevel.uppercore.util;

import lombok.Data;

@Data
public class Pair<A, B> {
    private final A first;
    private final B second;

    public static <A, B> Pair<A, B> of(A first, B second) {
        return new Pair<>(first, second);
    }
}
