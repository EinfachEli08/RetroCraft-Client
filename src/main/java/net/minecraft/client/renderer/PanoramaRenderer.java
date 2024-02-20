package net.minecraft.client.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.Util;

public class PanoramaRenderer {
    Minecraft minecraft;
    public static final ResourceLocation PANORAMA_LOCATION = new ResourceLocation("textures/gui/title/background/panorama.png");
    private float scroll = 0;

    public PanoramaRenderer(Minecraft minecraft){
        this.minecraft = minecraft;
    }
    public void render(GuiGraphics gfx, int height){
        scroll += (float) (this.minecraft.options.panoramaSpeed().get() / 180 * this.minecraft.getDeltaFrameTime());

        float aspectRatio = 6150f / 1080f;
        int targetWidth = Math.round(height * aspectRatio);


        gfx.blit(PANORAMA_LOCATION, 0, 0, targetWidth, height, scroll, 0.0F, 16, 128, 16, 128);




    }
}
