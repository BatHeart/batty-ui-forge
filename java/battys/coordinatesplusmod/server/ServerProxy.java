package battys.coordinatesplusmod.server;

import battys.coordinatesplusmod.util.CommonProxy;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@OnlyIn(Dist.DEDICATED_SERVER)
public final class ServerProxy implements CommonProxy {

    @Override
    public void preloadClasses() {

    }
}
