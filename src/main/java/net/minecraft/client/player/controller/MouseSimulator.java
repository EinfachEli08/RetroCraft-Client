package net.minecraft.client.player.controller;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.platform.DisplayData;
import com.mojang.blaze3d.platform.Window;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;


public class MouseSimulator {

	static int screenX = 0;
	static int screenY = 0;
	
	static int screenWidth = DisplayData.width;
	static int screenHeight = DisplayData.height;
	static float Xposition = 0;
	static float Yposition = 0;
	static float sensitivity = 12;
	
	public static float getXPosition() {
	    float horizontalAxis = ControllerInput.getControllerStickAxis(3);
	    float newXPosition = Xposition - (sensitivity * horizontalAxis);
	    
	    if (newXPosition < 0) {
	        Xposition = 0;
	    } else if (newXPosition > screenWidth) {
	        Xposition = screenWidth;
	    } else {
	        Xposition = newXPosition;
	    }
	    
	    return Xposition;
	}

	public static float getYPosition() {
	    float verticalAxis = ControllerInput.getControllerStickAxis(2);
	    float newYPosition = Yposition - (sensitivity * verticalAxis);
	    
	    if (newYPosition < 0) {
	        Yposition = 0;
	    } else if (newYPosition > screenHeight) {
	        Yposition = screenHeight;
	    } else {
	        Yposition = newYPosition;
	    }
	    
	    return Yposition;
	}
	
	public static void drawMouseCursor(GuiGraphics gfx,boolean visible, int size){
		
		 float verticalAxis = ControllerInput.getControllerStickAxis(2);
		 float newYPosition = Yposition - (sensitivity * verticalAxis);
		 float horizontalAxis = ControllerInput.getControllerStickAxis(3);
		 float newXPosition = Xposition - (sensitivity * horizontalAxis);
		    
		 if (newYPosition < 0) {
		     Yposition = 0;
		 } else if (newYPosition > screenHeight) {
		     Yposition = screenHeight;
		 } else {
		     Yposition = newYPosition;
		 }
		    
		 if (newXPosition < 0) {
		     Xposition = 0;
		 } else if (newXPosition > screenWidth) {
		     Xposition = screenWidth;
		 } else {
		     Xposition = newXPosition;
		 }
		 
		
	        
	        // Render the cursor
	        gfx.blit(new ResourceLocation("textures/gui/icons.png"), (int)Yposition, (int)Xposition, 0, 0, size, size);
	        
	        
		
	}
}
