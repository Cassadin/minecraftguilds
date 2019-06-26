package de.treinke.minecraftguilds.Events;

import com.google.gson.Gson;
import de.treinke.minecraftguilds.Items.GuildItems;
import de.treinke.minecraftguilds.network.Messages.GuildDonation;
import de.treinke.minecraftguilds.objects.Claim;
import de.treinke.minecraftguilds.objects.Guild;
import de.treinke.minecraftguilds.Main;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.stream.Collectors;

import static de.treinke.minecraftguilds.Main.MODID;

public class ClaimEvents {
    public static final Logger LOGGER = LogManager.getLogger(MODID);




    @OnlyIn(Dist.DEDICATED_SERVER)
    @SubscribeEvent
    public void onPlayerInteract(PlayerInteractEvent event) {
    {

        final int x = (event.getPos().getX())/16+(event.getPos().getX()<0?-1:0);
        final int z = (event.getPos().getZ())/16+(event.getPos().getZ()<0?-1:0);
        final int dim = event.getEntityPlayer().dimension.getId();

        List<Claim> lst = Guild.all_claims.stream().filter(p -> p.x==x && p.z == z && p.dim == dim).collect(Collectors.toList());
        if(lst.size() > 0)
        {
            if(!lst.get(0).guild.equals(Main.proxy.getPlayerGuildName(event.getEntity().getName().getString())))
                event.setCanceled(true);
        }
    }}



    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onHitEvent(PlayerEvent.BreakSpeed event)
    {
        final int x = (event.getPos().getX())/16+(event.getPos().getX()<0?-1:0);
        final int z = (event.getPos().getZ())/16+(event.getPos().getZ()<0?-1:0);
        final int dim = event.getEntityPlayer().dimension.getId();

        List<Claim> lst = Guild.all_claims.stream().filter(p -> p.x==x && p.z == z && p.dim == dim).collect(Collectors.toList());
        if(lst.size() > 0)
        {
            if(Guild.MyGuild == null)
                event.setNewSpeed(event.getOriginalSpeed()/50);
            else
                if(!lst.get(0).guild.equals(Guild.MyGuild.name))
                    event.setNewSpeed(event.getOriginalSpeed()/50);
        }


    }

    @OnlyIn(Dist.DEDICATED_SERVER)
    @SubscribeEvent
    public void onBreakEvent(BlockEvent.BreakEvent event) {
        Main.proxy.showSide();
        final int x = (event.getPos().getX())/16+(event.getPos().getX()<0?-1:0);
        final int z = (event.getPos().getZ())/16+(event.getPos().getZ()<0?-1:0);
        final int dim = event.getPlayer().dimension.getId();

        List<Claim> lst = Guild.all_claims.stream().filter(p -> p.x==x && p.z == z && p.dim == dim).collect(Collectors.toList());
        if(lst.size() > 0)
        {
            String playerguild = Main.proxy.getPlayerGuildName(event.getPlayer().getName().getString());

            if(!lst.get(0).guild.equals(playerguild)) {
                boolean playeronline = !Main.proxy.isGuildPlayerOnline(lst.get(0).guild);

                event.setCanceled(!playeronline);
            }
        }
    }


