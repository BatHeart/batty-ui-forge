/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package battys.coordinatesplusmod.util;

import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

/**
 *
 * @author BatHeart
 */
public interface CommonProxy {
    // Client stuff

    default void preloadClasses() {
        // Nothing here
    }
}
