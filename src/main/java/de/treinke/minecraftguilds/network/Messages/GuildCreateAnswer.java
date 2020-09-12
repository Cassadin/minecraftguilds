package de.treinke.minecraftguilds.network.Messages;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import de.treinke.minecraftguilds.GUI.GuildGUI;
import de.treinke.minecraftguilds.Main;
import de.treinke.minecraftguilds.objects.Guild;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Supplier;

import static de.treinke.minecraftguilds.Main.MODID;

public class GuildCreateAnswer{
    public static final Logger LOGGER = LogManager.getLogger(MODID);

    // A default constructor is always required
    public GuildCreateAnswer(){
    }


    public String data = "";

    public GuildCreateAnswer(String toSend) {
        if(this.data != null)
            this.data = toSend;
    }


    public void fromBytes(ByteBuf buf) {

        byte[] dst = new byte[buf.readableBytes()];

        buf.readBytes(dst);
        data = new String(dst);
    }

    public void toBytes(ByteBuf buf) {
        if(data != null)
            buf.writeBytes(data.getBytes());
    }


    public static void encode(GuildCreateAnswer message, PacketBuffer packet)
    {
        message.toBytes(packet);
    }

    public static GuildCreateAnswer decode(PacketBuffer packet)
    {
        GuildCreateAnswer message = new GuildCreateAnswer();
        message.fromBytes(packet);
        return message;
    }


    public static class Handler {
        public static void handle(GuildCreateAnswer message, Supplier<NetworkEvent.Context> ctx)
        {
            if(message.data.length() > 0 && message.data != null)
            {
                Guild.MyGuild = new Gson().fromJson(message.data,new TypeToken<Guild>() {}.getType());
                GuildGUI.refreshed = false;
                Main.proxy.open_gui();
            }else {
                Guild.MyGuild = null;
            }
        }
    }
}

