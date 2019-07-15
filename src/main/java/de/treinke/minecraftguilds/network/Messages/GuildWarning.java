package de.treinke.minecraftguilds.network.Messages;

import de.treinke.minecraftguilds.Main;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.network.NetworkEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static de.treinke.minecraftguilds.Main.MODID;

public class GuildWarning {
    public static final Logger LOGGER = LogManager.getLogger(MODID);

    // A default constructor is always required
    public GuildWarning(){
    }


    public String data = "";

    public GuildWarning(String toSend) {
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


    public static void encode(GuildWarning message, PacketBuffer packet)
    {
        message.toBytes(packet);
    }

    public static GuildWarning decode(PacketBuffer packet)
    {
        GuildWarning message = new GuildWarning();
        message.fromBytes(packet);
        return message;
    }


    public static class Handler {
        public static void handle(GuildWarning message, Supplier<NetworkEvent.Context> ctx)
        {
            Minecraft.getInstance().player.sendMessage(new StringTextComponent(I18n.format("guild.claim.attacked", new String[]{})).setStyle(new Style().setBold(true).setColor(TextFormatting.RED)));
        }
    }
}


