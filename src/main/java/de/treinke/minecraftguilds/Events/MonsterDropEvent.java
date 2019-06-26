package de.treinke.minecraftguilds.Events;

import de.treinke.minecraftguilds.Items.GuildItems;
import de.treinke.minecraftguilds.objects.Claim;
import de.treinke.minecraftguilds.objects.Guild;
import de.treinke.minecraftguilds.Main;
import net.minecraft.item.Item;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.player.PlayerEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus.MOD;

public class MonsterDropEvent {
    public static final Logger LOGGER = LogManager.getLogger(Main.MODID);
    public static double rand;
    public static Random r = new Random();

    @SubscribeEvent
    public void onEntityDrop(LivingDropsEvent event) {

        if (event.getSource().getTrueSource() instanceof PlayerEntity) {
            //Entity player = event.getSource().getTrueSource();
            int chance = r.nextInt(1000);

            if (event.getEntityLiving() instanceof MonsterEntity) {
                Item drop = null;
                if (chance <= 5) // 0.5% Chance auf eine Goldene Münze
                    drop = GuildItems.GOLD_COIN;
                else if (chance <= 100) // 10% Chance auf eine Silberne Münze
                    drop = GuildItems.SILVER_COID;
                else if (chance <= 900) // 90% Chance auf eine Kupferne Münze
                    drop = GuildItems.COPPER_COIN;


                if (drop != null) {
                    final int x = (event.getEntityLiving().getPosition().getX()) / 16 + (event.getEntityLiving().getPosition().getX() < 0 ? -1 : 0);
                    final int z = (event.getEntityLiving().getPosition().getZ()) / 16 + (event.getEntityLiving().getPosition().getZ() < 0 ? -1 : 0);
                    final int dim = event.getEntityLiving().dimension.getId();

                    List<Claim> lst = Guild.all_claims.stream().filter(p -> p.x == x && p.z == z && p.dim == dim).collect(Collectors.toList());
                    if (lst.size() > 0) {
                        if (drop == GuildItems.GOLD_COIN) // 0.5% Chance auf eine Goldene Münze
                            Main.proxy.donateGuild(lst.get(0).guild, 100);
                        else if (drop == GuildItems.SILVER_COID) // 10% Chance auf eine Silberne Münze
                            Main.proxy.donateGuild(lst.get(0).guild, 10);
                        else if (drop == GuildItems.COPPER_COIN) // 90% Chance auf eine Kupferne Münze
                            Main.proxy.donateGuild(lst.get(0).guild, 1);

                    }else{
                        event.getEntityLiving().entityDropItem(drop,1);
                    }
                }


                // Verteilung der Monster-EP
            }
        }
    }
}
