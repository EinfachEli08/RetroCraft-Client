package net.minecraft.client.gui.screens.worldselection;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.TextAndImageButton;
import net.minecraft.client.gui.components.tabs.GridLayoutTab;
import net.minecraft.client.gui.components.tabs.TabManager;
import net.minecraft.client.gui.components.tabs.TabNavigationBar;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class SelectGameScreen extends Screen {

    public static final ResourceLocation MENU_LOCATION = new ResourceLocation("textures/gui/container/menu/map_select.png");

    private static final int TEXTURE_WIDTH = 207;
    private static final int TEXTURE_HEIGHT = 163;
    private static final int TAB_HEIGHT = 21;
    @Nullable
    private final Screen lastScreen;
    @Nullable
    private GridLayout bottomButtons;
    @Nullable
    private TabNavigationBar tabNavigationBar;
    private int x;
    private int y;
    private int myWidth;
    private int myHeight;

    private final TabManager tabManager = new TabManager(this::addRenderableWidget, (p_267853_) -> {
        this.removeWidget(p_267853_);
    });

    public SelectGameScreen(Screen screen){
        super(Component.translatable("test"));
        this.lastScreen = screen;
    }

    protected void init(){
        this.tabNavigationBar = TabNavigationBar.builder(this.tabManager, this.width)
                .addTabs(new SelectGameScreen.LoadTab(),
                        new SelectGameScreen.CreateTab(),
                        new SelectGameScreen.JoinTab()).build();
        this.addRenderableWidget(this.tabNavigationBar);

        this.bottomButtons = (new GridLayout()).columnSpacing(4);
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
        int scale = (int)(this.minecraft.options.guiScale().get()*0.8F);
        this.myWidth = TEXTURE_WIDTH *scale;
        this.myHeight = TEXTURE_HEIGHT *scale;
        this.x = this.width / 2 - myWidth / 2;
        this.y = this.height / 2 - myHeight / 2;

        if (this.tabNavigationBar != null && this.bottomButtons != null) {
            this.tabNavigationBar.setWidth(this.myWidth);
            this.tabNavigationBar.arrangeElements();
            this.bottomButtons.arrangeElements();
            FrameLayout.alignInRectangle(this.bottomButtons, this.x, this.y+5, TEXTURE_WIDTH, TAB_HEIGHT, 0.5F, 0);
            ScreenRectangle screenrectangle = new ScreenRectangle(x, y-TAB_HEIGHT, 0, 0);
            this.tabManager.setTabArea(screenrectangle);
        }
    }
    public void render(GuiGraphics gfx, int mousex, int mousey, float p_282251_) {
        this.minecraft.panorama.render(gfx, this.height);
        gfx.blit(MENU_LOCATION, x, y, myWidth, myHeight, 0.0F, 0.0F, 64, 256, 64, 256);

        super.render(gfx, width, height, p_282251_);
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
