package de.treinke.minecraftguilds.network;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import de.treinke.minecraftguilds.*;
import de.treinke.minecraftguilds.GUI.GuildGUI;
import de.treinke.minecraftguilds.Items.GuildItems;
import de.treinke.minecraftguilds.objects.Claim;
import de.treinke.minecraftguilds.objects.Guild;
import io.netty.buffer.ByteBuf;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static de.treinke.minecraftguilds.Main.MODID;
import static de.treinke.minecraftguilds.network.NetworkActions.*;

public class NetworkMessage{
    public static final Logger LOGGER = LogManager.getLogger(MODID);

    // A default constructor is always required
    public NetworkMessage(){
    }


    public String json = "";
    private boolean powered;

    public NetworkMessage(String toSend) {
        this.json = toSend;
    }


    public void fromBytes(ByteBuf buf) {

        byte[] dst = new byte[buf.readableBytes()];

        buf.readBytes(dst);
        json = new String(dst);
    }

    public void toBytes(ByteBuf buf) {
        buf.writeBytes(json.getBytes());
    }


    public static void encode(NetworkMessage message, PacketBuffer packet)
    {
        message.toBytes(packet);
    }

    public static NetworkMessage decode(PacketBuffer packet)
    {
        NetworkMessage message = new NetworkMessage();

        message.fromBytes(packet);
        return message;
    }


    public static class Handler {

        public static void handle(NetworkMessage message, Supplier<NetworkEvent.Context> ctx)
        {
            ctx.get().setPacketHandled(true);
            // This is the player the packet was sent to the server from

            // The value that was sent

            NetworkObject na = (new Gson()).fromJson(message.json,new TypeToken<NetworkObject>() {}.getType());
            NetworkObject answer = new NetworkObject();
            Gson g = new Gson();

            System.out.println("["+ Main.proxy.side+"] Networkmessage Received: "+Messagetype(na.action));


            switch(na.action)
            {
                case GuildCreate:
                    Main.proxy.createGuild(na.values,ctx.get().getSender());


//		    	    serverPlayer.getServerWorld().addScheduledTask(() -> {
                    //serverPlayer.inventory.addItemStackToInventory(new ItemStack(Items.DIAMOND, 13));
//		    		});
                case GuildCheck:
                    answer.action = GuildCheckAnswer;
                    answer.values = Main.proxy.getPlayerGuild(ctx.get().getSender().getName().getString());
                    System.out.println("["+Main.proxy.side+"] Value: "+answer.values);

                    if(answer.values != null)
                        Main.NETWORK.sendTo(new NetworkMessage(g.toJson(answer)),ctx.get().getSender());
                    break;
                case GuildCheckAnswer:
                    if(na.values.length() > 0 && na.values != null)
                    {
                        Guild.MyGuild = g.fromJson(na.values,new TypeToken<Guild>() {}.getType());
                        GuildGUI.refreshed = false;
                    }else {
                        Guild.MyGuild = null;
                    }
                    break;
                case GuildInvites:

                    answer.action = GuildInvitesAnswer;
                    answer.values = g.toJson(Main.proxy.getInvites(ctx.get().getSender().getName().getString()), new TypeToken<List<String>>() {}.getType());//.replace("\"", "\\\"");

                    Main.NETWORK.sendTo(new NetworkMessage(g.toJson(answer)),ctx.get().getSender());
                    break;
                case GuildInvitesAnswer:
                    List<String> invs = g.fromJson(na.values, new TypeToken<List<String>>() {}.getType());
                    if(!invs.equals(Guild.single_user_invites)) {
                        Guild.single_user_invites = invs;
                        GuildGUI.refreshed = false;
                    }
                    break;
                case GuildAccept:

                    if(Main.proxy.acceptGuild(na.values, ctx.get().getSender().getName().getString()))
                    {
                        answer.action = GuildCheckAnswer;
                        answer.values = Main.proxy.getPlayerGuild(ctx.get().getSender().getName().getString());
                        if(answer.values != null)
                            Main.NETWORK.sendTo(new NetworkMessage(g.toJson(answer)),ctx.get().getSender());
                    }
                    break;
                case GuildRefuse:
                    Main.proxy.refuseGuild(na.values,ctx.get().getSender().getName().getString());
                    break;
                case GuildInviteUser:
                    String[] inv = g.fromJson(na.values, new TypeToken<String[]>() {}.getType());
                    Main.proxy.inviteUser(inv[0],inv[1]);
                    break;

                case GuildClaimList:
                    Guild.all_claims = g.fromJson(na.values, new TypeToken<List<Claim>>() {}.getType());
                    Main.proxy.showClaimMessage();
                    GuildGUI.refreshed = false;
                    break;
                case GuildDonation:


                    ctx.get().enqueueWork(new Runnable() {
                        @Override
                        public void run() {
                            int amount = ctx.get().getSender().inventory.clearMatchingItems(p -> p.getItem() == GuildItems.COPPER_COIN,-1);
                            amount += ctx.get().getSender().inventory.clearMatchingItems(p -> p.getItem() == GuildItems.SILVER_COID,-1)*10;
                            amount += ctx.get().getSender().inventory.clearMatchingItems(p -> p.getItem() == GuildItems.GOLD_COIN,-1)*100;
                            LOGGER.debug("Spenden: "+amount);
                            Main.proxy.playerdonateGuild(ctx.get().getSender().getName().getString(),amount);

                        }
                    });

                    break;
                case GuildKick:
                    Main.proxy.kickGuild(na.values);
                    break;
                case GuildPromote:
                    Main.proxy.promoteGuild(na.values);
                    break;
                case GuildDemote:
                    Main.proxy.demoteGuild(na.values);
                    break;
                case GuildClaim:
                    final int x = (ctx.get().getSender().getPosition().getX())/16+(ctx.get().getSender().getPosition().getX()<0?-1:0);
                    final int z = (ctx.get().getSender().getPosition().getZ())/16+(ctx.get().getSender().getPosition().getZ()<0?-1:0);

                    Main.proxy.addClaim(na.values,ctx.get().getSender().dimension.getId(),x,z);
                    break;
                default:
            }
        }


        private static int clearInventory(CommandSource source, Collection<ServerPlayerEntity> targetPlayers, Predicate<ItemStack> itemPredicateIn, int maxCount)
        {
            int donation = 0;
            try{


            }catch(Exception ex)
            {
                LOGGER.debug("Fehler beim Spenden: "+ex.getMessage());
            }
            return donation;
        }
    }
}
