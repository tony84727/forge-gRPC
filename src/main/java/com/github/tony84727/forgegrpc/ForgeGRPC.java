package com.github.tony84727.forgegrpc;

import com.github.tony84727.forgegrpc.service.Chat;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

@Mod(modid=ForgeGRPC.MODID, version=ForgeGRPC.VERSION, name = ForgeGRPC.NAME)
public class ForgeGRPC {
    public final static String MODID = "forgegrpc";
    public final static String VERSION = "0.0.0";
    public final static String NAME = "Forge GRPC";
    private static Logger logger;
    private static Server server;
    private static Side serverSide;

    @Mod.EventHandler
    public static void preInit(FMLPreInitializationEvent e) {
    	logger = e.getModLog();
	}

    @Mod.EventHandler
    public static void onServerStart(FMLServerStartingEvent e) {
    	if (server != null) {
    		server.shutdown();
		}
    	serverSide = Side.SERVER;
    	final MinecraftServer mcServer = e.getServer();
		server = getServerBuilder().addService(Chat.getInstance(logger, (message) -> {
			final ITextComponent messageComponent = new TextComponentString(message);
			mcServer.getPlayerList().getPlayers().forEach((player) ->
				player.sendMessage(messageComponent)
			);
		})).build();
		try {
			server.start();
			logger.info("gRPC enabled");
		} catch (IOException exception) {
			logger.error("unable to start gRPC server");
			logger.error(exception);
		}
    }

    @Mod.EventHandler
	public static void onServerStopping(FMLServerStoppingEvent e) {
    	if (server != null) {
    		server.shutdown();
    		server = null;
		}
		logger.info("grpc server shutdown");
	}

    @SideOnly(Side.CLIENT)
    @Mod.EventHandler
    public static void onConnectToServer(FMLNetworkEvent.ClientConnectedToServerEvent e) {
    	if (server != null && serverSide != Side.SERVER) {
    		server.shutdown();
		}
    	serverSide = Side.CLIENT;
    	final EntityPlayer p = Minecraft.getMinecraft().player;
    	server = getServerBuilder().addService(Chat.getInstance(logger, (message) ->
			Minecraft.getMinecraft().player.sendChatMessage(message)
		)).build();
    	try {
    		server.start();
    		logger.info("gRPC enabled");
		} catch (IOException exception) {
			logger.error("unable to start gRPC server");
    		logger.error(exception);
		}
    }

    private static ServerBuilder getServerBuilder() {
    	return ServerBuilder.forPort(30000);
	}
}
