package de.treinke.minecraftguilds.Events;

import de.treinke.minecraftguilds.Items.GuildItems;
import de.treinke.minecraftguilds.network.Messages.GuildDonation;
import de.treinke.minecraftguilds.network.Messages.GuildWarningMessage;
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
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static de.treinke.minecraftguilds.Main.MODID;

public class ClaimEvents {
    public static final Logger LOGGER = LogManager.getLogger(MODID);

    public static HashMap<String, Long> attacks = new HashMap<>();

    @OnlyIn(Dist.DEDICATED_SERVER)
    @SubscribeEvent
    public void onPlayerInteract(PlayerInteractEvent.RightClickBlock event) {
        final int x = (event.getPos().getX())/16+(event.getPos().getX()<0?-1:0);
        final int z = (event.getPos().getZ())/16+(event.getPos().getZ()<0?-1:0);
        final int dim = event.getEntityPlayer().dimension.getId();

        List<Claim> lst = Guild.all_claims.stream().filter(p -> p.x==x && p.z == z && p.dim == dim).collect(Collectors.toList());
        if(lst.size() > 0)
        {
            if(!lst.get(0).guild.equals(Main.proxy.getPlayerGuildName(event.getEntity().getName().getString())))
                event.setCanceled(true);
        }
    }



    @OnlyIn(Dist.DEDICATED_SERVER)
    @SubscribeEvent
    public void onPlayerDies(LivingDeathEvent event) {

        if(event.getEntityLiving() instanceof ServerPlayerEntity && event.getSource().getTrueSource() instanceof ServerPlayerEntity)
        {
            Main.proxy.killplayer((ServerPlayerEntity)event.getSource().getTrueSource());
        }
    }


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
            boolean slow_down  = false;
            if(Guild.MyGuild == null)
                slow_down = true;
            else {
                if (!lst.get(0).guild.equals(Guild.MyGuild.name))
                    slow_down = true;
            }

            if(slow_down) {
                event.setNewSpeed(event.getOriginalSpeed()/50);
                boolean warn = true;
                if (attacks.containsKey(lst.get(0).guild))
                    if (System.currentTimeMillis() - attacks.get(lst.get(0).guild) < 300000)
                        warn = false;
                if (warn) {
                    attacks.put(lst.get(0).guild, System.currentTimeMillis());
                    Main.NETWORK.sendToServer(new GuildWarningMessage(lst.get(0).guild));
                }
            }
        }


    }

   // @OnlyIn(Dist.DEDICATED_SERVER)
    @SubscribeEvent
    public void onBreakEvent(BlockEvent.BreakEvent event) {
        final int x = (event.getPos().getX())/16+(event.getPos().getX()<0?-1:0);
        final int z = (event.getPos().getZ())/16+(event.getPos().getZ()<0?-1:0);
        final int dim = event.getPlayer().dimension.getId();

        List<Claim> lst = Guild.all_claims.stream().filter(p -> p.x==x && p.z == z && p.dim == dim).collect(Collectors.toList());
        if(lst.size() > 0)
        {
            String playerguild = Main.proxy.getPlayerGuildName(event.getPlayer().getName().getString());

            if(!lst.get(0).guild.equals(playerguild)) {
                boolean playeronline = Main.proxy.isGuildPlayerOnline(lst.get(0).guild);

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

    
    public static void showMessage(PlayerEntity player, int chunk_x, int chunk_z, boolean force)
    {

        int x = chunk_x;
        int z = chunk_z;
        int dim = player.dimension.getId();

        List<Claim> lst = Guild.all_claims.stream().filter(p -> p.x==x && p.z == z && p.dim == dim).collect(Collectors.toList());
        if(lst.size() > 0)
        {
            if(!lst.get(0).guild.equals(Guild.current_claim_owner)||force)
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
                else {
                    player.sendMessage(new StringTextComponent(I18n.format("guild.claim.enter.danger", lst.get(0).guild)).setStyle(new Style().setBold(true).setColor(TextFormatting.RED)));
                }
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
                    player.sendMessage(new StringTextComponent(I18n.format("guild.claim.leave.danger",  Guild.current_claim_owner)));
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
                showMessage(((PlayerEntity)event.getEntity()),event.getNewChunkX(),event.getNewChunkZ(), false);

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

                    final int x = (player.getPosition().getX()) / 16 + (player.getPosition().getX() < 0 ? -1 : 0);
                    final int z = (player.getPosition().getZ()) / 16 + (player.getPosition().getZ() < 0 ? -1 : 0);
                    final int dim = player.dimension.getId();

                    String attacked_guild = Main.proxy.getPlayerGuildName(player.getName().getString());
                    String attacker_guild = Main.proxy.getPlayerGuildName(caused.getName().getString());
                    if (attacked_guild != null && attacker_guild != null) {
                        if (attacked_guild.equals(attacker_guild))
                        {
                            event.setCanceled(true);
                            return;
                        }
                    } else {
                        if (attacked_guild == null)
                            attacked_guild = "";
                        if (attacker_guild == null)
                            attacker_guild = "";
                    }

                    List<Claim> lst = Guild.all_claims.stream().filter(p -> p.x == x && p.z == z && p.dim == dim).collect(Collectors.toList());
                    String claimowner = null;
                    if (lst.size() > 0)
                        claimowner = lst.get(0).guild;

                    boolean attack_allowed = true;

                    // beide Spieler mind. lv 3?
                    attack_allowed = player.experienceLevel>=3&&caused.experienceLevel>=3;
                    // auf dem gildengebiet einer der beiden spieler?
                    attack_allowed = attack_allowed||attacked_guild.equals(claimowner)||attacker_guild.equals(claimowner);


                    if(attack_allowed) {
                        if (claimowner != null) {
                            if (claimowner.equals(attacked_guild))
                                event.setAmount(event.getAmount() * 0.25F);

                            if (claimowner.equals(attacker_guild))
                                event.setAmount(event.getAmount() * 4F);
                        }
                    }else{
                        event.setCanceled(true);
                    }
                }
        }
    }
}

