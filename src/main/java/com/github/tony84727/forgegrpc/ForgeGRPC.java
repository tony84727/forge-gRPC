package com.github.tony84727.forgegrpc;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import org.apache.logging.log4j.Logger;

import static net.minecraftforge.common.MinecraftForge.EVENT_BUS;

@Mod(modid=ForgeGRPC.MODID, version=ForgeGRPC.VERSION, name = ForgeGRPC.NAME)
public class ForgeGRPC {
    public final static String MODID = "forgegrpc";
    public final static String VERSION = "0.0.0";
    public final static String NAME = "Forge GRPC";
    private static Logger logger;
    private static ServerController serverController;

    @Mod.EventHandler
    public static void preInit(FMLPreInitializationEvent e) {
    	logger = e.getModLog();
    	serverController = new ServerController(logger);
    	EVENT_BUS.register(serverController);
	}

    @Mod.EventHandler
    public static void onServerStart(FMLServerStartingEvent e) {
    	serverController.startServerSideGRPC(e.getServer());
    }

    @Mod.EventHandler
	public static void onServerStopping(FMLServerStoppingEvent e) {
    	serverController.stopCurrentServer();
	}
}
