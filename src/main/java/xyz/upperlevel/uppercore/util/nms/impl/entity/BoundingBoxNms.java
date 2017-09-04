package xyz.upperlevel.uppercore.util.nms.impl.entity;

/*
 * MIT License
 * Copyright (c) 2017 upperlevel
 * Please see LICENSE.txt for the full license
 */
import xyz.upperlevel.uppercore.util.nms.NmsPacket;
import xyz.upperlevel.uppercore.util.nms.NmsUtil;
import xyz.upperlevel.uppercore.util.nms.exceptions.UnsupportedVersionException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import static xyz.upperlevel.uppercore.util.nms.NmsUtil.handleException;

public final class BoundingBoxNms {
    public static final Class<?> NMS_CLASS;
    private static final Method intersectsAabb;
    private static final Constructor<?> constructor;

    static {
        try {
            NMS_CLASS = NmsPacket.NMS.getClass("AxisAlignedBB");

            intersectsAabb = NmsUtil.getMethod(NMS_CLASS, Boolean.TYPE, NMS_CLASS);

            constructor = NMS_CLASS.getConstructor(Double.TYPE, Double.TYPE, Double.TYPE, Double.TYPE, Double.TYPE, Double.TYPE);
        } catch (Exception e) {
            throw new UnsupportedVersionException(e);
        }
    }

    public static boolean intersect(Object a, Object b) {
        try {
            return (boolean) intersectsAabb.invoke(a, b);
        } catch (Exception e) {
            handleException(e);
            return false;
        }
    }

    public static Object toNms(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        try {
            return constructor.newInstance(minX, minY, minZ, maxX, maxY, maxZ);
        } catch (Exception e) {
            handleException(e);
            return false;
        }
    }


    private BoundingBoxNms() {
    }
}
