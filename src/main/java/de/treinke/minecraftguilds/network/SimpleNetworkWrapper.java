package de.treinke.minecraftguilds.network;

import de.treinke.minecraftguilds.Main;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import org.lwjgl.system.windows.MSG;

import java.nio.ByteBuffer;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class SimpleNetworkWrapper {

    private static final String PROTOCOL_VERSION = Integer.toString(1);
    private int ID = 0;
    private static SimpleChannel net = null;

    public SimpleNetworkWrapper(String modid) {
        net = NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation(modid, "main_channel"))
                .clientAcceptedVersions(PROTOCOL_VERSION::equals)
                .serverAcceptedVersions(PROTOCOL_VERSION::equals)
                .networkProtocolVersion(() -> PROTOCOL_VERSION)
                .simpleChannel();
        registerPackets();
    }


    public SimpleChannel getSimpleChannel()
    {
        return net;
    }

    public void registerPackets()
    {
        net.registerMessage(ID++, NetworkMessage.class, NetworkMessage::encode, NetworkMessage::decode, NetworkMessage.Handler::handle);
    }


    public <MSG> void sendTo(MSG msg, ServerPlayerEntity player)
    {
        if (!(player instanceof FakePlayer))
        {
            net.sendTo(msg, player.connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
        }
    }

    public <MSG> void sendToServer(MSG msg) {
        net.sendToServer(msg);
    }
}
