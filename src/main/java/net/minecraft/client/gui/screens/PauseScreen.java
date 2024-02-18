package net.minecraft.client.gui.screens;

import com.mojang.realmsclient.RealmsMainScreen;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.LogoRenderer;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.screens.achievement.StatsScreen;
import net.minecraft.client.gui.screens.advancements.AdvancementsScreen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.social.SocialInteractionsScreen;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PauseScreen extends Screen {
   private static final Component RETURN_TO_GAME = Component.translatable("menu.returnToGame");
   private static final Component ADVANCEMENTS = Component.translatable("gui.advancements");
   private static final Component STATS = Component.translatable("gui.stats");
   private static final Component SEND_FEEDBACK = Component.translatable("menu.sendFeedback");
   private static final Component REPORT_BUGS = Component.translatable("menu.reportBugs");
   private static final Component OPTIONS = Component.translatable("menu.options");
   private static final Component SHARE_TO_LAN = Component.translatable("menu.shareToLan");
   private static final Component PLAYER_REPORTING = Component.translatable("menu.playerReporting");
   private static final Component RETURN_TO_MENU = Component.translatable("menu.returnToMenu");
   private static final Component DISCONNECT = Component.translatable("menu.disconnect");
   private static final Component SAVING_LEVEL = Component.translatable("menu.savingLevel");
   private static final Component GAME = Component.translatable("menu.game");
   private static final Component PAUSED = Component.translatable("menu.paused");
   private final boolean showPauseMenu;
   
   private LogoRenderer logoRenderer;
   @Nullable
   private Button disconnectButton;

   public PauseScreen(boolean p_96308_) {
      super(p_96308_ ? GAME : PAUSED);
      this.showPauseMenu = p_96308_;
      
   }

   protected void init() {
	   this.logoRenderer = new LogoRenderer(false);
	      
      if (this.showPauseMenu) {
         this.createPauseMenu();
      }



   }

   private void createPauseMenu() {
      GridLayout gridlayout = new GridLayout();
      gridlayout.defaultCellSetting().padding(4, 4, 4, 0);
      GridLayout.RowHelper gridlayout$rowhelper = gridlayout.createRowHelper(1);
      
      gridlayout$rowhelper.addChild(Button.builder(RETURN_TO_GAME, (p_280814_) -> {
         this.minecraft.setScreen((Screen)null);
         this.minecraft.mouseHandler.grabMouse();
      }).width(210).build(), 1, gridlayout.newCellSettings().paddingTop(50));
      
      gridlayout$rowhelper.addChild(this.openScreenButton(OPTIONS, () -> {
          return new OptionsScreen(this, this.minecraft.options);
       }));
      
      gridlayout$rowhelper.addChild(this.openScreenButton(ADVANCEMENTS, () -> {
         return new AdvancementsScreen(this.minecraft.player.connection.getAdvancements());
      }));
      


      if (this.minecraft.hasSingleplayerServer() && !this.minecraft.getSingleplayerServer().isPublished()) {
         gridlayout$rowhelper.addChild(this.openScreenButton(SHARE_TO_LAN, () -> {
            return new ShareToLanScreen(this);
         }));
      }

      Component component = this.minecraft.isLocalServer() ? RETURN_TO_MENU : DISCONNECT;
      
      this.disconnectButton = gridlayout$rowhelper.addChild(Button.builder(component, (p_280815_) -> {
         p_280815_.active = false;
         this.minecraft.getReportingContext().draftReportHandled(this.minecraft, this, this::onDisconnect, true);
      }).width(210).build(), 1);
      
      gridlayout.arrangeElements();
      FrameLayout.alignInRectangle(gridlayout, 0, 112, this.width, this.height, 0, 0.5F);
      gridlayout.visitWidgets(this::addRenderableWidget);
   }

   private void onDisconnect() {
      boolean flag = this.minecraft.isLocalServer();
      boolean flag1 = this.minecraft.isConnectedToRealms();
      this.minecraft.level.disconnect();
      if (flag) {
         this.minecraft.clearLevel(new GenericDirtMessageScreen(SAVING_LEVEL));
      } else {
         this.minecraft.clearLevel();
      }

      TitleScreen titlescreen = new TitleScreen();
      if (flag) {
         this.minecraft.setScreen(titlescreen);
      } else if (flag1) {
         this.minecraft.setScreen(new RealmsMainScreen(titlescreen));
      } else {
         this.minecraft.setScreen(new JoinMultiplayerScreen(titlescreen));
      }

   }

   public void tick() {
      super.tick();
   }

   public void render(GuiGraphics gfx, int p_281431_, int p_283183_, float p_281435_) {
      if (this.showPauseMenu) {
         this.renderBackground(gfx);
      }
      this.logoRenderer.renderLogo(gfx, this.width, 2);
      super.render(gfx, p_281431_, p_283183_, p_281435_);
      if (this.showPauseMenu && this.minecraft != null && this.minecraft.getReportingContext().hasDraftReport() && this.disconnectButton != null) {
         gfx.blit(AbstractWidget.WIDGETS_LOCATION, this.disconnectButton.getX() + this.disconnectButton.getWidth() - 17, this.disconnectButton.getY() + 3, 182, 24, 15, 15);
      }

   }

   private Button openScreenButton(Component p_262567_, Supplier<Screen> p_262581_) {
      return Button.builder(p_262567_, (p_280817_) -> {
         this.minecraft.setScreen(p_262581_.get());
      }).width(210).build();
   }
}