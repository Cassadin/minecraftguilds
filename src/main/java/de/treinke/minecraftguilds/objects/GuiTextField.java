package de.treinke.minecraftguilds.objects;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import javax.annotation.Nullable;

public class GuiTextField extends TextFieldWidget {
    public int id = -1;

    public GuiTextField(FontRenderer p_i232260_1_, int p_i232260_2_, int p_i232260_3_, int p_i232260_4_, int p_i232260_5_, ITextComponent p_i232260_6_) {
        super(p_i232260_1_, p_i232260_2_, p_i232260_3_, p_i232260_4_, p_i232260_5_, p_i232260_6_);
    }
/*
    public GuiTextField(int id, FontRenderer p_i232259_1_, int p_i232259_2_, int p_i232259_3_, int p_i232259_4_, int p_i232259_5_, @Nullable TextFieldWidget p_i232259_6_, ITextComponent p_i232259_7_) {
        super(p_i232259_1_, p_i232259_2_, p_i232259_3_, p_i232259_4_, p_i232259_5_, p_i232259_6_, p_i232259_7_);
        this.id = id;
    }*/


    public GuiTextField(int id, FontRenderer p_i51137_1_, int p_i51137_2_, int p_i51137_3_, int p_i51137_4_, int p_i51137_5_)
    {
        this(id,p_i51137_1_,p_i51137_2_,p_i51137_3_,p_i51137_4_,p_i51137_5_,"");
    }

    public GuiTextField(int id, FontRenderer p_i51137_1_, int p_i51137_2_, int p_i51137_3_, int p_i51137_4_, int p_i51137_5_, String p_i51137_6_) {
        super(p_i51137_1_, p_i51137_2_, p_i51137_3_, p_i51137_4_, p_i51137_5_, null, new StringTextComponent(p_i51137_6_));
        this.id = id;
    }

}
