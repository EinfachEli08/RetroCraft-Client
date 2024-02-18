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
   private static final int TEXTURE_HEIGHT = 24;
   private static final int TEXTURE_BORDER = 2;
   private static final int TEXTURE_BORDER_BOTTOM = 0;
   private static final int SELECTED_OFFSET = 3;
   private static final int TEXT_MARGIN = 1;
   private static final int UNDERLINE_HEIGHT = 1;
   private static final int UNDERLINE_MARGIN_X = 4;
   private static final int UNDERLINE_MARGIN_BOTTOM = 2;
   private final TabManager tabManager;
   private final Tab tab;
   private final int yPos;

   public TabButton(TabManager p_275399_, Tab p_275391_, int p_275340_, int p_275364_, int yPos) {
      super(0, 0, p_275340_, p_275364_, p_275391_.getTabTitle());
      this.tabManager = p_275399_;
      this.tab = p_275391_;
      this.yPos = yPos;
   }

   public void renderWidget(GuiGraphics p_283350_, int p_283437_, int p_281595_, float p_282117_) {
      p_283350_.blitNineSliced(TEXTURE_LOCATION, this.getX(), this.yPos, this.width, 24, 4, 4, 4, 100, 130, 24, 0, this.getTextureY());
      Font font = Minecraft.getInstance().font;

      this.renderString(p_283350_, font, 0x181818);


   }

   public void renderString(GuiGraphics gfx, Font font, int color) {
      int i = this.getX() + 1;
      int j = this.yPos - 3 + (this.isSelected() ? 0 : 3);
      int k = this.getX() + this.getWidth() - 1;
      int l = this.yPos + this.getHeight();

      //TODO: Remove shadow
      renderScrollingString(gfx, font, this.getMessage(), i, j, k, l, color);
   }



   protected int getTextureY() {
      int i = 2;
      if (this.isSelected() && this.isHoveredOrFocused()) {
         i = 1;
      } else if (this.isSelected()) {
         i = 0;
      } else if (this.isHoveredOrFocused()) {
         i = 3;
      }

      return i * 24;
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