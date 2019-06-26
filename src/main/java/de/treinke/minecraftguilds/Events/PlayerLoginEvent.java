package de.treinke.minecraftguilds.Events;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import de.treinke.minecraftguilds.objects.Claim;
import de.treinke.minecraftguilds.objects.Guild;
import de.treinke.minecraftguilds.Main;
import de.treinke.minecraftguilds.network.NetworkActions;
import de.treinke.minecraftguilds.network.NetworkMessage;
import de.treinke.minecraftguilds.network.NetworkObject;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

import java.util.List;

import static de.treinke.minecraftguilds.Main.MODID;
import static net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus.MOD;

public class PlayerLoginEvent {

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {


        System.out.println("["+Main.proxy.side+"] Player Logged in: "+event.getPlayer().getName().getString());
        if (event.getPlayer().isServerWorld()) {
            Main.proxy.setServer(event.getPlayer().getServer());

            // Gildencheck
            NetworkObject answer = new NetworkObject();
            answer.action = NetworkActions.GuildCheckAnswer;
            answer.values = Main.proxy.getPlayerGuild(event.getPlayer().getName().getString());
            Main.NETWORK.sendTo(new NetworkMessage((new Gson()).toJson(answer)),(ServerPlayerEntity) event.getPlayer());


            // Invites
            if(answer.values != null)
            {
                List<String> invs = Main.proxy.getInvites(event.getPlayer().getName().getString());

                answer.action = NetworkActions.GuildInvitesAnswer;
                answer.values = (new Gson()).toJson(invs, new TypeToken<List<String>>() {}.getType());//.replace("\"", "\\\"");

                Main.NETWORK.sendTo(new NetworkMessage((new Gson()).toJson(answer)),(ServerPlayerEntity) event.getPlayer());
            }


            Guild.all_claims = Main.proxy.getGuildClaims();

            answer = new NetworkObject();
            answer.action = NetworkActions.GuildClaimList;
            answer.values = (new Gson()).toJson(Guild.all_claims,new TypeToken<List<Claim>>() {}.getType());
            Main.NETWORK.sendTo(new NetworkMessage((new Gson()).toJson(answer)),(ServerPlayerEntity) event.getPlayer());
        }
    }
}
