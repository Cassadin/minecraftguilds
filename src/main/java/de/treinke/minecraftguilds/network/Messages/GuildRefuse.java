package de.treinke.minecraftguilds.network.Messages;

import de.treinke.minecraftguilds.*;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Supplier;

import static de.treinke.minecraftguilds.Main.MODID;

public class GuildRefuse{
    public static final Logger LOGGER = LogManager.getLogger(MODID);

    // A default constructor is always required
    public GuildRefuse(){
    }


    public String data = "";

    public GuildRefuse(String toSend) {
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


    public static void encode(GuildRefuse message, PacketBuffer packet)
    {
        message.toBytes(packet);
    }

    public static GuildRefuse decode(PacketBuffer packet)
    {
        GuildRefuse message = new GuildRefuse();
        message.fromBytes(packet);
        return message;
    }


    public static class Handler {
        public static void handle(GuildRefuse message, Supplier<NetworkEvent.Context> ctx)
        {
            Main.proxy.refuseGuild(message.data,ctx.get().getSender().getName().getString());
        }
    }
}
