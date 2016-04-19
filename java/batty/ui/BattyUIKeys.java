/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package batty.ui;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
/**
 * Handles the detection of key-presses that Batty's Coordinates PLUS mod is interested in 
 * @author BatHeart
 */
public class BattyUIKeys {

	public static boolean keyToggleCoords = false;
	public static boolean keyMoveCoords = false;
	public static boolean keyCopyCoords = false; 
	public static boolean keyToggleTimerVis = false;
	public static boolean keyToggleTimerRun = false;
	public static boolean keyResetTimer = false;
	public static boolean keyMoveTimer = false;
	public static boolean keyToggleFPSVis = false;
	public static boolean keyMoveFPS = false;

	@SubscribeEvent
	public void trackKeyInputs(KeyInputEvent event) {

		if (BattyBaseUI.hideunhideCoordskey.isPressed()) {
			keyToggleCoords = true;
		} else if (BattyBaseUI.moveCoordScreenPos.isPressed()) {
			keyMoveCoords = true;
		} else if (BattyBaseUI.copyCoordsClipboard.isPressed()) {
			keyCopyCoords = true;			
        } else if (BattyBaseUI.hideunhideTimerkey.isPressed()) {
			keyToggleTimerVis = true;
		} else if (BattyBaseUI.startstopTimerkey.isPressed()) {
			keyToggleTimerRun = true;
		} else if (BattyBaseUI.resetTimerkey.isPressed()) {
			keyResetTimer = true;
		} else if (BattyBaseUI.moveTimerScreenPos.isPressed()){
			keyMoveTimer = true;
        } else if (BattyBaseUI.hideunhideFPSkey.isPressed()) {
			keyToggleFPSVis = true;
		} else if (BattyBaseUI.moveFPSScreenPos.isPressed()){
			keyMoveFPS = true;
        }

	}


    public BattyUIKeys() {
        
    }

}
