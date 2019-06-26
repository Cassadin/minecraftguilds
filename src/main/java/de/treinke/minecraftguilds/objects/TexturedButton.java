package de.treinke.minecraftguilds.objects;

import com.mojang.blaze3d.platform.GlStateManager;
import de.treinke.minecraftguilds.Main;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public class TexturedButton extends GuiButton
{
    private ResourceLocation resource = new ResourceLocation(Main.MODID,"textures/gui/guild/guild.png");;
    private int tposx = 0;
    private int tposy = 0;

    public TexturedButton(int id, int xPos, int yPos,int xtpos, int ytpos, int width, int height, ResourceLocation pResource, IPressable onclick)
    {
        super(id, xPos, yPos, width, height, "", onclick);
        this.resource = pResource;
        this.visible = true;
        this.width = width;
        this.height = height;
        this.tposx = xtpos;
        this.tposy = ytpos;
    }


    @Override
    public void renderButton(int p_renderButton_1_, int p_renderButton_2_, float p_renderButton_3_) {
        //super.renderButton(p_renderButton_1_, p_renderButton_2_, p_renderButton_3_);

        if (this.visible)
        {
            GlStateManager.pushMatrix();
            Minecraft.getInstance().getRenderManager().textureManager.bindTexture(this.resource);
            blit(this.x, this.y, this.tposx, this.tposy, this.width, this.height);
            GlStateManager.popMatrix();
        }
    }

    public int getId()
    {
        return this.id;
    }
}