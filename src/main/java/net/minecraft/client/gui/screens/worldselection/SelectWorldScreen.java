package net.minecraft.client.gui.screens.worldselection;

import com.mojang.logging.LogUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class SelectWorldScreen extends Screen {
   protected final Screen lastScreen;
   private Button deleteButton;
   private Button selectButton;
   private Button renameButton;
   private Button copyButton;
   private WorldSelectionList list;
   public static final ResourceLocation MENU_LOCATION = new ResourceLocation("textures/gui/container/menu/map_select.png");

   public SelectWorldScreen(Screen p_101338_) {
      super(Component.translatable("selectWorld.title"));
      this.lastScreen = p_101338_;
   }

   protected void init() {

      /*
         TODO: To tweak the cutting of at the top and bottom, edit the variables below
      */
      int cuttingTop = 90;
      int cuttingBottom = 90;

      int buttonWidth = 90;

      this.list = new WorldSelectionList(this, this.minecraft, this.width-5, this.height, this.height/2-cuttingTop, this.height/2 + cuttingBottom, 24, "", this.list);
      this.addWidget(this.list);

      this.addRenderableWidget(Button.builder(Component.translatable("test1"),(p_232984_) -> {

      }).bounds(this.width / 2 - (buttonWidth/2) - buttonWidth, this.height / 2 - 124, buttonWidth, 20).build());

      this.addRenderableWidget(Button.builder(Component.translatable("test2"),(p_232984_) -> {

      }).bounds(this.width / 2 - (buttonWidth/2) , this.height / 2 - 124,buttonWidth, 20).build());

      this.addRenderableWidget(Button.builder(Component.translatable("test3"),(p_232984_) -> {

      }).bounds(this.width / 2 - (buttonWidth/2) + buttonWidth, this.height / 2 - 124,buttonWidth, 20).build());

      this.selectButton = this.addRenderableWidget(Button.builder(Component.translatable("selectWorld.select"), (p_232984_) -> {
         this.list.getSelectedOpt().ifPresent(WorldSelectionList.WorldListEntry::joinWorld);
      }).bounds(this.width / 2 - 154, this.height - 52, 150, 20).build());

      this.addRenderableWidget(Button.builder(Component.translatable("selectWorld.create"), (p_280918_) -> {
         CreateWorldScreen.openFresh(this.minecraft, this);
      }).bounds(this.width / 2 + 4, this.height - 52, 150, 20).build());

      this.renameButton = this.addRenderableWidget(Button.builder(Component.translatable("selectWorld.edit"), (p_101378_) -> {
         this.list.getSelectedOpt().ifPresent(WorldSelectionList.WorldListEntry::editWorld);
      }).bounds(this.width / 2 + 4 + 150, this.height - 28, 72, 20).build());

      this.deleteButton = this.addRenderableWidget(Button.builder(Component.translatable("selectWorld.delete"), (p_101376_) -> {
         this.list.getSelectedOpt().ifPresent(WorldSelectionList.WorldListEntry::deleteWorld);
      }).bounds(this.width, this.height, this.width, this.height).build());

      this.copyButton = this.addRenderableWidget(Button.builder(Component.translatable("selectWorld.recreate"), (p_101373_) -> {
         this.list.getSelectedOpt().ifPresent(WorldSelectionList.WorldListEntry::recreateWorld);
      }).bounds(this.width, this.height, this.width, this.height).build());

      this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, (p_280917_) -> {
         this.minecraft.setScreen(this.lastScreen);
      }).bounds(this.width, this.height, this.width, this.height).build());

   }

   public void onClose() {
      this.minecraft.setScreen(this.lastScreen);
   }

   public void render(GuiGraphics gfx, int p_281534_, int p_281859_, float p_283289_) {

      float scale = this.minecraft.options.guiScale().get() * 0.7F;
      float scaleFactor = 1.2F;

      int unscaledImgWidth = 224;
      int unscaledImgHeight = 176;

      int imgWidth = (int) (224 * scaleFactor);
      int imgHeight = (int) (176 * scaleFactor);

      int x = (this.width / 2 - imgWidth / 2);
      int y = (this.height / 2 - imgHeight / 2);

      this.renderBackground(gfx);
      gfx.blit(MENU_LOCATION,x, y, imgWidth, imgHeight , 0.0F, 0.0F, unscaledImgWidth, unscaledImgHeight, 256, 256);

      this.list.render(gfx, p_281534_, p_281859_, p_283289_);

      super.render(gfx, p_281534_, p_281859_, p_283289_);
   }

   public void updateButtonStatus(boolean p_276122_, boolean p_276113_) {
      this.selectButton.active = p_276122_;
      this.renameButton.active = p_276122_;
      this.copyButton.active = p_276122_;
      this.deleteButton.active = p_276113_;
   }

   public void removed() {
      if (this.list != null) {
         this.list.children().forEach(WorldSelectionList.Entry::close);
      }

   }
}