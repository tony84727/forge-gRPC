package com.github.tony84727.forgegrpc.service;

import com.github.tony84727.proto.ChatGrpc;
import com.github.tony84727.proto.ChatOuterClass;
import io.grpc.stub.StreamObserver;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ConcurrentLinkedQueue;

@Mod.EventBusSubscriber
public class Chat extends ChatGrpc.ChatImplBase
{
	public interface Chatter {
		void sendMessage(String message);
	}

	private final Chatter chatter;
	private final ConcurrentLinkedQueue<StreamObserver<ChatOuterClass.Message>> observers = new ConcurrentLinkedQueue<>();

	private Chat(Logger logger,Chatter chatter) {
		this.chatter = chatter;
	}

	private static Chat instance;

	public static Chat getInstance(Logger logger,Chatter chatter) {
		if (instance == null) {
			instance = new Chat(logger,chatter);
		}
		return instance;
	}

	@Override
	public StreamObserver<ChatOuterClass.Message> connect(StreamObserver<ChatOuterClass.Message> responseObserver)
	{
		observers.add(responseObserver);
		return new StreamObserver<ChatOuterClass.Message>()
		{
			@Override
			public void onNext(ChatOuterClass.Message value)
			{
				chatter.sendMessage(String.format("<%s> %s",value.getSender(),value.getContent()));
			}

			@Override
			public void onError(Throwable t)
			{
				observers.remove(responseObserver);
			}

			@Override
			public void onCompleted()
			{
				observers.remove(responseObserver);
				responseObserver.onCompleted();
			}
		};
	}

	private void onChat(String sender, String message) {
		final ChatOuterClass.Message protoMessage = ChatOuterClass.Message.newBuilder().setSender(sender).setContent(message).build();
		observers.forEach(l -> l.onNext(protoMessage));
	}

	@SubscribeEvent
	public static void onServerChatEvent(ServerChatEvent event) {
		if (instance == null) {
			return;
		}
		instance.onChat(event.getUsername(), event.getMessage());
	}

	@SubscribeEvent
	public static void onClientChatEvent(ClientChatReceivedEvent event) {
		if (instance == null) {
			return;
		}

		instance.onChat("",event.getMessage().getFormattedText());
	}

}
