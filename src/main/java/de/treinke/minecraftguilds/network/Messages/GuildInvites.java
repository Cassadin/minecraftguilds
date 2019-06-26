package de.treinke.minecraftguilds.network.Messages;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import de.treinke.minecraftguilds.*;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.function.Supplier;

import static de.treinke.minecraftguilds.Main.MODID;

public class GuildInvites{
    public static final Logger LOGGER = LogManager.getLogger(MODID);

    // A default constructor is always required
    public GuildInvites(){
    }


    public String data = "";

    public GuildInvites(String toSend) {
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


    public static void encode(GuildInvites message, PacketBuffer packet)
    {
        message.toBytes(packet);
    }

    public static GuildInvites decode(PacketBuffer packet)
    {
        GuildInvites message = new GuildInvites();
        message.fromBytes(packet);
        return message;
    }


    public static class Handler {
        public static void handle(GuildInvites message, Supplier<NetworkEvent.Context> ctx)
        {
            Main.NETWORK.sendTo(new GuildInvitesAnswer(new Gson().toJson(Main.proxy.getInvites(ctx.get().getSender().getName().getString()), new TypeToken<List<String>>() {}.getType())),ctx.get().getSender());
        }
    }
}
