package de.treinke.minecraftguilds.Proxy;

import com.mojang.brigadier.CommandDispatcher;
import de.treinke.minecraftguilds.Events.KeyPressEvent;
import de.treinke.minecraftguilds.Events.MonsterDropEvent;
import de.treinke.minecraftguilds.objects.Claim;
import de.treinke.minecraftguilds.Events.ClaimEvents;
import de.treinke.minecraftguilds.objects.Guild;
import javafx.scene.input.KeyCode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import de.treinke.minecraftguilds.Main;
import java.util.List;


public class ClientProxy implements IProxy {
	public String side = "";
	public static KeyBinding[] keyBindings;
	public static final Logger LOGGER = LogManager.getLogger(Main.MODID);
	public MinecraftServer SERVER = null;
	private final CommandDispatcher<CommandSource> dispatcher = new CommandDispatcher<>();

	@Override
	public boolean isGuildPlayerOnline(String guild) {
		return false;
	}

	@Override
	public void init() {
		this.side = "CLIENT";
		LOGGER.debug(this.side+"-Proxy wird geladen");

		// declare an array of key bindings
		keyBindings = new KeyBinding[1];


		// instantiate the key bindings
		keyBindings[0] = new KeyBinding("key.guild.desc", KeyCode.G.impl_getCode(), "key.guild.category");

		// register all the key bindings
		for (int i = 0; i < keyBindings.length; ++i)
		{
			ClientRegistry.registerKeyBinding(keyBindings[i]);
		}

		LOGGER.debug( keyBindings.length+" keys geladen.");

		//MinecraftForge.EVENT_BUS.register(new MonsterDropEvent());
		MinecraftForge.EVENT_BUS.register(new KeyPressEvent());
		MinecraftForge.EVENT_BUS.register(new ClaimEvents());
		//new CustomCommands(false);
	}

	@Override
	public void showSide() {
		LOGGER.debug("Side: "+this.side);
	}

	@Override
	public List<Guild> getPlayerGuildList() {
		return null;
	}

	@Override
	public String getPlayerGuild(String playerName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getGuild(String guildname) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPlayerGuildName(String playerName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void serverStarted() {

	}

	@Override
	public void saveGuilds() {

	}

	@Override
	public void addClaim(String guild_name,int dim, int x, int z) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean acceptGuild(String guild_name, String serverPlayer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void refuseGuild(String values, String name) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<String> getInvites(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void inviteUser(String string, String string2) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<Claim> getGuildClaims() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void donateGuild(String name, int amount) {
		// TODO Auto-generated method stub
	}

	@Override
	public void playerdonateGuild(String name, int amount) {
		// TODO Auto-generated method stub
	}

	@Override
	public void kickGuild(String name) {}

	@Override
	public void promoteGuild(String name) {}

	@Override
	public void demoteGuild(String name) {}

	@Override
	public boolean inInGuild(CommandSource p_198868_0_) {
		return false;
	}

	@Override
	public void guildChat(String string, String values) {

	}

	@Override
	public void warnGuild(String message) {

	}

	@Override
	public void killplayer(ServerPlayerEntity player) {

	}

	@Override
	public void showClaimMessage(boolean force) {
		PlayerEntity player = Minecraft.getInstance().player;
		ClaimEvents.showMessage(player, player.getPosition().getX()/16, player.getPosition().getZ()/16,force);
	}

	@Override
	public void showClaimMessage() {
		showClaimMessage(false);
	}
	@Override
	public void setServer(MinecraftServer server) {
		
	}
}
