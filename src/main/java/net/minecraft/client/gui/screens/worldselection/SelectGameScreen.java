package net.minecraft.client.gui.screens.worldselection;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.TextAndImageButton;
import net.minecraft.client.gui.components.tabs.GridLayoutTab;
import net.minecraft.client.gui.components.tabs.TabManager;
import net.minecraft.client.gui.components.tabs.TabNavigationBar;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.OptionsScreen;
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
    public static final ResourceLocation SPRITE_LOCATION = new ResourceLocation("textures/gui/icons.png");

    private static final int TEXTURE_WIDTH = 223;
    private static final int TEXTURE_HEIGHT = 179;
    private static final int OFFSET_WIDTH = 8;
    private static final int OFFSET_HEIGHT = 30;
    private static final int TAB_OFFSET = 11;
    private static final int MENU_OFFSET = 40;
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
    private float scale;

    private final TabManager tabManager = new TabManager(this::addRenderableWidget, (p_267853_) -> {
        this.removeWidget(p_267853_);
    });

    public SelectGameScreen(Screen screen){
        super(Component.translatable("test"));
        this.lastScreen = screen;
    }

    protected void init(){
        this.tabNavigationBar = TabNavigationBar.builder(this.tabManager, this.width)
                .addTabs(new SelectGameScreen.ServerTab(minecraft),
                        new SelectGameScreen.WorldTab(minecraft),
                        new SelectGameScreen.RealmTab(minecraft)).build();
        this.addRenderableWidget(this.tabNavigationBar);
        this.tabNavigationBar.selectTab(1, false);

        this.bottomButtons = (new GridLayout()).columnSpacing(0);
        GridLayout.RowHelper gridlayout$rowhelper = this.bottomButtons.createRowHelper(1);
        gridlayout$rowhelper.addChild(Button.builder(CommonComponents.GUI_CANCEL, (p_232903_) -> {
            this.popScreen();
        }).build());
        this.bottomButtons.visitWidgets((p_267851_) -> {
            p_267851_.setTabOrderGroup(1);
            this.addRenderableWidget(p_267851_);
        });
        this.repositionElements();
    }

    public void repositionElements() {
        this.scale = this.minecraft.options.guiScale().get() * 0.7F;
        this.myWidth = (int)(TEXTURE_WIDTH *scale);
        this.myHeight = (int)(TEXTURE_HEIGHT *scale)+OFFSET_HEIGHT*2;
        this.x = this.width / 2 - myWidth / 2;
        this.y = this.height / 2 - myHeight / 2;

        if (this.tabNavigationBar != null && this.bottomButtons != null) {
            this.tabNavigationBar.setWidth(this.myWidth+1000);
            this.tabNavigationBar.arrangeElements((int)(x+13), (int)(y+TAB_OFFSET));
            this.bottomButtons.arrangeElements();
            FrameLayout.centerInRectangle(this.bottomButtons, this.width/4, this.height-36, TEXTURE_WIDTH, 0);
            this.tabManager.setTabArea(x+OFFSET_WIDTH*2, y+MENU_OFFSET, myWidth, myHeight);
        }
    }
    public void render(GuiGraphics gfx, int mousex, int mousey, float p_282251_) {
        this.minecraft.panorama.render(gfx, this.height);
        this.tabNavigationBar.render(gfx);
        gfx.blit(MENU_LOCATION, x+OFFSET_WIDTH, y+OFFSET_HEIGHT, myWidth, myHeight, 0.0F, 0.0F, 64, 256, 64, 256);

        super.render(gfx, width, height, p_282251_);
    }
    public void onClose() {
        this.popScreen();
    }
    public void popScreen() {
        this.minecraft.setScreen(this.lastScreen);
    }

    protected void openScreenButton(Component text, ResourceLocation image, int xOff, int yOff, Supplier<Screen> p_262119_){
        callButton(text, image, xOff, yOff, (p_280808_) -> {
            this.minecraft.setScreen(p_262119_.get());
        });
    }
    protected TextAndImageButton callButton(Component text, ResourceLocation image, int xOff, int yOff, Button.OnPress p_254567_) {
        return TextAndImageButton.builder(text, image, p_254567_)
                .texStart(0, 0)
                .offset(3, 3)
                .yDiffTex(0)
                .usedTextureSize(20,  20)
                .textureSize(256, 256)
                .buttonSize(240, 26)
                .stringX(10)
                .build();
    }
    @OnlyIn(Dist.CLIENT)
    class WorldTab extends GridLayoutTab {
        private static final Component TITLE = Component.translatable("menu.worlds");

        WorldTab(Minecraft minecraft) {
            super(TITLE);
            GridLayout.RowHelper gridlayout$rowhelper = this.layout.createRowHelper(1);
            gridlayout$rowhelper.addChild(callButton(Component.translatable("menu.singleplayer"), SPRITE_LOCATION, 0, 0, (p_280808_) -> {}));
        }
    }
    @OnlyIn(Dist.CLIENT)
    class ServerTab extends GridLayoutTab {
        private static final Component TITLE = Component.translatable("menu.servers");

        ServerTab(Minecraft minecraft) {
            super(TITLE);
        }
    }
    @OnlyIn(Dist.CLIENT)
    class RealmTab extends GridLayoutTab {
        private static final Component TITLE = Component.translatable("menu.realms");

        RealmTab(Minecraft minecraft) {
            super(TITLE);
        }
    }
    public void tick() {
        this.tabManager.tickCurrent();
    }
}
