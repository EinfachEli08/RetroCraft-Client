package net.minecraft.client.gui.screens;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.Util;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.LogoRenderer;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.progress.StoringChunkProgressListener;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Objects;

@OnlyIn(Dist.CLIENT)
public class LevelLoadingScreen extends Screen {

   private LogoRenderer logoRenderer;
   private static final long NARRATION_DELAY_MS = 2000L;
   private final StoringChunkProgressListener progressListener;
   private long lastNarration = -1L;
   public static boolean done = true;
   private static final Object2IntMap<ChunkStatus> COLORS = Util.make(new Object2IntOpenHashMap<>(), (p_280803_) -> {
      p_280803_.defaultReturnValue(0);
      p_280803_.put(ChunkStatus.EMPTY, 5526612);
      p_280803_.put(ChunkStatus.STRUCTURE_STARTS, 10066329);
      p_280803_.put(ChunkStatus.STRUCTURE_REFERENCES, 6250897);
      p_280803_.put(ChunkStatus.BIOMES, 8434258);
      p_280803_.put(ChunkStatus.NOISE, 13750737);
      p_280803_.put(ChunkStatus.SURFACE, 7497737);
      p_280803_.put(ChunkStatus.CARVERS, 3159410);
      p_280803_.put(ChunkStatus.FEATURES, 2213376);
      p_280803_.put(ChunkStatus.INITIALIZE_LIGHT, 13421772);
      p_280803_.put(ChunkStatus.LIGHT, 16769184);
      p_280803_.put(ChunkStatus.SPAWN, 15884384);
      p_280803_.put(ChunkStatus.FULL, 16777215);
   });

   public LevelLoadingScreen(StoringChunkProgressListener p_96143_) {
      super(GameNarrator.NO_TITLE);
      this.progressListener = p_96143_;

   }

   public boolean shouldCloseOnEsc() {
      return false;
   }

   protected boolean shouldNarrateNavigation() {
      return false;
   }

   public void removed() {
      this.done = true;
      this.triggerImmediateNarration(true);
   }

   protected void updateNarratedWidget(NarrationElementOutput p_169312_) {
      if (this.done) {
         p_169312_.add(NarratedElementType.TITLE, Component.translatable("narrator.loading.done"));
      } else {
         String s = this.getFormattedProgress();
         p_169312_.add(NarratedElementType.TITLE, s);
      }

   }

   private String getFormattedProgress() {
      return Mth.clamp(this.progressListener.getProgress(), 0, 100) + "%";
   }

   protected void init(){

      done = false;
      this.logoRenderer = new LogoRenderer(false);
   }
   public void render(GuiGraphics gfx, int p_96146_, int p_96147_, float p_96148_) {

      this.renderBackground(gfx);
      this.logoRenderer.renderLogo(gfx, this.width, 2);

      int fieldHeight=50;
      int yPos = 50;

      double d1 = Math.min((double)gfx.guiWidth() * 0.75D, gfx.guiHeight()) * 0.25D;
      double d0 = d1 * 4.0D;
      int j1 = (int)(d0 * 0.5D);
      int k1 = (int) ((int)((double)gfx.guiHeight())/1.8);

      gfx.fill(gfx.guiWidth() / 2 - j1,k1 + yPos,gfx.guiWidth() / 2 + j1,k1 + yPos + fieldHeight,FastColor.ARGB32.color(255, 109, 109, 109));


      gfx.drawString(this.font, this.getFormattedProgress(), gfx.guiWidth() / 2 - j1, k1 - 14, 16777215);

      //TODO: Fix overscaling issue
      this.drawProgressBar(gfx, gfx.guiWidth() / 2 - j1, k1 - 5, gfx.guiWidth() / 2 + j1, k1 + 5, 255);




   }
   private void drawProgressBar(GuiGraphics gfx, int xPos, int yPos, int width, int height, int opacity) {

      int i = Mth.ceil((float)(width - xPos -2) * ((float) Mth.clamp(this.progressListener.getProgress(), 0, 100) / 100));

      int k = FastColor.ARGB32.color(opacity, 109, 109, 109);
      int f = FastColor.ARGB32.color(opacity, 1, 255, 0);

      gfx.fill(xPos, yPos, width ,  height, k);
      gfx.fill(xPos + 2, yPos + 2, xPos + i  ,  height - 2, f);

   }


   //usefull some day?
   public static void renderChunks(GuiGraphics p_283467_, StoringChunkProgressListener p_96151_, int p_96152_, int p_96153_, int p_96154_, int p_96155_) {
      int i = p_96154_ + p_96155_;
      int j = p_96151_.getFullDiameter();
      int k = j * i - p_96155_;
      int l = p_96151_.getDiameter();
      int i1 = l * i - p_96155_;
      int j1 = p_96152_ - i1 / 2;
      int k1 = p_96153_ - i1 / 2;
      int l1 = k / 2 + 1;
      int i2 = -16772609;
      p_283467_.drawManaged(() -> {
         if (p_96155_ != 0) {
            p_283467_.fill(p_96152_ - l1, p_96153_ - l1, p_96152_ - l1 + 1, p_96153_ + l1, -16772609);
            p_283467_.fill(p_96152_ + l1 - 1, p_96153_ - l1, p_96152_ + l1, p_96153_ + l1, -16772609);
            p_283467_.fill(p_96152_ - l1, p_96153_ - l1, p_96152_ + l1, p_96153_ - l1 + 1, -16772609);
            p_283467_.fill(p_96152_ - l1, p_96153_ + l1 - 1, p_96152_ + l1, p_96153_ + l1, -16772609);
         }

         for(int j2 = 0; j2 < l; ++j2) {
            for(int k2 = 0; k2 < l; ++k2) {
               ChunkStatus chunkstatus = p_96151_.getStatus(j2, k2);
               int l2 = j1 + j2 * i;
               int i3 = k1 + k2 * i;
               p_283467_.fill(l2, i3, l2 + p_96154_, i3 + p_96154_, COLORS.getInt(chunkstatus) | -16777216);
            }
         }

      });
   }
}