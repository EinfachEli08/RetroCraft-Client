package net.minecraft.client.gui.components;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LogoRenderer {
   public static final ResourceLocation MINECRAFT_LOGO = new ResourceLocation("textures/gui/title/minecraft.png");
   public static final ResourceLocation EASTER_EGG_LOGO = new ResourceLocation("textures/gui/title/minceraft.png");
   public static final ResourceLocation MINECRAFT_EDITION = new ResourceLocation("textures/gui/title/edition.png");

   private final boolean showEasterEgg = (double)RandomSource.create().nextFloat() < 1.0E-4D;
   private final boolean keepLogoThroughFade;

   public LogoRenderer(boolean p_265300_) {
      this.keepLogoThroughFade = p_265300_;
   }

   public void renderLogo(GuiGraphics p_282217_, int p_283270_, float p_282051_) {
      this.renderLogo(p_282217_, p_283270_, p_282051_, 25);
   }

   public void renderLogo(GuiGraphics p_281856_, int p_281512_, float p_281290_, int p_282296_) {
      p_281856_.setColor(1.0F, 1.0F, 1.0F, this.keepLogoThroughFade ? 1.0F : p_281290_);
      int i = p_281512_ / 2 - 128;
      p_281856_.blit(this.showEasterEgg ? EASTER_EGG_LOGO : MINECRAFT_LOGO, i, p_282296_, 0.0F, 0.0F, 256, 44, 256, 64);
      int j = p_281512_ / 2 - 64;
      int k = p_282296_ + 44 - 7;
      p_281856_.blit(MINECRAFT_EDITION, j, k, 0.0F, 0.0F, 128, 14, 128, 16);
      p_281856_.setColor(1.0F, 1.0F, 1.0F, 1.0F);
   }
}