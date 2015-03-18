package com.amadornes.connected.util;

import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.ForgeDirection;

public class AABBUtils {

    public static AxisAlignedBB clip(AxisAlignedBB a, AxisAlignedBB b) {

        AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(//
                Math.max(a.minX, b.minX), //
                Math.max(a.minY, b.minY), //
                Math.max(a.minZ, b.minZ),//
                Math.min(a.maxX, b.maxX), //
                Math.min(a.maxY, b.maxY), //
                Math.min(a.maxZ, b.maxZ)//
                );
        if (aabb.intersectsWith(a) && aabb.intersectsWith(b))
            return aabb;
        return null;
    }

    public static double getSide(AxisAlignedBB box, ForgeDirection side) {

        if (side == ForgeDirection.DOWN)
            return box.minY;
        if (side == ForgeDirection.UP)
            return box.maxY;
        if (side == ForgeDirection.NORTH)
            return box.minZ;
        if (side == ForgeDirection.SOUTH)
            return box.maxZ;
        if (side == ForgeDirection.WEST)
            return box.minX;
        if (side == ForgeDirection.EAST)
            return box.maxX;

        return 0;
    }

}
