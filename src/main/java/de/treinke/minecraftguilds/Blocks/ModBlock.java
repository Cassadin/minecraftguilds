package de.treinke.minecraftguilds.Blocks;


import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;

public class ModBlock extends Block {
    private int level;

    public ModBlock(Block.Properties properties, String name, int level) {
        super(properties);
        this.level = level;
        setRegistryName("minecraftguilds:" + name);
    }

    @Override
    public ToolType getHarvestTool(BlockState state) {
        return ToolType.PICKAXE;
    }

    @Override
    public int getHarvestLevel(BlockState state) {
        return level;
    }
}