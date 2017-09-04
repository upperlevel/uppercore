package xyz.upperlevel.uppercore.gui;

/*
 * MIT License
 * Copyright (c) 2017 upperlevel
 * Please see LICENSE.txt for the full license
 */
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@RequiredArgsConstructor
@Accessors(fluent = true)
@Getter
public enum GuiSize {
    SMALLEST(9), SMALL(9*2), NORMAL(9*3), BIG(9*4), BIGGER(9*5), DOUBLE(9*6);
    private final int size;

    /**
     * returns the minimum acceptable size for an inventory
     * @param needed the spaces needed in the inventory
     * @return the smallest inventory size that has at least the spaces passed as argument
     */
    public static int min(int needed) {
        return (int) (Math.ceil(needed / 9.0) * 9);
    }
}
