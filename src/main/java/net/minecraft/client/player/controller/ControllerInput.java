package net.minecraft.client.player.controller;

import net.minecraft.client.Options;
import net.minecraft.client.player.Input;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import org.lwjgl.glfw.GLFW;
import static org.lwjgl.glfw.GLFW.*;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

@OnlyIn(Dist.CLIENT)
public class ControllerInput extends Input{
	
	public static boolean up;
	public static boolean down;
	public static boolean left;
	public static boolean right;
	
	public float sc_upImpulse;
	public float sc_leftImpulse;
	
	int jumpingReleaseDelay = 5;
	
	private static boolean CRTL_DBG = false;
	private static boolean BTN_DBG = false;
	
	
	public ControllerInput(Options p_108580_) {
	}

	public void tick(boolean p_234118_, float p_234119_) {
		
		if (p_234118_) {
			this.leftImpulse *= p_234119_;
			this.forwardImpulse *= p_234119_;
		}

		if (getControllerConnected()) {
			
			this.jumping = getPressedButton(2);
			this.shiftKeyDown = getPressedButton(11);
			
			ControllerInput.up = getPressedButton(14);
			ControllerInput.right = getPressedButton(15);
			ControllerInput.down = getPressedButton(16);
			ControllerInput.left = getPressedButton(17);
			
					// Get joystick axes
			this.leftImpulse = getControllerStickAxis(0);	
			this.forwardImpulse = getControllerStickAxis(1);
			this.sc_upImpulse = getControllerStickAxis(2);
			this.sc_leftImpulse = getControllerStickAxis(3);
			
			
			
		}else {
			if(CRTL_DBG) {
				System.out.println("Controller 1 is not present!");
			}
		}
	}

	public static boolean getControllerConnected() {
		return glfwJoystickPresent(GLFW_JOYSTICK_1);
	}

	public static float getControllerStickAxis(int stick) {
		FloatBuffer axesBuffer = GLFW.glfwGetJoystickAxes(GLFW.GLFW_JOYSTICK_1);
		if (axesBuffer != null) {
			
			for (int i = 0; i < axesBuffer.remaining(); i++) {
				float axisValue = axesBuffer.get(i);
				
				
				if(i==stick) {
					return -axisValue;
				}
				
			}
		}
		return 0;
	}

	public static boolean getPressedButton(int button) {
		ByteBuffer buttonsBuffer = GLFW.glfwGetJoystickButtons(GLFW.GLFW_JOYSTICK_1);
		
		if (buttonsBuffer != null) {
			if(CRTL_DBG) {
				System.out.println("Joystick 1 Buttons:");
			}
			
			for (int i = 0; i < buttonsBuffer.remaining(); i++) {
				int state = buttonsBuffer.get(i) & 0xFF;

				if(i == button) {
					if (state == 1 ) {
						if(BTN_DBG) {
							System.out.println("button:"+state+" pressed:"+i);
						}
						
						return true;
						
						
					} else {
						if(BTN_DBG) {
							System.out.println("button:"+state+" pressed:"+i);
						}
						return false;
					}
				}
			}
		} else {
			if(CRTL_DBG) {
				System.out.println("Failed to get joystick buttons for Joystick 1");
			}
			return false;
		}
		return false;
	}
}