package net.minecraft.client.gui.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.tabs.Tab;
import net.minecraft.client.gui.components.tabs.TabManager;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TabButton extends AbstractWidget {
   private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation("textures/gui/tab_button.png");
   private static final int TEXTURE_WIDTH = 130;
   private static final int TEXTURE_HEIGHT = 21;
   private static final int TEXTURE_BORDER = 4;
   private static final int TEXTURE_BORDER_BOTTOM = 100;
   private final TabManager tabManager;
   private final Tab tab;
   private final int type;

   public TabButton(TabManager p_275399_, Tab p_275391_, int p_275340_, int p_275364_, int type) {
      super(0, 0, p_275340_, p_275364_, p_275391_.getTabTitle());
      this.tabManager = p_275399_;
      this.tab = p_275391_;
      this.type = type;
   }

   public void renderWidget(GuiGraphics p_283350_, int p_283437_, int p_281595_, float p_282117_) {
      p_283350_.blitNineSliced(TEXTURE_LOCATION, this.getX(), this.getX(), this.width, TEXTURE_HEIGHT, TEXTURE_BORDER, TEXTURE_BORDER, TEXTURE_BORDER, TEXTURE_BORDER_BOTTOM, TEXTURE_WIDTH, TEXTURE_HEIGHT, 0, this.getTextureY());
      // texture, x, y, full width, full height, left border, top border, right border, bottom border, texture width, texture height, tex offset x, tex offset y
      Font font = Minecraft.getInstance().font;

      this.renderString(p_283350_, font, 0x181818);


   }

   public void renderString(GuiGraphics gfx, Font font, int color) {
      int i = this.getX() + 1;
      int j = this.getY() - 3 + (this.isSelected() ? 0 : 3);
      int k = this.getX() + this.getWidth() - 1;
      int l = this.getY() + this.getHeight();

      //TODO: Remove shadow
      renderScrollingString(gfx, font, this.getMessage(), i, j, k, l, color);
   }



   protected int getTextureY() {
      int i = 4;
      if (this.isSelected()) i = this.type;

      return i * TEXTURE_HEIGHT;
   }

   protected void updateWidgetNarration(NarrationElementOutput p_275465_) {
      p_275465_.add(NarratedElementType.TITLE, Component.translatable("gui.narrate.tab", this.tab.getTabTitle()));
   }

   public void playDownSound(SoundManager p_276302_) {
   }

   public Tab tab() {
      return this.tab;
   }

   public boolean isSelected() {
      return this.tabManager.getCurrentTab() == this.tab;
   }
}