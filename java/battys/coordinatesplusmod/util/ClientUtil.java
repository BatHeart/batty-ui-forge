package battys.coordinatesplusmod.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;

import static battys.coordinatesplusmod.util.Reference.MOD_SHORTNAME;
import static org.lwjgl.glfw.GLFW.*;

public class ClientUtil {
    public static final KeyBinding hideunhideCoordskey = new KeyBinding(
            "key." + MOD_SHORTNAME + ".coorddisplay", GLFW_KEY_KP_4,"key.categories." + MOD_SHORTNAME + ":coords");
    public static final KeyBinding moveCoordScreenPos = new KeyBinding(
            "key." + MOD_SHORTNAME + ".coordmove", GLFW_KEY_KP_1,"key.categories." + MOD_SHORTNAME + ":coords");
    public static final KeyBinding copyCoordsClipboard = new KeyBinding(
            "key." + MOD_SHORTNAME + ".coordcopy", GLFW_KEY_KP_7,"key.categories." + MOD_SHORTNAME + ":coords");
    public static final KeyBinding hideunhideTimerkey = new KeyBinding(
            "key." + MOD_SHORTNAME + ".timerdisplay", GLFW_KEY_KP_5, "key.categories." + MOD_SHORTNAME + ":timer");
    public static final KeyBinding startstopTimerkey = new KeyBinding(
            "key." + MOD_SHORTNAME + ".timerstop", GLFW_KEY_KP_DECIMAL, "key.categories." + MOD_SHORTNAME + ":timer");
    public static final KeyBinding resetTimerkey = new KeyBinding(
            "key." + MOD_SHORTNAME + ".timerzero", GLFW_KEY_KP_0, "key.categories." + MOD_SHORTNAME + ":timer");
    public static final KeyBinding moveTimerScreenPos = new KeyBinding(
            "key." + MOD_SHORTNAME + ".timermove", GLFW_KEY_KP_2,	"key.categories." + MOD_SHORTNAME + ":timer");
    public static final KeyBinding hideunhideInfokey = new KeyBinding(
            "key." + MOD_SHORTNAME + ".infodisplay", GLFW_KEY_KP_6, "key.categories." + MOD_SHORTNAME + ":info");
    public static final KeyBinding moveInfoScreenPos = new KeyBinding(
            "key." + MOD_SHORTNAME + ".infomove", GLFW_KEY_KP_3,	"key.categories." + MOD_SHORTNAME + ":info");

    static {
        ClientRegistry.registerKeyBinding(hideunhideCoordskey);
        ClientRegistry.registerKeyBinding(moveCoordScreenPos);
        ClientRegistry.registerKeyBinding(copyCoordsClipboard);
        ClientRegistry.registerKeyBinding(hideunhideTimerkey);
        ClientRegistry.registerKeyBinding(startstopTimerkey);
        ClientRegistry.registerKeyBinding(resetTimerkey);
        ClientRegistry.registerKeyBinding(moveTimerScreenPos);
        ClientRegistry.registerKeyBinding(hideunhideInfokey);
        ClientRegistry.registerKeyBinding(moveInfoScreenPos);
    }

    public static void tryReloadRenderers() {
        final WorldRenderer worldRenderer = Minecraft.getInstance().worldRenderer;
        if (worldRenderer != null) {
            worldRenderer.loadRenderers();
        }
    }
}
