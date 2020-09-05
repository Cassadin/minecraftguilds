package de.treinke.minecraftguilds.network.Messages;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import de.treinke.minecraftguilds.GUI.GuildGUI;
import de.treinke.minecraftguilds.Items.GuildItems;
import de.treinke.minecraftguilds.Main;
import de.treinke.minecraftguilds.objects.Claim;
import de.treinke.minecraftguilds.objects.Guild;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.function.Supplier;

import static de.treinke.minecraftguilds.Main.MODID;

public class GuildClaim{
    public static final Logger LOGGER = LogManager.getLogger(MODID);

    // A default constructor is always required
    public GuildClaim(){
    }


    public String data = "";

    public GuildClaim(String toSend) {
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


    public static void encode(GuildClaim message, PacketBuffer packet)
    {
        message.toBytes(packet);
    }

    public static GuildClaim decode(PacketBuffer packet)
    {
        GuildClaim message = new GuildClaim();
        message.fromBytes(packet);
        return message;
    }


    public static class Handler {
        public static void handle(GuildClaim message, Supplier<NetworkEvent.Context> ctx)
        {
            final int x = (ctx.get().getSender().getPosition().getX())/16+(ctx.get().getSender().getPosition().getX()<0?-1:0);
            final int z = (ctx.get().getSender().getPosition().getZ())/16+(ctx.get().getSender().getPosition().getZ()<0?-1:0);

            Main.proxy.addClaim(message.data,ctx.get().getSender().getEntity().getEntityWorld().func_234923_W_().toString(),x,z);
        }
    }
}

