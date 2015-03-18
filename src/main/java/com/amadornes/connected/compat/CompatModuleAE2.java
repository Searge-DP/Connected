package com.amadornes.connected.compat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;
import appeng.api.parts.IFacadeContainer;
import appeng.api.parts.IFacadePart;
import appeng.facade.FacadePart;
import appeng.facade.IFacadeItem;
import appeng.tile.networking.TileCableBus;

import com.amadornes.connected.api.ConnectedApi;
import com.amadornes.connected.api.ConnectedApi.DefaultConnectedProvider;
import com.amadornes.connected.api.ConnectedCuboid;
import com.amadornes.connected.api.IConnected;
import com.amadornes.connected.api.IConnected.IConnectedAdvanced;
import com.amadornes.connected.util.AABBUtils;

import cpw.mods.fml.common.event.FMLInitializationEvent;

public class CompatModuleAE2 extends CompatModule {

    @Override
    public void init(FMLInitializationEvent ev) {

        ConnectedApi.registerBlockProvider(new ConnectedProviderAE2());

    }

    private static class ConnectedProviderAE2 extends DefaultConnectedProvider {

        @Override
        public IConnected findConnectable(IBlockAccess world, int x, int y, int z, int side) {

            TileEntity te = world.getTileEntity(x, y, z);
            if (te != null && te instanceof TileCableBus) {

                return new ConnectedAE2FacadeAdvanced(((TileCableBus) te).cb.getFacadeContainer());
            }

            return null;
        }
    }

    private static class ConnectedAE2Facade implements IConnected {

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

        protected IFacadeContainer tile;

        public ConnectedAE2Facade(IFacadeContainer tile) {

            this.tile = tile;
        }

        @Override
        public Collection<ConnectedCuboid> getCuboids(IBlockAccess world, int x, int y, int z, ForgeDirection side) {

            List<ConnectedCuboid> cuboids = new ArrayList<ConnectedCuboid>();

            // Loop through all the faces looking for facades
            for (ForgeDirection face : ForgeDirection.VALID_DIRECTIONS) {
                IFacadePart p = tile.getFacade(face);
                if (p != null && p instanceof FacadePart) {
                    FacadePart facade = (FacadePart) p;
                    Item item = facade.facade.getItem();
                    Block block = null;
                    if (item instanceof IFacadeItem) {
                        block = ((IFacadeItem) item).getBlock(facade.facade);
                    } else if (item instanceof ItemBlock) {
                        block = Block.getBlockFromItem(item);
                    } else if (item instanceof buildcraft.api.facades.IFacadeItem) {
                        block = ((buildcraft.api.facades.IFacadeItem) item).getBlocksForFacade(facade.facade)[0];
                    }
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

    private static class ConnectedAE2FacadeAdvanced extends ConnectedAE2Facade implements IConnectedAdvanced {

        public ConnectedAE2FacadeAdvanced(IFacadeContainer tile) {

            super(tile);
        }

        @Override
        public boolean canConnectTo(IBlockAccess world, int x, int y, int z, IBlockAccess world_, int x_, int y_, int z_,
                IConnected connected, ConnectedCuboid cuboid, ForgeDirection side) {

            return true;// ((IConnectedAdvanced) block).canConnectTo(world, x, y, z, world_, x_, y_, z_, connected, cuboid, side);
        }

    }

}
