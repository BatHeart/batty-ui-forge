/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package batty.ui;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent;

/**
 *
 * @author BatHeart
**/
public class BattyUIKeys {

    public static boolean keyToggleCoords = false;
    public static boolean keyToggleTimerVis = false;
    public static boolean keyToggleTimerRun = false;
    public static boolean keyResetTimer = false;
    
    @SubscribeEvent
	public void trackKeyInputs(KeyInputEvent event) {

		if (BattyBaseUI.hideunhideCoordskey.func_151468_f()) {
			keyToggleCoords = true;
		} else if (BattyBaseUI.hideunhideTimerkey.func_151468_f()) {
			keyToggleTimerVis = true;
		} else if (BattyBaseUI.startstopTimerkey.func_151468_f()) {
			keyToggleTimerRun = true;
		} else if (BattyBaseUI.resetTimerkey.func_151468_f()) {
			keyResetTimer = true;
		}

	}


    public BattyUIKeys() {
        
    }

}
