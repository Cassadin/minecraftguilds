package de.treinke.minecraftguilds.network.Messages;

import de.treinke.minecraftguilds.Main;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Supplier;

import static de.treinke.minecraftguilds.Main.MODID;

public class GuildKill {
    public static final Logger LOGGER = LogManager.getLogger(MODID);

    // A default constructor is always required
    public GuildKill(){
    }


    public String data = "";

    public GuildKill(String toSend) {
        this.data = toSend;
    }


    public void fromBytes(ByteBuf buf) {

        byte[] dst = new byte[buf.readableBytes()];

        buf.readBytes(dst);
        data = new String(dst);
    }

    public void toBytes(ByteBuf buf) {
        buf.writeBytes(data.getBytes());
    }


    public static void encode(GuildKill message, PacketBuffer packet)
    {
        message.toBytes(packet);
    }

    public static GuildKill decode(PacketBuffer packet)
    {
        GuildKill message = new GuildKill();
        message.fromBytes(packet);
        return message;
    }


    public static class Handler {
        public static void handle(GuildKill message, Supplier<NetworkEvent.Context> ctx)
        {
            ServerPlayerEntity player = ctx.get().getSender();

            Main.proxy.killplayer(player);
        }
    }
}
