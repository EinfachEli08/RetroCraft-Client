package net.minecraft.client.gui.screens;

import com.mojang.authlib.minecraft.BanDetails;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.gui.screens.RealmsNotificationsScreen;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.LogoRenderer;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.components.PlainTextButton;
import net.minecraft.client.gui.components.SplashRenderer;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.screens.controls.ControlsScreen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.multiplayer.SafetyScreen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.client.gui.screens.worldselection.SelectGameScreen;
import net.minecraft.client.player.controller.MouseSimulator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TitleScreen extends Screen {
   @Nullable
   private SplashRenderer splash;
   @Nullable
   private RealmsNotificationsScreen realmsNotificationsScreen;
   private final boolean fading;
   private long fadeInStart;
   @Nullable
   private TitleScreen.WarningLabel warningLabel;
   private final LogoRenderer logoRenderer;

   public TitleScreen() {
      this(false);
   }

   public TitleScreen(boolean p_96733_) {
      this(p_96733_, (LogoRenderer)null);
   }

   public TitleScreen(boolean p_265779_, @Nullable LogoRenderer p_265067_) {
      super(Component.translatable("narrator.screen.title"));
      this.fading = p_265779_;
      this.logoRenderer = Objects.requireNonNullElseGet(p_265067_, () -> {
         return new LogoRenderer(false);
      });
   }

   private boolean realmsNotificationsEnabled() {
      return this.realmsNotificationsScreen != null;
   }

   public void tick() {
      if (this.realmsNotificationsEnabled()) {
         this.realmsNotificationsScreen.tick();
      }

      //this.minecraft.getRealms32BitWarningStatus().showRealms32BitWarningIfNeeded(this);
   }

   public static CompletableFuture<Void> preloadResources(TextureManager p_96755_, Executor p_96756_) {
      return CompletableFuture.allOf(p_96755_.preload(LogoRenderer.MINECRAFT_LOGO, p_96756_), p_96755_.preload(LogoRenderer.MINECRAFT_EDITION, p_96756_));
   }

   public boolean isPauseScreen() {
      return false;
   }

   public boolean shouldCloseOnEsc() {
      return false;
   }

   protected void init() {
      if (this.splash == null) {
         this.splash = this.minecraft.getSplashManager().getSplash();
      }

      GridLayout gridLayout = new GridLayout();
      gridLayout.defaultCellSetting().padding(4, 4, 4, 0);
      GridLayout.RowHelper gridlayout$rowhelper = gridLayout.createRowHelper(1);

      gridlayout$rowhelper.addChild(this.openScreenButton(Component.translatable("menu.singleplayer"), () -> new SelectWorldScreen(this)));
      gridlayout$rowhelper.addChild(this.openScreenButton(Component.translatable("menu.multiplayer"), () -> {
         Screen screen = this.minecraft.options.skipMultiplayerWarning ? new JoinMultiplayerScreen(this) : new SafetyScreen(this);
         return screen;
      }));
      gridlayout$rowhelper.addChild(this.openScreenButton(Component.translatable("menu.online"), () -> new RealmsMainScreen(this)));
      gridlayout$rowhelper.addChild(this.openScreenButton(Component.translatable("menu.options"), () -> new OptionsScreen(this, this.minecraft.options)));
      gridlayout$rowhelper.addChild(this.openScreenButton(Component.translatable("narrator.button.language"), () -> new LanguageSelectScreen(this, this.minecraft.options, this.minecraft.getLanguageManager())));
      gridlayout$rowhelper.addChild(this.openScreenButton(Component.translatable("narrator.button.accessibility"), () -> new AccessibilityOptionsScreen(this, this.minecraft.options)));

      gridlayout$rowhelper.addChild(this.openScreenButton(Component.translatable("test"), () -> new SelectGameScreen(this)));

      gridlayout$rowhelper.addChild(this.openScreenButton(Component.translatable("menu.quit"), () -> {
         this.minecraft.stop();
         return null;
      }));

      gridLayout.arrangeElements();
      FrameLayout.alignInRectangle(gridLayout, 0, 112, this.width, this.height, 0.5F, 0);
      gridLayout.visitWidgets(this::addRenderableWidget);

      this.minecraft.setConnectedToRealms(false);

      if (this.realmsNotificationsScreen == null) {
         this.realmsNotificationsScreen = new RealmsNotificationsScreen();
      }

      if (this.realmsNotificationsEnabled()) {
         this.realmsNotificationsScreen.init(this.minecraft, this.width, this.height);
      }

      /*if (!this.minecraft.is64Bit()) {
         this.warningLabel = new TitleScreen.WarningLabel(this.font, MultiLineLabel.create(this.font, Component.translatable("title.32bit.deprecation"), 350, 2), this.width / 2, l - 24);
      }*/

      this.minecraft.controllerHint.clearSwitch();
      this.minecraft.controllerHint.setSwitch(0, "Select");
      this.minecraft.controllerHint.setSwitch(1, "Back");

   }
/*
   private void createNormalMenuOptions(int p_96764_, int p_96765_, int p_69696_, int p_42042_) {

      this.addRenderableWidget(Button.builder(Component.translatable("menu.singleplayer"), (p_280832_) -> {
         this.minecraft.setScreen(new SelectWorldScreen(this));
      }).bounds(this.width / 2 - 100, p_96764_, p_69696_, p_42042_).build());

      Component component = this.getMultiplayerDisabledReason();
      boolean flag = component == null;
      Tooltip tooltip = component != null ? Tooltip.create(component) : null;

      (this.addRenderableWidget(Button.builder(Component.translatable("menu.multiplayer"), (p_280833_) -> {
         Screen screen = (Screen)(this.minecraft.options.skipMultiplayerWarning ? new JoinMultiplayerScreen(this) : new SafetyScreen(this));
         this.minecraft.setScreen(screen);
      }).bounds(this.width / 2 - 100, p_96764_ + p_96765_ * 1, p_69696_, p_42042_).tooltip(tooltip).build())).active = flag;

      (this.addRenderableWidget(Button.builder(Component.translatable("menu.online"), (p_210872_) -> {
         this.realmsButtonClicked();
      }).bounds(this.width / 2 - 100, p_96764_ + p_96765_ * 2, p_69696_, p_42042_).tooltip(tooltip).build())).active = flag;

      this.addRenderableWidget(Button.builder(Component.translatable("menu.options"), (p_280838_) -> {
         this.minecraft.setScreen(new OptionsScreen(this, this.minecraft.options));
      }).bounds(this.width / 2 - 100, p_96764_ + p_96765_ * 3, p_69696_, p_42042_).build());

      this.addRenderableWidget(Button.builder(Component.translatable("narrator.button.language"), (p_280838_) -> {
         this.minecraft.setScreen(new LanguageSelectScreen(this, this.minecraft.options, this.minecraft.getLanguageManager()));
      }).bounds(this.width / 2 - 100, p_96764_ + p_96765_ * 4, p_69696_, p_42042_).build());

      this.addRenderableWidget(Button.builder(Component.translatable("narrator.button.accessibility"), (p_280838_) -> {
         this.minecraft.setScreen(new AccessibilityOptionsScreen(this, this.minecraft.options));
      }).bounds(this.width / 2 - 100, p_96764_ + p_96765_ * 5, p_69696_, p_42042_).build());

      this.addRenderableWidget(Button.builder(Component.translatable("menu.quit"), (p_280831_) -> {
         this.minecraft.stop();
      }).bounds(this.width / 2 - 100, p_96764_ + p_96765_ * 6, p_69696_, p_42042_).build());
   }*/

   @Nullable
   private Component getMultiplayerDisabledReason() {
      if (this.minecraft.allowsMultiplayer()) {
         return null;
      } else {
         BanDetails bandetails = this.minecraft.multiplayerBan();
         if (bandetails != null) {
            return bandetails.expires() != null ? Component.translatable("title.multiplayer.disabled.banned.temporary") : Component.translatable("title.multiplayer.disabled.banned.permanent");
         } else {
            return Component.translatable("title.multiplayer.disabled");
         }
      }
   }

   private void realmsButtonClicked() {
      this.minecraft.setScreen(new RealmsMainScreen(this));
   }

   public void render(GuiGraphics gfx, int p_281753_, int p_283539_, float p_282628_) {
      /*
      if (this.fadeInStart == 0L && this.fading) {
         this.fadeInStart = Util.getMillis();
      }

      float f = this.fading ? (float)(Util.getMillis() - this.fadeInStart) / 1000.0F : 1.0F;
      float f1 = this.fading ? Mth.clamp(f - 1.0F, 0.0F, 1.0F) : 1.0F;
*/
      this.renderBackground(gfx);

      this.logoRenderer.renderLogo(gfx, this.width, 1);

      /*int i = Mth.ceil(f1 * 255.0F) << 24;
      if ((i & -67108864) != 0) {
         if (this.warningLabel != null) {
            this.warningLabel.render(gfx, 1F);
         }*/

         if (this.splash != null) {
            this.splash.render(gfx, this.width, this.font, 1);
         }

         /*for(GuiEventListener guieventlistener : this.children()) {
            if (guieventlistener instanceof AbstractWidget) {
               ((AbstractWidget)guieventlistener).setAlpha(f1);
            }
         }*/

         super.render(gfx, p_281753_, p_283539_, p_282628_);
         //if (this.realmsNotificationsEnabled() && f1 >= 1.0F) {
            RenderSystem.enableDepthTest();
            this.realmsNotificationsScreen.render(gfx, p_281753_, p_283539_, p_282628_);
         //}

      //}
   }

   public boolean mouseClicked(double p_96735_, double p_96736_, int p_96737_) {
      if (super.mouseClicked(p_96735_, p_96736_, p_96737_)) {
         return true;
      } else {
         return this.realmsNotificationsEnabled() && this.realmsNotificationsScreen.mouseClicked(p_96735_, p_96736_, p_96737_);
      }
   }

   public void removed() {
      if (this.realmsNotificationsScreen != null) {
         this.realmsNotificationsScreen.removed();
      }

   }

   public void added() {
      super.added();
      if (this.realmsNotificationsScreen != null) {
         this.realmsNotificationsScreen.added();
      }

   }


   @OnlyIn(Dist.CLIENT)
   static record WarningLabel(Font font, MultiLineLabel label, int x, int y) {
      public void render(GuiGraphics p_281783_, int p_281383_) {
         this.label.renderBackgroundCentered(p_281783_, this.x, this.y, 9, 2, 2097152 | Math.min(p_281383_, 1426063360));
         this.label.renderCentered(p_281783_, this.x, this.y, 9, 16777215 | p_281383_);
      }
   }

   private Button openScreenButton(Component p_261565_, Supplier<Screen> p_262119_) {
      return Button.builder(p_261565_, (p_280808_) -> {
         this.minecraft.setScreen(p_262119_.get());
      }).width(210).build();
   }
}