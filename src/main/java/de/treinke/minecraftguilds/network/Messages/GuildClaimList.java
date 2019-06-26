package de.treinke.minecraftguilds.network.Messages;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import de.treinke.minecraftguilds.GUI.GuildGUI;
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

public class GuildClaimList{
    public static final Logger LOGGER = LogManager.getLogger(MODID);

    // A default constructor is always required
    public GuildClaimList(){
    }


    public String data = "";

    public GuildClaimList(String toSend) {
        this.data = toSend;
    }

    public GuildClaimList(List<Claim> all_claims) {
        this.data = new Gson().toJson(all_claims,new TypeToken<List<Claim>>() {}.getType());
    }


    public void fromBytes(ByteBuf buf) {

        byte[] dst = new byte[buf.readableBytes()];

        buf.readBytes(dst);
        data = new String(dst);
    }

    public void toBytes(ByteBuf buf) {
        buf.writeBytes(data.getBytes());
    }


    public static void encode(GuildClaimList message, PacketBuffer packet)
    {
        message.toBytes(packet);
    }

    public static GuildClaimList decode(PacketBuffer packet)
    {
        GuildClaimList message = new GuildClaimList();
        message.fromBytes(packet);
        return message;
    }


    public static class Handler {
        public static void handle(GuildClaimList message, Supplier<NetworkEvent.Context> ctx)
        {
            Guild.all_claims = new Gson().fromJson(message.data, new TypeToken<List<Claim>>() {}.getType());
            Main.proxy.showClaimMessage();
            GuildGUI.refreshed = false;
        }
    }
}

