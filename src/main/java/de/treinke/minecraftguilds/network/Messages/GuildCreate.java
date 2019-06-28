package de.treinke.minecraftguilds.network.Messages;

        import com.google.gson.Gson;
        import com.google.gson.reflect.TypeToken;
        import de.treinke.minecraftguilds.*;
        import de.treinke.minecraftguilds.objects.Guild;
        import io.netty.buffer.ByteBuf;
        import net.minecraft.entity.player.ServerPlayerEntity;
        import net.minecraft.network.PacketBuffer;
        import net.minecraftforge.fml.network.NetworkEvent;
        import org.apache.logging.log4j.LogManager;
        import org.apache.logging.log4j.Logger;
        import org.apache.logging.log4j.core.jmx.Server;

        import java.util.function.Supplier;

        import static de.treinke.minecraftguilds.Main.MODID;

public class GuildCreate{
    public static final Logger LOGGER = LogManager.getLogger(MODID);

    // A default constructor is always required
    public GuildCreate(){
    }


    public String data = "";

    public GuildCreate(String toSend) {
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


    public static void encode(GuildCreate message, PacketBuffer packet)
    {
        message.toBytes(packet);
    }

    public static GuildCreate decode(PacketBuffer packet)
    {
        GuildCreate message = new GuildCreate();
        message.fromBytes(packet);
        return message;
    }


    public static class Handler {
        public static void handle(GuildCreate message, Supplier<NetworkEvent.Context> ctx)
        {
            ctx.get().setPacketHandled(true);
            ServerPlayerEntity player = ctx.get().getSender();

            Guild.list.add(new Guild(message.data,player.getName().getString()));

            Main.proxy.addClaim(message.data,player.dimension.getId(),(player.getPosition().getX())/16+(player.getPosition().getX()<0?-1:0),(player.getPosition().getZ())/16+(player.getPosition().getZ()<0?-1:0));

            Main.NETWORK.sendTo(new GuildCheckAnswer(Main.proxy.getPlayerGuild(player.getName().getString())),player);

        }
    }
}
