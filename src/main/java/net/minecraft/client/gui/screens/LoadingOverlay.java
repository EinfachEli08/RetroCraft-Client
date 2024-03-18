package net.minecraft.client.gui.screens;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.IntSupplier;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.LogoRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.resources.metadata.texture.TextureMetadataSection;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.VanillaPackResources;
import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraft.server.packs.resources.ReloadInstance;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LoadingOverlay extends Overlay {

   static final ResourceLocation MOJANG_LOGO = new ResourceLocation("textures/gui/title/minecraft.png");
   static final ResourceLocation FOURJ_LOGO = new ResourceLocation("textures/gui/title/4j.png");
   private static final int LOGO_BACKGROUND_COLOR = FastColor.ARGB32.color(100, 255, 255, 255);
   private static final IntSupplier BRAND_BACKGROUND = () -> {
      return LOGO_BACKGROUND_COLOR;
   };
   private static final int LOGO_SCALE = 256;

   private final Minecraft minecraft;
   private final ReloadInstance reload;
   private final Consumer<Optional<Throwable>> onFinish;
   private final boolean fadeIn;
   private float currentProgress;
   private long fadeOutStart = -1L;
   private long fadeInStart = -1L;

   public LoadingOverlay(Minecraft p_96172_, ReloadInstance p_96173_, Consumer<Optional<Throwable>> p_96174_, boolean p_96175_) {
      this.minecraft = p_96172_;
      this.reload = p_96173_;
      this.onFinish = p_96174_;
      this.fadeIn = p_96175_;
   }

   public static void registerTextures(Minecraft p_96190_) {
      p_96190_.getTextureManager().register(MOJANG_LOGO, new LoadingOverlay.LogoTexture());
   }

   private static int replaceAlpha(int p_169325_, int p_169326_) {
      return p_169325_ & 16777215 | p_169326_ << 24;
   }

   public void render(GuiGraphics gfx, int p_282704_, int p_283650_, float p_283394_) {

      int guiWidth = gfx.guiWidth();
      int guiHeight = gfx.guiHeight();
      long millis = Util.getMillis();

      if (this.fadeIn && this.fadeInStart == -1L) {
         this.fadeInStart = millis;
      }

      float f = this.fadeOutStart > -1L ? (float)(millis - this.fadeOutStart) / 10.0F : -1.0F;
      float f1 = this.fadeInStart > -1L ? (float)(millis - this.fadeInStart) / 500.0F : -1.0F;
      float f2;
      float f6 = this.reload.getActualProgress();
      this.currentProgress = Mth.clamp(this.currentProgress * 0.95F + f6 * 0.050000012F, 0.0F, 1.0F);
      if (f >= 1.0F) {
         if (this.minecraft.screen != null) {
            this.minecraft.screen.render(gfx, 0, 0, p_283394_);
         }

         int l = Mth.ceil((1.0F - Mth.clamp(f - 1.0F, 0.0F, 1.0F)) * 255.0F);
         gfx.fill(RenderType.guiOverlay(), 0, 0, guiWidth, guiHeight, replaceAlpha(BRAND_BACKGROUND.getAsInt(), l));
         f2 = 1.0F - Mth.clamp(f - 1.0F, 0.0F, 1.0F);
      } else if (this.fadeIn) {
         if (this.minecraft.screen != null && f1 < 1.0F) {
            this.minecraft.screen.render(gfx, p_282704_, p_283650_, p_283394_);
         }

         int l1 = Mth.ceil(Mth.clamp((double)f1, 0.15D, 1.0D) * 255.0D);
         gfx.fill(RenderType.guiOverlay(), 0, 0, guiWidth, guiHeight, replaceAlpha(BRAND_BACKGROUND.getAsInt(), l1));
         f2 = Mth.clamp(f1, 0.0F, 1.0F);
      } else {
         GlStateManager._clearColor(1, 1, 1, 1);
         GlStateManager._clear(16384, Minecraft.ON_OSX);
         f2 = 1.0F;
      }

      this.currentProgress = Mth.clamp(this.currentProgress * 0.95F + f6 * 0.050000012F, 0.0F, 1.0F);


      if (f >= 2.0F) {
         this.minecraft.setOverlay(null);
      }

      if (this.fadeOutStart == -1L && this.reload.isDone() && (!this.fadeIn || f1 >= 2.0F)) {
         try {
            this.reload.checkExceptions();
            this.onFinish.accept(Optional.empty());
         } catch (Throwable throwable) {
            this.onFinish.accept(Optional.of(throwable));
         }

         this.fadeOutStart = Util.getMillis();
         if (this.minecraft.screen != null) {
            this.minecraft.screen.init(this.minecraft, gfx.guiWidth(), gfx.guiHeight());
         }
      }

      RenderSystem.disableDepthTest();
      RenderSystem.depthMask(false);
      RenderSystem.enableBlend();

      gfx.setColor(1.0F, 1.0F, 1.0F, f2);

      gfx.blit(MOJANG_LOGO, guiWidth / 2 - LOGO_SCALE/2, guiHeight/2 - ((LOGO_SCALE/4)/2), 0, 0, LOGO_SCALE, LOGO_SCALE/4, LOGO_SCALE, LOGO_SCALE/4);

      gfx.setColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.disableBlend();
      RenderSystem.depthMask(true);
      RenderSystem.enableDepthTest();
   }
    @OnlyIn(Dist.CLIENT)
    static class LogoTexture extends SimpleTexture {
      public LogoTexture() {
         super(LoadingOverlay.MOJANG_LOGO);
      }

      protected SimpleTexture.TextureImage getTextureImage(ResourceManager p_96194_) {
         VanillaPackResources vanillapackresources = Minecraft.getInstance().getVanillaPackResources();
         IoSupplier<InputStream> iosupplier = vanillapackresources.getResource(PackType.CLIENT_RESOURCES, LoadingOverlay.MOJANG_LOGO);
         if (iosupplier == null) {
            return new SimpleTexture.TextureImage(new FileNotFoundException(LoadingOverlay.MOJANG_LOGO.toString()));
         } else {
            try (InputStream inputstream = iosupplier.get()) {
               return new SimpleTexture.TextureImage(new TextureMetadataSection(true, true), NativeImage.read(inputstream));
            } catch (IOException ioexception) {
               return new SimpleTexture.TextureImage(ioexception);
            }
         }
      }
   }
}