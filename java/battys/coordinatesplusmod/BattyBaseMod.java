/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package battys.coordinatesplusmod;

import battys.coordinatesplusmod.client.BattyUI;
import battys.coordinatesplusmod.client.BattyUIKeys;
import battys.coordinatesplusmod.util.ClientUtil;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static battys.coordinatesplusmod.util.Reference.MODID;
import static net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@Mod(MODID)
@EventBusSubscriber(modid = MODID)
/**
 * Forge base class for BattyUI
 * @author BatHeart
 */
public final class BattyBaseMod {


	public static final BattyUI battyUI = new BattyUI(Minecraft.getInstance());
	public static final BattyUIKeys myKeys = new BattyUIKeys();
	public static final ClientUtil myClientUtil = new ClientUtil();
	public static final Logger LOGGER = LogManager.getLogger(MODID);

	public BattyBaseMod() {


        MinecraftForge.EVENT_BUS.register(this);
		MinecraftForge.EVENT_BUS.register(new BattyUIKeys());
		MinecraftForge.EVENT_BUS.register(new BattyUI(Minecraft.getInstance()));
		
	}
}
