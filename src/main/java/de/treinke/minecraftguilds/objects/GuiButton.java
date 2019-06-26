package de.treinke.minecraftguilds.objects;

import net.minecraft.client.gui.widget.button.Button;

import java.io.IOException;

public class GuiButton extends Button {

    public int id = -1;

    public GuiButton(int id, int p_i51141_1_, int p_i51141_2_, int p_i51141_3_, int p_i51141_4_, String p_i51141_5_, IPressable onclick) {
        super(p_i51141_1_, p_i51141_2_, p_i51141_3_, p_i51141_4_, p_i51141_5_, onclick);

        this.id = id;
    }
}
