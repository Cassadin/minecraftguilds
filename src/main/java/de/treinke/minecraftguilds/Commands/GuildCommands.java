package de.treinke.minecraftguilds.Commands;

import com.mojang.brigadier.CommandDispatcher;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.treinke.minecraftguilds.Main;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.MessageArgument;
import net.minecraft.entity.player.ServerPlayerEntity;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class GuildCommands {
    public static final Logger LOGGER = LogManager.getLogger(Main.MODID);
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(
                (LiteralArgumentBuilder)Commands.literal("guild")
                        .then(
                                Commands.argument("message", MessageArgument.message())
                                        .executes(GuildCommands::execute)
                        )
                        .executes(c -> {
                            System.out.println("Bitte eine Nachricht eingeben");
                            return 0;
                        })
        );

        dispatcher.register((LiteralArgumentBuilder)Commands.literal("g").then(
                Commands.argument("message", MessageArgument.message())
                        .executes(GuildCommands::execute)
                )
                        .executes(c -> {
                            System.out.println("Bitte eine Nachricht eingeben");
                            return 0;
                        })
        );
    }

    private static int execute(CommandContext<CommandSource> context) throws CommandSyntaxException {

        if(context.getSource().getEntity() instanceof ServerPlayerEntity) {

            String msg =  MessageArgument.getMessage(context, "message").getString();

            Main.proxy.guildChat(context.getSource().asPlayer().getName().getString(), msg);
        }else{
            LOGGER.warn("Dieser Befehl kann nur als Spieler verwendet werden.");
        }

        return 1;
    }
}