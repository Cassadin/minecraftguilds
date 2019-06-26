package de.treinke.minecraftguilds.Items;


import de.treinke.minecraftguilds.objects.GuildTab;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;

public class ItemRegistry {
    public static void registry(RegistryEvent.Register<Item> e) {
        GuildItems.COPPER_COIN = register(new Item(new Item.Properties().group(GuildTab.MOD_ITEM_GROUP)).setRegistryName("minecraftguilds:copper_coin"), e);
        GuildItems.SILVER_COID = register(new Item(new Item.Properties().group(GuildTab.MOD_ITEM_GROUP)).setRegistryName("minecraftguilds:silver_coin"), e);
        GuildItems.GOLD_COIN = register(new Item(new Item.Properties().group(GuildTab.MOD_ITEM_GROUP)).setRegistryName("minecraftguilds:gold_coin"), e);
     }

    private static Item register(Item item, RegistryEvent.Register<Item> e) {
        e.getRegistry().register(item);
        return item;
    }
}