package de.treinke.minecraftguilds.Events;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import de.treinke.minecraftguilds.network.Messages.GuildCheckAnswer;
import de.treinke.minecraftguilds.network.Messages.GuildClaimList;
import de.treinke.minecraftguilds.network.Messages.GuildInvitesAnswer;
import de.treinke.minecraftguilds.objects.Claim;
import de.treinke.minecraftguilds.objects.Guild;
import de.treinke.minecraftguilds.Main;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

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
            String guild = Main.proxy.getPlayerGuild(event.getPlayer().getName().getString());
            Main.NETWORK.sendTo(new GuildCheckAnswer(guild),(ServerPlayerEntity) event.getPlayer());


            // Invites
            if(guild != null)
                Main.NETWORK.sendTo(new GuildInvitesAnswer(Main.proxy.getInvites(event.getPlayer().getName().getString())),(ServerPlayerEntity) event.getPlayer());

            Main.NETWORK.sendTo(new GuildClaimList(Main.proxy.getGuildClaims()),(ServerPlayerEntity) event.getPlayer());
        }
    }
}
