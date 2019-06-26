package de.treinke.minecraftguilds.objects;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;

public class GuiTextField extends TextFieldWidget {
    public int id = -1;

    public GuiTextField(int id, FontRenderer p_i51137_1_, int p_i51137_2_, int p_i51137_3_, int p_i51137_4_, int p_i51137_5_, String p_i51137_6_) {
        super(p_i51137_1_, p_i51137_2_, p_i51137_3_, p_i51137_4_, p_i51137_5_, p_i51137_6_);
        this.id = id;
    }

    public GuiTextField(int txt_create, FontRenderer font, int i, int i1, int i2, int i3) {
        super(font, i, i1, i2, i3, "");
    }
}
