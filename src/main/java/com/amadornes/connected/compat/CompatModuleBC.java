package com.amadornes.connected.compat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;
import buildcraft.api.transport.pluggable.PipePluggable;
import buildcraft.transport.FacadePluggable;
import buildcraft.transport.TileGenericPipe;
import buildcraft.transport.render.FacadeBlockAccess;

import com.amadornes.connected.api.ConnectedApi;
import com.amadornes.connected.api.ConnectedApi.DefaultConnectedProvider;
import com.amadornes.connected.api.ConnectedCuboid;
import com.amadornes.connected.api.IConnected;
import com.amadornes.connected.api.IConnected.IConnectedAdvanced;
import com.amadornes.connected.util.AABBUtils;

import cpw.mods.fml.common.event.FMLInitializationEvent;

public class CompatModuleBC extends CompatModule {

    @Override
    public void init(FMLInitializationEvent ev) {

        ConnectedApi.registerBlockProvider(new ConnectedProviderBC());

    }

    private static class ConnectedProviderBC extends DefaultConnectedProvider {

        @Override
        public IConnected findConnectable(IBlockAccess world, int x, int y, int z, int side) {

            if (world instanceof FacadeBlockAccess)
                return ConnectedApi.findConnectable(Minecraft.getMinecraft().theWorld, x, y, z, side);

            TileEntity te = world.getTileEntity(x, y, z);
            if (te != null && te instanceof TileGenericPipe)
                return new ConnectedBCFacadeAdvanced((TileGenericPipe) te);

            return null;
        }
    }

    private static class ConnectedBCFacade implements IConnected {

        private static final double thickness = 2 / 16D;

        private static final AxisAlignedBB[] boxes = new AxisAlignedBB[] {//
        //
                AxisAlignedBB.getBoundingBox(0, 0, 0, 1, thickness, 1), //
                AxisAlignedBB.getBoundingBox(0, 1 - thickness, 0, 1, 1, 1), //
                AxisAlignedBB.getBoundingBox(0, 0, 0, 1, 1, thickness), //
                AxisAlignedBB.getBoundingBox(0, 0, 1 - thickness, 1, 1, 1), //
                AxisAlignedBB.getBoundingBox(0, 0, 0, thickness, 1, 1), //
                AxisAlignedBB.getBoundingBox(1 - thickness, 0, 0, 1, 1, 1) //
        };

        protected TileGenericPipe tile;

        public ConnectedBCFacade(TileGenericPipe tile) {

            this.tile = tile;
        }

        @Override
        public Collection<ConnectedCuboid> getCuboids(IBlockAccess world, int x, int y, int z, ForgeDirection side) {

            List<ConnectedCuboid> cuboids = new ArrayList<ConnectedCuboid>();

            // Loop through all the faces looking for facades
            for (ForgeDirection face : ForgeDirection.VALID_DIRECTIONS) {
                PipePluggable pp = tile.getPipePluggable(face);
                if (pp != null && pp instanceof FacadePluggable) {
                    FacadePluggable facade = (FacadePluggable) pp;
                    Block block = facade.getRenderingBlock();
                    if (block != null && block instanceof IConnected) {

                        // Get all the cuboids for that face
                        for (ConnectedCuboid c : ((IConnected) block).getCuboids(world, x, y, z, side)) {
                            // Clip them to the facade's size
                            AxisAlignedBB clipped = clipBox(c.getAABB(), face);
                            // If they're valid cuboids, add them to the list
                            if (clipped != null)
                                cuboids.add(new ConnectedCuboid(clipped, c.getTexture()));
                        }
                    }
                }
            }

            return cuboids;
        }

        private AxisAlignedBB clipBox(AxisAlignedBB aabb, ForgeDirection side) {

            AxisAlignedBB sideBox = boxes[side.ordinal()];
            if (aabb.intersectsWith(sideBox)) {
                AxisAlignedBB box = AABBUtils.clip(aabb, sideBox);
                if (box == null)
                    return null;
                return box;
            }

            return null;
        }

    }

    private static class ConnectedBCFacadeAdvanced extends ConnectedBCFacade implements IConnectedAdvanced {

        public ConnectedBCFacadeAdvanced(TileGenericPipe tile) {

            super(tile);
        }

        @Override
        public boolean canConnectTo(IBlockAccess world, int x, int y, int z, IBlockAccess world_, int x_, int y_, int z_,
                IConnected connected, ConnectedCuboid cuboid, ForgeDirection side) {

            return true;// ((IConnectedAdvanced) block).canConnectTo(world, x, y, z, world_, x_, y_, z_, connected, cuboid, side);
        }

    }

}
