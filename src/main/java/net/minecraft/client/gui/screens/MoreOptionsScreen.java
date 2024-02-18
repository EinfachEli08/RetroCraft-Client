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
public class MoreOptionsScreen extends OptionsSubScreen {
    private static final Component SKIN_CUSTOMIZATION = Component.translatable("options.skinCustomisation");
    private static final Component SOUNDS = Component.translatable("options.sounds");
    private static final Component VIDEO = Component.translatable("options.video");
    private static final Component CONTROLS = Component.translatable("options.controls");
    private static final Component LANGUAGE = Component.translatable("options.language");
    private static final Component CHAT = Component.translatable("options.chat.title");
    private static final Component RESOURCEPACK = Component.translatable("options.resourcepack");
    private static final Component ACCESSIBILITY = Component.translatable("options.accessibility.title");
    private static final Component TELEMETRY = Component.translatable("options.telemetry");
    private static final Component CREDITS_AND_ATTRIBUTION = Component.translatable("options.credits_and_attribution");

    private static final Component OPTIONS = Component.translatable("menu.options");

    private final Screen lastScreen;
    private final Options options;
    private CycleButton<Difficulty> difficultyButton;
    private LockIconButton lockButton;
    private LogoRenderer logoRenderer;

    public MoreOptionsScreen(Screen p_96242_, Options p_96243_) {
        super(p_96242_,p_96243_,Component.translatable("options.title"));
        this.lastScreen = p_96242_;
        this.options = p_96243_;
    }

    protected void init() {

        this.logoRenderer = new LogoRenderer(false);

        GridLayout gridlayout = new GridLayout();
        gridlayout.defaultCellSetting().padding(4, 4, 4, 0);
        GridLayout.RowHelper gridlayout$rowhelper = gridlayout.createRowHelper(1);

        gridlayout$rowhelper.addChild(this.options.fov().createButton(this.minecraft.options, 0, 0, 210));
        gridlayout$rowhelper.addChild(this.createOnlineButton());

        gridlayout$rowhelper.addChild(this.openScreenButton(SOUNDS, () -> {
            return new SoundOptionsScreen(this, this.options);
        }));

        gridlayout$rowhelper.addChild(this.openScreenButton(VIDEO, () -> {
            return new VideoSettingsScreen(this, this.options);
        }));


        gridlayout$rowhelper.addChild(this.openScreenButton(CHAT, () -> {
         return new IngameOptionsScreen(this, this.options);
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
        FrameLayout.alignInRectangle(gridlayout, 0, 112, this.width, this.height, 0.5F, 0);
        gridlayout.visitWidgets(this::addRenderableWidget);
    }

    private void applyPacks(PackRepository p_275714_) {
        this.options.updateResourcePacks(p_275714_);
        this.minecraft.setScreen(this);
    }

    private LayoutElement createOnlineButton() {
        if (this.minecraft.level != null && this.minecraft.hasSingleplayerServer()) {
            this.difficultyButton = createDifficultyButton(0, 0, "options.difficulty", this.minecraft);
            if (!this.minecraft.level.getLevelData().isHardcore()) {
                this.lockButton = new LockIconButton(0, 0, (p_280806_) -> {
                    this.minecraft.setScreen(new ConfirmScreen(this::lockCallback, Component.translatable("difficulty.lock.title"), Component.translatable("difficulty.lock.question", this.minecraft.level.getLevelData().getDifficulty().getDisplayName())));
                });
                this.difficultyButton.setWidth(210);
                this.lockButton.setLocked(this.minecraft.level.getLevelData().isDifficultyLocked());
                this.lockButton.active = !this.lockButton.isLocked();
                this.difficultyButton.active = !this.lockButton.isLocked();
                GridLayout gl = new GridLayout(210, 0);
                gl.defaultCellSetting().padding(0, 4, 4, 0);
                GridLayout.RowHelper gridlayout$rowhelper = gl.createRowHelper(1);
                gridlayout$rowhelper.addChild(this.difficultyButton);
                gridlayout$rowhelper.addChild(this.lockButton);
                return gridlayout$rowhelper.getGrid();
            } else {
                this.difficultyButton.active = false;
                return this.difficultyButton;
            }
        } else {
            return Button.builder(Component.translatable("options.online"), (p_280805_) -> {
                this.minecraft.setScreen(OnlineOptionsScreen.createOnlineOptionsScreen(this.minecraft, this, this.options));
            }).bounds(this.width / 2 + 5, this.height / 6 - 12 + 24, 210, 20).build();
        }
    }

    public static CycleButton<Difficulty> createDifficultyButton(int p_262051_, int p_261805_, String p_261598_, Minecraft p_261922_) {
        return CycleButton.builder(Difficulty::getDisplayName).withValues(Difficulty.values()).withInitialValue(p_261922_.level.getDifficulty()).create(p_262051_, p_261805_, 150, 20, Component.translatable(p_261598_), (p_193854_, p_193855_) -> {
            p_261922_.getConnection().send(new ServerboundChangeDifficultyPacket(p_193855_));
        });
    }

    private void lockCallback(boolean p_96261_) {
        this.minecraft.setScreen(this);
        if (p_96261_ && this.minecraft.level != null) {
            this.minecraft.getConnection().send(new ServerboundLockDifficultyPacket(true));
            this.lockButton.setLocked(true);
            this.lockButton.active = false;
            this.difficultyButton.active = false;
        }

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