    @OnlyIn(Dist.DEDICATED_SERVER)
    @SubscribeEvent
    public void onPlaceEvent(BlockEvent.EntityPlaceEvent event) {

        final int x = (event.getPos().getX())/16+(event.getPos().getX()<0?-1:0);
        final int z = (event.getPos().getZ())/16+(event.getPos().getZ()<0?-1:0);
        final int dim = event.getEntity().dimension.getId();

        if(event.getEntity() instanceof ServerPlayerEntity) {
            List<Claim> lst = Guild.all_claims.stream().filter(p -> p.x == x && p.z == z && p.dim == dim).collect(Collectors.toList());
            if (lst.size() > 0) {
                if (!lst.get(0).guild.equals(Main.proxy.getPlayerGuildName(event.getEntity().getName().getString())))
                    event.setCanceled(true);
            }
        }
    }

    
    public static void showMessage(PlayerEntity player, int chunk_x, int chunk_z)
    {

        int x = chunk_x;
        int z = chunk_z;
        int dim = player.dimension.getId();

        List<Claim> lst = Guild.all_claims.stream().filter(p -> p.x==x && p.z == z && p.dim == dim).collect(Collectors.toList());
        if(lst.size() > 0)
        {
            if(!lst.get(0).guild.equals(Guild.current_claim_owner))
            {
                Guild.current_claim_owner = lst.get(0).guild;
                boolean own = false;
                if(Guild.MyGuild != null)
                    if(Guild.current_claim_owner.equals(Guild.MyGuild.name))
                        own = true;

                if(own)
                {
                    player.sendMessage(new StringTextComponent(I18n.format("guild.claim.enter.save", new Object[0])));
                    if(		player.inventory.hasItemStack(new ItemStack(GuildItems.COPPER_COIN))||
                            player.inventory.hasItemStack(new ItemStack(GuildItems.SILVER_COID))||
                            player.inventory.hasItemStack(new ItemStack(GuildItems.GOLD_COIN)))
                    {
                        player.inventory.clearMatchingItems(p -> p.getItem() == GuildItems.COPPER_COIN,-1);
                        player.inventory.clearMatchingItems(p -> p.getItem() == GuildItems.SILVER_COID,-1);
                        player.inventory.clearMatchingItems(p -> p.getItem() == GuildItems.GOLD_COIN,-1);

                        Main.NETWORK.sendToServer(new GuildDonation());
                    }
                }
                else
                    player.sendMessage(new StringTextComponent(String.format(I18n.format("guild.claim.enter.danger", new Object[0]),lst.get(0).guild)).setStyle(new Style().setBold(true).setColor(TextFormatting.RED)));

            }
        }else {
            if(Guild.current_claim_owner.length() > 0)
            {
                boolean own = false;
                if(Guild.MyGuild != null)
                    if(Guild.current_claim_owner.equals(Guild.MyGuild.name))
                        own = true;

                if(own)
                    player.sendMessage(new StringTextComponent(I18n.format("guild.claim.leave.save", new Object[0])));
                else {
                    player.sendMessage(new StringTextComponent(String.format(I18n.format("guild.claim.leave.danger", new Object[0]), Guild.current_claim_owner)));
                }
                Guild.current_claim_owner = "";
            }
        }
    }


    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent(receiveCanceled=true)
    public void onEvent(EntityEvent.EnteringChunk event)
    {
        if(event.getEntity() instanceof PlayerEntity)
            if(event.getEntity() == Minecraft.getInstance().player)
                showMessage(((PlayerEntity)event.getEntity()),event.getNewChunkX(),event.getNewChunkZ());

    }


    @SubscribeEvent(receiveCanceled=true)
    public void onDamage(LivingDamageEvent event)
    {
        if(event.getEntityLiving() instanceof  ServerPlayerEntity) {
            Main.proxy.showSide();
            ServerPlayerEntity player = (ServerPlayerEntity) event.getEntityLiving();
            DamageSource damage = event.getSource();
            if (damage.getTrueSource() != null)
                if (damage.getTrueSource() instanceof ServerPlayerEntity) {

                    ServerPlayerEntity caused = (ServerPlayerEntity) event.getSource().getTrueSource();

                    String attacked_guild = Main.proxy.getPlayerGuildName(player.getName().getString());
                    String attacker_guild = Main.proxy.getPlayerGuildName(caused.getName().getString());

                    if(attacked_guild != null && attacker_guild != null) {
                        if (attacked_guild.equals(attacker_guild))
                            event.setCanceled(true);
                    }else{
                        if(attacked_guild == null)
                            attacked_guild = "";
                        if(attacker_guild == null)
                            attacker_guild = "";
                    }

                    LOGGER.debug(player.getName().getString()+ " was attacked by " + damage.getTrueSource().getName().getString());


                    final int x = (player.getPosition().getX())/16+(player.getPosition().getX()<0?-1:0);
                    final int z = (player.getPosition().getZ())/16+(player.getPosition().getZ()<0?-1:0);
                    final int dim = player.dimension.getId();

                    List<Claim> lst = Guild.all_claims.stream().filter(p -> p.x==x && p.z == z && p.dim == dim).collect(Collectors.toList());
                    if(lst.size() > 0)
                    {

                        // ist der angegriffene in seinem Gebiet? dann * 0.25
                        if(lst.get(0).guild.equals(attacked_guild))
                            event.setAmount(event.getAmount()*0.25F);

                        // ist der angegriffene in meinem Gebiet? dann * 4
                        if(lst.get(0).guild.equals(attacker_guild))
                            event.setAmount(event.getAmount()*4F);
                    }
                }
        }
    }
}

