package net.minecraft.client.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PanoramaRenderer {
   private final Minecraft minecraft;
   private final CubeMap cubeMap;


   public PanoramaRenderer() {
      this.cubeMap = new CubeMap(new ResourceLocation("textures/gui/title/background/panorama"));
      this.minecraft = Minecraft.getInstance();
   }

   public void render(float p_110004_, float p_110005_) {

      this.cubeMap.render(this.minecraft, 10.0F, -Screen.spin, p_110005_);
   }

   private static float wrap(float p_249058_, float p_249548_) {
      return p_249058_ > p_249548_ ? p_249058_ - p_249548_ : p_249058_;
   }
}