package de.treinke.minecraftguilds.Events;

import de.treinke.minecraftguilds.GUI.GuildGUI;
import de.treinke.minecraftguilds.Proxy.ClientProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static de.treinke.minecraftguilds.Main.MODID;
import static net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus.MOD;

public class KeyPressEvent {
    public static final Logger LOGGER = LogManager.getLogger(MODID);

    @SubscribeEvent//(priority= EventPriority.HIGH, receiveCanceled=true)
    public void onEvent(InputEvent.KeyInputEvent event)
    {
        // make local copy of key binding array
        KeyBinding[] keyBindings = ClientProxy.keyBindings;

        if (keyBindings[0].isPressed()) // Guild
        {
            Minecraft.getInstance().displayGuiScreen(new GuildGUI());
        }

    }
}
