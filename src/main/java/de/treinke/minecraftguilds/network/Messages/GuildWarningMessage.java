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

public class GuildWarningMessage {
    public static final Logger LOGGER = LogManager.getLogger(MODID);

    // A default constructor is always required
    public GuildWarningMessage(){
    }


    public String data = "";

    public GuildWarningMessage(String toSend) {
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


    public static void encode(GuildWarningMessage message, PacketBuffer packet)
    {
        message.toBytes(packet);
    }

    public static GuildWarningMessage decode(PacketBuffer packet)
    {
        GuildWarningMessage message = new GuildWarningMessage();
        message.fromBytes(packet);
        return message;
    }


    public static class Handler {
        public static void handle(GuildWarningMessage message, Supplier<NetworkEvent.Context> ctx)
        {
            Main.proxy.warnGuild(message.data);
        }
    }
}


