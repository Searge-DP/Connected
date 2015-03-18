package com.amadornes.connected;

import com.amadornes.connected.compat.CompatibilityUtils;
import com.amadornes.connected.ref.ModInfo;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = ModInfo.MODID, name = ModInfo.NAME, version = ModInfo.VERSION)
public class Connected {

    @Instance(ModInfo.MODID)
    public static Connected inst;

    @EventHandler
    public void preInit(FMLPreInitializationEvent ev) {

        CompatibilityUtils.preInit(ev);
    }

    @EventHandler
    public void init(FMLInitializationEvent ev) {

        CompatibilityUtils.init(ev);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent ev) {

        CompatibilityUtils.postInit(ev);
    }

}
