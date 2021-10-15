/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package battys.coordinatesplusmod.client;

import battys.coordinatesplusmod.util.CommonProxy;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;

import static battys.coordinatesplusmod.util.Reference.MODID;
import static battys.coordinatesplusmod.util.Reference.MOD_SHORTNAME;
import static org.lwjgl.glfw.GLFW.*;

/**
 *
 * Declare and register the KeyBindings
 * @author BatHeart
 */
@OnlyIn(Dist.CLIENT)
public final class ClientProxy implements CommonProxy {




    @Override
    public void preloadClasses() {
        CommonProxy.super.preloadClasses();
    }
}
