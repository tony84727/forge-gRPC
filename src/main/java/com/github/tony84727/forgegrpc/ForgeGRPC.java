package com.github.tony84727.forgegrpc;

import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

@Mod(modid=ForgeGRPC.MODID, version=ForgeGRPC.VERSION, name = ForgeGRPC.NAME)
public class ForgeGRPC {
    public final static String MODID = "forgegrpc";
    public final static String VERSION = "0.0.0";
    public final static String NAME = "Forge GRPC";

    @Mod.EventHandler
    public static void onServerStart(FMLServerStartingEvent e) {
        e.getServer().sendMessage(new TextComponentString("gRPC enabled"));
    }
}
