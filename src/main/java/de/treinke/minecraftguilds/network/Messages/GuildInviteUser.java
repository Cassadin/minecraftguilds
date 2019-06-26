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

public class GuildInviteUser{
    public static final Logger LOGGER = LogManager.getLogger(MODID);

    // A default constructor is always required
    public GuildInviteUser(){
    }


    public String data = "";

    public GuildInviteUser(String toSend) {
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


    public static void encode(GuildInviteUser message, PacketBuffer packet)
    {
        message.toBytes(packet);
    }

    public static GuildInviteUser decode(PacketBuffer packet)
    {
        GuildInviteUser message = new GuildInviteUser();
        message.fromBytes(packet);
        return message;
    }


    public static class Handler {
        public static void handle(GuildInviteUser message, Supplier<NetworkEvent.Context> ctx)
        {
            String[] inv = new Gson().fromJson(message.data, new TypeToken<String[]>() {}.getType());
            Main.proxy.inviteUser(inv[0],inv[1]);
        }
    }
}

