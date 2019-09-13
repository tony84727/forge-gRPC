package com.github.tony84727.forgegrpc.service;

import com.github.tony84727.proto.ChatGrpc;
import com.github.tony84727.proto.ChatOuterClass;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.ConcurrentLinkedQueue;

@Mod.EventBusSubscriber
public class Chat extends ChatGrpc.ChatImplBase
{
	private final MinecraftServer server;
	private final ConcurrentLinkedQueue<StreamObserver<ChatOuterClass.ChatEvent>> observers = new ConcurrentLinkedQueue<>();
	public Chat(MinecraftServer server) {
		this.server = server;
	}

	@Override
	public StreamObserver<ChatOuterClass.Message> connect(StreamObserver<ChatOuterClass.ChatEvent> responseObserver)
	{
		observers.add(responseObserver);
		return new StreamObserver<ChatOuterClass.Message>()
		{
			@Override
			public void onNext(ChatOuterClass.Message value)
			{
				server.sendMessage(new TextComponentString(value.getContent()));
			}

			@Override
			public void onError(Throwable t)
			{
			}

			@Override
			public void onCompleted()
			{
				observers.remove(responseObserver);
				responseObserver.onCompleted();
			}
		};
	}

	public void close() {
		observers.forEach(l -> l.onCompleted());
		observers.clear();
	}

	@SubscribeEvent
	public void onChat(ServerChatEvent event) {
		final ChatOuterClass.ChatEvent chat = ChatOuterClass.ChatEvent.newBuilder().setSender(event.getUsername()).setContent(event.getMessage()).build();
		observers.forEach(l -> l.onNext(chat));
	}

}
