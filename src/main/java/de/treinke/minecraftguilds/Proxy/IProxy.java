package de.treinke.minecraftguilds.Proxy;


import java.util.List;

import de.treinke.minecraftguilds.objects.Claim;
import de.treinke.minecraftguilds.objects.Guild;

import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;

public interface IProxy {
	String side = "";
	MinecraftServer SERVER = null;

	void init();
		
	List<Guild> getPlayerGuildList();

	String getPlayerGuild(String playerName);
	
	String getGuild(String guildname);	
	
	
	String getPlayerGuildName(String playerName);

		
	void createGuild(String guild_name, PlayerEntity player);
	
	void addClaim(String guild_name,int dim, int x, int z);
	
	boolean acceptGuild(String guild_name, String serverPlayer);
	
	

	void refuseGuild(String values, String name);

	List<String> getInvites(String name);

		
	void inviteUser(String string, String string2);

	List<Claim> getGuildClaims();
	
	void donateGuild(String name, int amount);

	void playerdonateGuild(String name, int amount);

	void kickGuild(String name);

	void promoteGuild(String name);

	void demoteGuild(String name);

	void showClaimMessage();

	void setServer(MinecraftServer server);

	boolean inInGuild(CommandSource p_198868_0_);

	void guildChat(String string, String values);

	boolean isGuildPlayerOnline(String guild);

    void showSide();
}
