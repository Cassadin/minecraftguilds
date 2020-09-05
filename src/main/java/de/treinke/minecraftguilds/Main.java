package de.treinke.minecraftguilds;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.treinke.minecraftguilds.Blocks.BlockRegistry;
import de.treinke.minecraftguilds.Commands.GuildCommands;
import de.treinke.minecraftguilds.Items.ItemRegistry;
import de.treinke.minecraftguilds.network.SimpleNetworkWrapper;
import net.minecraft.block.Block;
import net.minecraft.command.CommandSource;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemTier;
import net.minecraft.item.PickaxeItem;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import de.treinke.minecraftguilds.Proxy.*;

@Mod(Main.MODID)
public class Main {
    public static final String MODID = "minecraftguilds";
    public static final Logger LOGGER = LogManager.getLogger(MODID);


    public static IProxy proxy = DistExecutor.runForDist(() -> ClientProxy::new, () -> ServerProxy::new);
    public static final SimpleNetworkWrapper NETWORK = new SimpleNetworkWrapper(MODID);




    public Main() {
        /*FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onServerStarting);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onServerStarted);*/

        MinecraftForge.EVENT_BUS.addListener(this::setup);
        MinecraftForge.EVENT_BUS.addListener(this::enqueueIMC);
        MinecraftForge.EVENT_BUS.addListener(this::processIMC);
        MinecraftForge.EVENT_BUS.addListener(this::onServerStarting);
        MinecraftForge.EVENT_BUS.addListener(this::onServerStarted);

        MinecraftForge.EVENT_BUS.register(this);

        proxy.init();
    }

    private void setup(final FMLCommonSetupEvent event) {}

    private void enqueueIMC(final InterModEnqueueEvent event) {}

    private void processIMC(final InterModProcessEvent event) {}

    private void onServerStarting(final FMLServerStartingEvent e)
    {
        //GuildCommands.register(e.getCommandDispatcher());
    }

    private void onServerStarted(final FMLServerStartedEvent e) {proxy.serverStarted();};

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = MODID)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onBlockRegistry(final RegistryEvent.Register<Block> e) {
            BlockRegistry.registry(e);
        }
        @SubscribeEvent
        public static void onItemRegistry(final RegistryEvent.Register<Item> e) {
            ItemRegistry.registry(e);
            BlockRegistry.itemRegistry(e);
        }

    }


}

