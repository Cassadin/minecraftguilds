package de.treinke.minecraftguilds.Blocks;

import de.treinke.minecraftguilds.objects.GuildTab;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.event.RegistryEvent;


public class BlockRegistry {

    public static Block EXAMPLE;


    public static void registry(RegistryEvent.Register<Block> e) {

        EXAMPLE = register(new ModBlock(Block.Properties.create(Material.ROCK).hardnessAndResistance(2, 6).sound(SoundType.STONE), "example", 0), e);
    }


    public static void itemRegistry(RegistryEvent.Register<Item> e) {
        registerItemBlock(EXAMPLE, e);
    }

    private static Block register(Block block, RegistryEvent.Register<Block> e) {
        e.getRegistry().register(block);
        return block;
    }

    private static void registerItemBlock(Block block, RegistryEvent.Register<Item> e) {
        registerItemBlock(block, e, true);
    }

    private static void registerItemBlock(Block block, RegistryEvent.Register<Item> e, boolean withGroup) {
        e.getRegistry().register(new BlockItem(block, new Item.Properties().group(GuildTab.MOD_ITEM_GROUP)) {
            @Override
            public ITextComponent getDisplayName(ItemStack stack) {
                return getBlock().getNameTextComponent();
            }
        }.setRegistryName(block.getRegistryName()));
    }
}
