/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package batty.ui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.MinecraftForge;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
//import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = "Batty's Coordinates PLUS", name = "Batty's Coordinates PLUS", version = "1.7.2_1.4.0")
/**
 * Forge base class for BattyUI
 * @author BatHeart
 */
public class BattyBaseUI {
	/**
	 * This is the instance of Batty's Coordinates PLUS mod that Forge uses
	 */
	@Instance("Batty's UI")
	public static BattyBaseUI instance;
	/**
	 * Says where the client and server 'proxy' code is loaded.
	 */
	@SidedProxy(clientSide = "batty.ui.client.ClientProxy", serverSide = "batty.ui.CommonProxy")
	public static CommonProxy proxy;

	public static KeyBinding hideunhideCoordskey = new KeyBinding(
			"Hide / Unhide Coords", Keyboard.KEY_NUMPAD4, "Batty's Coordinates");
	public static KeyBinding moveCoordScreenPos = new KeyBinding(
			"Change Coords Screen Position", Keyboard.KEY_NUMPAD1,
			"Batty's Coordinates");
	public static KeyBinding hideunhideTimerkey = new KeyBinding(
			"Hide / Unhide Timer", Keyboard.KEY_NUMPAD5, "Batty's Timer");
	public static KeyBinding startstopTimerkey = new KeyBinding(
			"Start / Stop Timer", Keyboard.KEY_DECIMAL, "Batty's Timer");
	public static KeyBinding resetTimerkey = new KeyBinding(
			"Reset Timer to Zero", Keyboard.KEY_NUMPAD0, "Batty's Timer");
	public static KeyBinding moveTimerScreenPos = new KeyBinding(
			"Change Timer Screen Position", Keyboard.KEY_NUMPAD2,
			"Batty's Timer");

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {

		ClientRegistry.registerKeyBinding(hideunhideCoordskey);
		ClientRegistry.registerKeyBinding(moveCoordScreenPos);
		ClientRegistry.registerKeyBinding(hideunhideTimerkey);
		ClientRegistry.registerKeyBinding(startstopTimerkey);
		ClientRegistry.registerKeyBinding(resetTimerkey);
		ClientRegistry.registerKeyBinding(moveTimerScreenPos);
		
	}

	@EventHandler
	public void load(FMLInitializationEvent event) {
		proxy.registerRenderers();

	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		MinecraftForge.EVENT_BUS
				.register(new BattyUI(Minecraft.getMinecraft()));
		FMLCommonHandler.instance().bus().register(new BattyUIKeys());
	}

}
