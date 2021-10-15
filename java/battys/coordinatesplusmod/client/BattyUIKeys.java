/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package battys.coordinatesplusmod.client;

import battys.coordinatesplusmod.util.ClientUtil;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import battys.coordinatesplusmod.client.ClientProxy;
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
	public static boolean keyToggleInfoVis = false;
	public static boolean keyMoveInfo = false;

	
 	@SubscribeEvent
	public void trackKeyInputs(ClientTickEvent event) {

		if (ClientUtil.hideunhideCoordskey.isPressed()) {
			keyToggleCoords = true;
		} else if (ClientUtil.moveCoordScreenPos.isPressed()) {
			keyMoveCoords = true;
		} else if (ClientUtil.copyCoordsClipboard.isPressed()) {
			keyCopyCoords = true;			
        } else if (ClientUtil.hideunhideTimerkey.isPressed()) {
			keyToggleTimerVis = true;
		} else if (ClientUtil.startstopTimerkey.isPressed()) {
			keyToggleTimerRun = true;
		} else if (ClientUtil.resetTimerkey.isPressed()) {
			keyResetTimer = true;
		} else if (ClientUtil.moveTimerScreenPos.isPressed()){
			keyMoveTimer = true;
        } else if (ClientUtil.hideunhideInfokey.isPressed()) {
			keyToggleInfoVis = true;
		} else if (ClientUtil.moveInfoScreenPos.isPressed()){
			keyMoveInfo = true;
        }

	}


    public BattyUIKeys() {
        
    }

}
