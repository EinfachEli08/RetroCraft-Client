package net.minecraft.client.gui.components;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Arrays;
import java.util.Collections;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ControllerHintOverlay {

    private static final ResourceLocation BUTTONS_SHEET = new ResourceLocation("textures/gui/buttons.png");
    private static final int ICON_SIZE = 16;
    private static final int ICONS_TOTAL = 16;
    private Minecraft minecraft;
    private Font font;
    private int totalWidth = 0;
    private List<String> hintSwitch = Arrays.asList(new String[ICONS_TOTAL]);

    public ControllerHintOverlay(Minecraft minecraft) {
        this.minecraft = minecraft;
        this.font = minecraft.font;
    }

    public void render(GuiGraphics gfx) {
        int k = gfx.guiHeight(); //window height
        int l = k / 9; //small portion of window height, used for the small space gap below
        int y = k - l; //window height minus space gap
        totalWidth = 0; //reset back to start every loop

        //interate through possible button strings, since every button can only appear once.
        //if button string is null, dont show. if not null, show with string
        for (int i = 0; i < ICONS_TOTAL; i++){
            if(hintSwitch.get(i) != null) drawTip(gfx, hintSwitch.get(i), l + totalWidth, y, i);
        }
    }
    private void drawTip(GuiGraphics gfx, String s, float posX, float posY, int button){
        //gfx.setColor(1.0F, 1.0F, 1.0F, this.alpha);
        gfx.blit(BUTTONS_SHEET, (int)posX, (int)posY, (button > 7 ? button  - 8 : button)*ICON_SIZE, button > 7 ? ICON_SIZE : 0, ICON_SIZE, ICON_SIZE,128,128);
        //gfx.blit(BUTTONS_SHEET, (int)posX-ICON_SIZE/2, (int)posY-ICON_SIZE/2, (button > 7 ? button - 8: button)*32, button > 7 ? ICON_SIZE*2 : 0, 32, 32,256,256);
        //gfx.setColor(1.0F, 1.0F, 1.0F, 1F);
        gfx.drawString(this.font, s, (int)posX + 20, (int)posY+ICON_SIZE/4, 14737632, true);
        totalWidth += this.font.width(s) + 30;
    }
    public void clearSwitch(){ Collections.fill(hintSwitch, null); }
    public void setSwitch(int index, String value){
        hintSwitch.set(index, value);
    }
}
