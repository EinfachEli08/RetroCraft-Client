package net.minecraft.client.gui.screens;

import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.LockIconButton;
import net.minecraft.client.gui.components.LogoRenderer;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.layouts.SpacerElement;
import net.minecraft.client.gui.screens.controls.ControlsScreen;
import net.minecraft.client.gui.screens.packs.PackSelectionScreen;
import net.minecraft.client.gui.screens.telemetry.TelemetryInfoScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundChangeDifficultyPacket;
import net.minecraft.network.protocol.game.ServerboundLockDifficultyPacket;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.world.Difficulty;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class IngameOptionsScreen extends OptionsSubScreen {

    private static final Component CHAT = Component.translatable("options.chat.title");
    private static final Component RESOURCEPACK = Component.translatable("options.resourcepack");

    private final Screen lastScreen;
    private final Options options;
    private LogoRenderer logoRenderer;

    public IngameOptionsScreen(Screen p_96242_, Options p_96243_) {
        super(p_96242_,p_96243_,Component.translatable("options.title"));
        this.lastScreen = p_96242_;
        this.options = p_96243_;
    }

    protected void init() {

        this.logoRenderer = new LogoRenderer(false);

        GridLayout gridlayout = new GridLayout();
        gridlayout.defaultCellSetting().padding(4, 4, 4, 0);
        GridLayout.RowHelper gridlayout$rowhelper = gridlayout.createRowHelper(1);



        gridlayout$rowhelper.addChild(this.openScreenButton(CHAT, () -> {
            return new ChatOptionsScreen(this, this.options);
        }));
        gridlayout$rowhelper.addChild(this.openScreenButton(RESOURCEPACK, () -> {
            return new PackSelectionScreen(this.minecraft.getResourcePackRepository(), this::applyPacks, this.minecraft.getResourcePackDirectory(), Component.translatable("resourcePack.title"));
        }));



        gridlayout$rowhelper.addChild(Button.builder(CommonComponents.GUI_DONE, (p_280809_) -> {
            this.minecraft.setScreen(this.lastScreen);
        }).width(210).build(), 1);

      /*

      this shouldn't even exist...

      gridlayout$rowhelper.addChild(this.openScreenButton(TELEMETRY, () -> {
         return new TelemetryInfoScreen(this, this.options);
      }));

       */





        gridlayout.arrangeElements();
        FrameLayout.alignInRectangle(gridlayout, 0, 112, this.width, this.height, 0, 0.5F);
        gridlayout.visitWidgets(this::addRenderableWidget);
    }

    private void applyPacks(PackRepository p_275714_) {
        this.options.updateResourcePacks(p_275714_);
        this.minecraft.setScreen(this);
    }

    public void removed() {
        this.options.save();
    }

    public void render(GuiGraphics gfx, int p_281826_, int p_283378_, float p_281975_) {
        this.renderBackground(gfx);
        this.logoRenderer.renderLogo(gfx, this.width, 2);
        super.render(gfx, p_281826_, p_283378_, p_281975_);
    }

    private Button openScreenButton(Component p_261565_, Supplier<Screen> p_262119_) {
        return Button.builder(p_261565_, (p_280808_) -> {
            this.minecraft.setScreen(p_262119_.get());
        }).width(210).build();
    }
}