package de.treinke.minecraftguilds.network.Messages;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import de.treinke.minecraftguilds.*;
import de.treinke.minecraftguilds.GUI.GuildGUI;
import de.treinke.minecraftguilds.objects.Guild;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.function.Supplier;

import static de.treinke.minecraftguilds.Main.MODID;

public class GuildInvitesAnswer{
    public static final Logger LOGGER = LogManager.getLogger(MODID);

    // A default constructor is always required
    public GuildInvitesAnswer(){
    }


    public String data = "";

    public GuildInvitesAnswer(String toSend) {
        this.data = toSend;
    }

    public GuildInvitesAnswer(List<String> invs) {
        this.data = new Gson().toJson(invs, new TypeToken<List<String>>() {}.getType());
    }


    public void fromBytes(ByteBuf buf) {

        byte[] dst = new byte[buf.readableBytes()];

        buf.readBytes(dst);
        data = new String(dst);
    }

    public void toBytes(ByteBuf buf) {
        buf.writeBytes(data.getBytes());
    }


    public static void encode(GuildInvitesAnswer message, PacketBuffer packet)
    {
        message.toBytes(packet);
    }

    public static GuildInvitesAnswer decode(PacketBuffer packet)
    {
        GuildInvitesAnswer message = new GuildInvitesAnswer();
        message.fromBytes(packet);
        return message;
    }


    public static class Handler {
        public static void handle(GuildInvitesAnswer message, Supplier<NetworkEvent.Context> ctx)
        {
            List<String> invs = new Gson().fromJson(message.data, new TypeToken<List<String>>() {}.getType());
            if(!invs.equals(Guild.single_user_invites)) {
                Guild.single_user_invites = invs;
                GuildGUI.refreshed = false;
            }
        }
    }
}
