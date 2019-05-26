package com.github.tony84727.forgegrpc;

import com.github.tony84727.forgegrpc.service.Chat;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

import java.io.IOException;

@Mod(modid=ForgeGRPC.MODID, version=ForgeGRPC.VERSION, name = ForgeGRPC.NAME)
public class ForgeGRPC {
    public final static String MODID = "forgegrpc";
    public final static String VERSION = "0.0.0";
    public final static String NAME = "Forge GRPC";
    private static Thread serverThread;

    @Mod.EventHandler
    public static void preInit(FMLPreInitializationEvent e) {}

    @Mod.EventHandler
    public static void onServerStart(FMLServerStartingEvent e) {
        serverThread = new Thread(() -> {
            final Server server = ServerBuilder.forPort(30000).addService(Chat.getInstance(e.getServer())).build();
        	try {
                server.start();
                e.getServer().sendMessage(new TextComponentString("gRPC enabled"));
            } catch (IOException ioException) {
        	    ioException.printStackTrace();
            }
        });
        serverThread.run();
    }
}
