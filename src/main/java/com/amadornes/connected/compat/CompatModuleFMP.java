package com.amadornes.connected.compat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;
import codechicken.microblock.CommonMicroblock;
import codechicken.microblock.FaceMicroblock;
import codechicken.microblock.HollowMicroblock;
import codechicken.multipart.TMultiPart;
import codechicken.multipart.TileMultipart;

import com.amadornes.connected.api.ConnectedApi;
import com.amadornes.connected.api.ConnectedApi.DefaultConnectedProvider;
import com.amadornes.connected.api.ConnectedCuboid;
import com.amadornes.connected.api.IConnected;
import com.amadornes.connected.api.IConnected.IConnectedAdvanced;
import com.amadornes.connected.test.MicroMaterialConnected;
import com.amadornes.connected.util.AABBUtils;

import cpw.mods.fml.common.event.FMLInitializationEvent;

public class CompatModuleFMP extends CompatModule {

    @Override
    public void init(FMLInitializationEvent ev) {

        ConnectedApi.registerBlockProvider(new ConnectedProviderFMP());

    }

    private static class ConnectedProviderFMP extends DefaultConnectedProvider {

        @Override
        public IConnected findConnectable(IBlockAccess world, int x, int y, int z, int side) {

            TileEntity te = world.getTileEntity(x, y, z);
            if (te != null && te instanceof TileMultipart)
                return new ConnectedFMPCover((TileMultipart) te);

            return null;
        }
    }

    private static class ConnectedFMPCover implements IConnected {

        private static final AxisAlignedBB[][] boxes = new AxisAlignedBB[6][8];
        static {
            for (int i = 0; i < 8; i++) {
                double thickness = i / 8D;
                boxes[0][i] = AxisAlignedBB.getBoundingBox(0, 0, 0, 1, thickness, 1);
                boxes[1][i] = AxisAlignedBB.getBoundingBox(0, 1 - thickness, 0, 1, 1, 1);
                boxes[2][i] = AxisAlignedBB.getBoundingBox(0, 0, 0, 1, 1, thickness);
                boxes[3][i] = AxisAlignedBB.getBoundingBox(0, 0, 1 - thickness, 1, 1, 1);
                boxes[4][i] = AxisAlignedBB.getBoundingBox(0, 0, 0, thickness, 1, 1);
                boxes[5][i] = AxisAlignedBB.getBoundingBox(1 - thickness, 0, 0, 1, 1, 1);
            }
        }

        protected TileMultipart tile;

        public ConnectedFMPCover(TileMultipart tile) {

            this.tile = tile;
        }

        @Override
        public Collection<ConnectedCuboid> getCuboids(IBlockAccess world, int x, int y, int z, ForgeDirection side) {

            List<ConnectedCuboid> cuboids = new ArrayList<ConnectedCuboid>();

            // Loop through all the multiparts looking for face microblocks
            for (TMultiPart p : tile.jPartList()) {
                if (p instanceof FaceMicroblock || p instanceof HollowMicroblock) {
                    CommonMicroblock mb = (CommonMicroblock) p;
                    if (mb.getIMaterial() instanceof MicroMaterialConnected) {
                        MicroMaterialConnected mat = (MicroMaterialConnected) mb.getIMaterial();
                        // Get all the cuboids for that face
                        for (ConnectedCuboid c : ((IConnected) mat.block()).getCuboids(world, x, y, z, side)) {
                            // Clip them to the facade's size
                            AxisAlignedBB clipped = clipBox(c.getAABB(), ForgeDirection.getOrientation(mb.getShape()), mb.getSize());
                            // If they're valid cuboids, add them to the list
                            if (clipped != null)
                                cuboids.add(new ConnectedCuboid(clipped, c.getTexture()));
                        }
                    }
                }
            }

            return cuboids;
        }

        private AxisAlignedBB clipBox(AxisAlignedBB aabb, ForgeDirection side, int thickness) {

            thickness = Math.min(thickness, 7);

            AxisAlignedBB sideBox = boxes[side.ordinal()][thickness];
            if (aabb.intersectsWith(sideBox)) {
                AxisAlignedBB box = AABBUtils.clip(aabb, sideBox);
                if (box == null)
                    return null;
                return box;
            }

            return null;
        }

    }

    private static class ConnectedBCFacadeAdvanced extends ConnectedFMPCover implements IConnectedAdvanced {

        public ConnectedBCFacadeAdvanced(TileMultipart tile) {

            super(tile);
        }

        @Override
        public boolean canConnectTo(IBlockAccess world, int x, int y, int z, IBlockAccess world_, int x_, int y_, int z_,
                IConnected connected, ConnectedCuboid cuboid, ForgeDirection side) {

            return true;// ((IConnectedAdvanced) block).canConnectTo(world, x, y, z, world_, x_, y_, z_, connected, cuboid, side);
        }

    }

}
