package de.treinke.minecraftguilds.objects;

import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import java.io.IOException;

public class GuiButton extends Button {

    public int id = -1;

    public GuiButton(int id, int p_i232255_1_, int p_i232255_2_, int p_i232255_3_, int p_i232255_4_, String p_i232255_5_, IPressable p_i232255_6_) {
        super(p_i232255_1_, p_i232255_2_, p_i232255_3_, p_i232255_4_, new StringTextComponent(p_i232255_5_), p_i232255_6_);
        this.id = id;
    }

}
