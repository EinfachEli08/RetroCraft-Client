package net.minecraft.client.gui.screens.worldselection;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Lifecycle;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.TextAndImageButton;
import net.minecraft.client.gui.components.tabs.GridLayoutTab;
import net.minecraft.client.gui.components.tabs.TabManager;
import net.minecraft.client.gui.components.tabs.TabNavigationBar;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.RegistryLayer;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.levelgen.WorldDimensions;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class SelectGameScreen extends Screen {

    public static final ResourceLocation MENU_LOCATION = new ResourceLocation("textures/gui/container/menu/map_select.png");

    private static final int TEXTURE_WIDTH = 207/4+207;
    private static final int TEXTURE_HEIGHT = 163/4+163;
    private static final int TAB_HEIGHT = 21;
    @Nullable
    private final Screen lastScreen;
    @Nullable
    private GridLayout bottomButtons;
    @Nullable
    private TabNavigationBar tabNavigationBar;
    private int x;
    private int y;

    private final TabManager tabManager = new TabManager(this::addRenderableWidget, (p_267853_) -> {
        this.removeWidget(p_267853_);
    });

    public SelectGameScreen(Screen screen){
        super(Component.translatable("test"));
        this.lastScreen = screen;
    }

    public void init(){
        this.tabNavigationBar = TabNavigationBar.builder(this.tabManager, this.width)
                .addTabs(new SelectGameScreen.LoadTab(), new SelectGameScreen.CreateTab(), new SelectGameScreen.JoinTab()).build();
        this.addRenderableWidget(this.tabNavigationBar);

        this.bottomButtons = (new GridLayout()).columnSpacing(10);
        GridLayout.RowHelper gridlayout$rowhelper = this.bottomButtons.createRowHelper(1);
        gridlayout$rowhelper.addChild(Button.builder(CommonComponents.GUI_CANCEL, (p_232903_) -> {
            this.popScreen();
        }).build());
        this.bottomButtons.visitWidgets((p_267851_) -> {
            p_267851_.setTabOrderGroup(1);
            this.addRenderableWidget(p_267851_);
        });
        this.tabNavigationBar.selectTab(0, false);
        this.repositionElements();
    }

    public void repositionElements() {
        if (this.tabNavigationBar != null && this.bottomButtons != null) {
            this.tabNavigationBar.setWidth(TEXTURE_WIDTH);
            this.tabNavigationBar.arrangeElements();
            this.bottomButtons.arrangeElements();
            FrameLayout.alignInRectangle(this.bottomButtons, 0, y-TAB_HEIGHT, TEXTURE_WIDTH, TAB_HEIGHT, 0.5F, 0);
            ScreenRectangle screenrectangle = new ScreenRectangle(x, y, TEXTURE_WIDTH, TEXTURE_HEIGHT);
            this.tabManager.setTabArea(screenrectangle);
        }
    }
    public void render(GuiGraphics gfx, int p_281262_, int p_283321_, float p_282251_){
        x = this.width / 2 - TEXTURE_WIDTH / 2;
        y = this.height / 2 - TEXTURE_HEIGHT / 2;

        this.renderBackground(gfx);
        gfx.blit(MENU_LOCATION, x, y, 0, 0, 207, 163, TEXTURE_WIDTH, TEXTURE_HEIGHT);

        super.render(gfx, p_281262_, p_283321_, p_282251_);
    }
    public void onClose() {
        this.popScreen();
    }
    public void popScreen() {
        this.minecraft.setScreen(this.lastScreen);
    }
    protected TextAndImageButton openScreenButton(Component text, ResourceLocation image, int xOff, int yOff, Supplier<Screen> p_262119_) {
        return TextAndImageButton.builder(text, image, (p_280808_) -> {
                    this.minecraft.setScreen(p_262119_.get());
                }).texStart(3, 109)
                .offset(65, 3)
                .yDiffTex(20)
                .usedTextureSize(14, 14)
                .textureSize(256, 256).build();
    }
    @OnlyIn(Dist.CLIENT)
    class LoadTab extends GridLayoutTab {
        private static final Component TITLE = Component.translatable("test");

        LoadTab() {
            super(TITLE);
            GridLayout.RowHelper gridlayout$rowhelper = this.layout.createRowHelper(1);
            //gridlayout$rowhelper.addChild(openScreenButton(Component.translatable("menu.singleplayer"), () -> new SelectWorldScreen(this)));

        }
    }
    @OnlyIn(Dist.CLIENT)
    class CreateTab extends GridLayoutTab {
        private static final Component TITLE = Component.translatable("test");

        CreateTab() {
            super(TITLE);
        }
    }
    @OnlyIn(Dist.CLIENT)
    class JoinTab extends GridLayoutTab {
        private static final Component TITLE = Component.translatable("test");

        JoinTab() {
            super(TITLE);
        }
    }
    public void tick() {
        this.tabManager.tickCurrent();
    }
}
