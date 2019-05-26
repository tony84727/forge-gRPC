package com.github.tony84727.forgegrpc;

import com.github.tony84727.forgegrpc.service.Chat;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

/*
Manage lifecycle of grpc server by forge's event.
 */
public class ServerController
{
	private Server server;
	private Side side;
	private Logger logger;

	public ServerController(Logger logger) {
		this.logger = logger;
	}

	public void startServerSideGRPC(MinecraftServer mcServer) {
		side = Side.SERVER;
		server = getServerBuilder().addService(Chat.getInstance(logger, (message) -> {
			final ITextComponent messageComponent = new TextComponentString(message);
			mcServer.getPlayerList().getPlayers().forEach((player) ->
				player.sendMessage(messageComponent)
			);
		})).build();
		startServer();
	}

	public void stopCurrentServer() {
		if (server == null) {
			return;
		}
		server.shutdown();
    	server = null;
		logger.info("grpc server shutdown");
	}

	private void startServer() {
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

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onClientConnectToServer(FMLNetworkEvent.ClientConnectedToServerEvent event) {
		if (server != null && side != Side.SERVER) {
    		server.shutdown();
		}
    	side = Side.CLIENT;
    	server = getServerBuilder().addService(Chat.getInstance(logger, (message) -> {
    		final EntityPlayerSP player = FMLClientHandler.instance().getClientPlayerEntity();
    		if (player == null) {
    			return;
			}
    		player.sendChatMessage(message);
		}
		)).build();
    	startServer();
	}
}
