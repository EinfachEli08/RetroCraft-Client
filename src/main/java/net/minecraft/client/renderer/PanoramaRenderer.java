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
    public void render(GuiGraphics gfx, int width, int height){
        scroll += this.minecraft.options.panoramaSpeed().get() / 60 * this.minecraft.getDeltaFrameTime();
        gfx.blit(PANORAMA_LOCATION, 0, 0, (2060/width)*width, height, scroll, 0.0F, 16, 128, 16, 128);

    }
}
