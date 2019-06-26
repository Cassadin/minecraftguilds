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

public class GuildDonation{
    public static final Logger LOGGER = LogManager.getLogger(MODID);

    // A default constructor is always required
    public GuildDonation(){
    }


    public String data = "";

    public GuildDonation(String toSend) {
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


    public static void encode(GuildDonation message, PacketBuffer packet)
    {
        message.toBytes(packet);
    }

    public static GuildDonation decode(PacketBuffer packet)
    {
        GuildDonation message = new GuildDonation();
        message.fromBytes(packet);
        return message;
    }


    public static class Handler {
        public static void handle(GuildDonation message, Supplier<NetworkEvent.Context> ctx)
        {
            ctx.get().enqueueWork(new Runnable() {
                @Override
                public void run() {
                    int amount = ctx.get().getSender().inventory.clearMatchingItems(p -> p.getItem() == GuildItems.COPPER_COIN,-1);
                    amount += ctx.get().getSender().inventory.clearMatchingItems(p -> p.getItem() == GuildItems.SILVER_COID,-1)*10;
                    amount += ctx.get().getSender().inventory.clearMatchingItems(p -> p.getItem() == GuildItems.GOLD_COIN,-1)*100;
                    Main.proxy.playerdonateGuild(ctx.get().getSender().getName().getString(),amount);
                }
            });
        }
    }
}

