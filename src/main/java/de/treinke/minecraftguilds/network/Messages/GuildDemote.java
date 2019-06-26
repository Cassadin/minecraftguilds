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

public class GuildDemote{
    public static final Logger LOGGER = LogManager.getLogger(MODID);

    // A default constructor is always required
    public GuildDemote(){
    }


    public String data = "";

    public GuildDemote(String toSend) {
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


    public static void encode(GuildDemote message, PacketBuffer packet)
    {
        message.toBytes(packet);
    }

    public static GuildDemote decode(PacketBuffer packet)
    {
        GuildDemote message = new GuildDemote();
        message.fromBytes(packet);
        return message;
    }


    public static class Handler {
        public static void handle(GuildDemote message, Supplier<NetworkEvent.Context> ctx)
        {
            Main.proxy.demoteGuild(message.data);
        }
    }
}

