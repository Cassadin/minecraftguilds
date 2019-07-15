package de.treinke.minecraftguilds.Proxy;


import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.treinke.minecraftguilds.Events.ClaimEvents;
import de.treinke.minecraftguilds.Events.MonsterDropEvent;
import de.treinke.minecraftguilds.Events.PlayerLoginEvent;
import de.treinke.minecraftguilds.network.Messages.GuildCheckAnswer;
import de.treinke.minecraftguilds.network.Messages.GuildClaimList;
import de.treinke.minecraftguilds.network.Messages.GuildWarning;
import de.treinke.minecraftguilds.objects.Claim;
import de.treinke.minecraftguilds.objects.Guild;
import de.treinke.minecraftguilds.Main;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.*;
import net.minecraft.network.play.server.SChatPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerProxy implements IProxy {
	public String side = "";
	public static final Logger LOGGER = LogManager.getLogger(Main.MODID);
	public MinecraftServer SERVER = null;


	@Override
	public void init() {
		this.side = "SERVER";

		MinecraftForge.EVENT_BUS.register(new MonsterDropEvent());
		MinecraftForge.EVENT_BUS.register(new PlayerLoginEvent());
		MinecraftForge.EVENT_BUS.register(new ClaimEvents());
	}

	@Override
	public void serverStarted() {
		this.loadGuilds();
	}

	@Override
	public boolean isGuildPlayerOnline(String guild_name) {
		int index = findGuildIndex(guild_name);

		if(index > -1) {
			for(ServerPlayerEntity player : this.SERVER.getPlayerList().getPlayers())
				if(Guild.list.get(index).getMember(player.getName().getString()))
					return true;
		}

		return false;
	}

	@Override
	public List<Guild> getPlayerGuildList() {
		return Guild.list;
	}

	@Override
	public String getPlayerGuild(String playerName)
	{
		String g = null;

		List<Guild> lst = Guild.list.stream().filter(p -> p.getMember(playerName)).collect(Collectors.toList());
		if(lst.size() > 0)
			g = (new Gson()).toJson(lst.get(0), new TypeToken<Guild>() {}.getType());


		return g;
	}

	@Override
	public String getGuild(String guildname) {
		String g = null;

		List<Guild> lst = Guild.list.stream().filter(p -> p.name.equals(guildname)).collect(Collectors.toList());
		if(lst.size() > 0)
			g = (new Gson()).toJson(lst.get(0), new TypeToken<Guild>() {}.getType());

		return g;
	}

	@Override
	public String getPlayerGuildName(String playerName) {
		String g = null;

		List<Guild> lst = Guild.list.stream().filter(p -> p.getMember(playerName)).collect(Collectors.toList());
		if(lst.size() > 0)
			g = lst.get(0).name;

		return g;
	}

	@Override
	public void addClaim(String guild_name,int dim, int x, int z) {
		int index = findGuildIndex(guild_name);

		if(index > -1)
		{
			int claims = Guild.list.get(index).claims.size();
			if(claims > 0)
			{
				double factor = Guild.guild_claim_factor;
				double claims_factored = Math.ceil((claims+1)*(claims+1)*factor);

				int price = ((Double)claims_factored).intValue()*100;
				Guild.list.get(index).cash-=price;
			}
			Guild.list.get(index).claims.add(new Claim(guild_name,dim,x,z));
			saveGuilds();
			refresh_all_claims();
			refresh_guilds(guild_name);
		}
	}

	private List<String> getGuildPlayers(int index)
	{
		List<String> players = new ArrayList<>();
		players.add(Guild.list.get(index).leader);
		for(int i = 0; i < Guild.list.get(index).offi.length; i++)
			if(Guild.list.get(index).offi[i]!=null)
				if(Guild.list.get(index).offi[i].length()>0)
					players.add(Guild.list.get(index).offi[i]);

		for(int i = 0; i < Guild.list.get(index).member.length; i++)
			if(Guild.list.get(index).member[i]!=null)
				if(Guild.list.get(index).member[i].length()>0)
					players.add(Guild.list.get(index).member[i]);
				return players;
	}

	private void refresh_guilds(String name) {

		int index = findGuildIndex(name);
		if(index > -1)
		{
			List<String> players = getGuildPlayers(index);

			ServerPlayerEntity player = null;

			String guild = null;

			for( int i = 0; i < players.size(); i++)
			{
				player = SERVER.getPlayerList().getPlayerByUsername(players.get(i));
				if(player != null)
				{
					if(guild == null)
						guild = Main.proxy.getPlayerGuild(player.getName().getString());
					Main.NETWORK.sendTo(new GuildCheckAnswer(guild), player);
				}
			}
		}
	}
	
	
	private void refresh_all_claims() {
		String claims = new Gson().toJson(Main.proxy.getGuildClaims(),new TypeToken<List<Claim>>() {}.getType());

		for(ServerPlayerEntity player : SERVER.getPlayerList().getPlayers())
		{
			Main.NETWORK.sendTo(new GuildClaimList(claims),player);
		}

	}

	private int findGuildIndex(String guild_name) {
		for(int i = 0; i < Guild.list.size(); i++)
			if(Guild.list.get(i).name.equals(guild_name))
				return i;
		return -1;
	}

	@Override
	public void saveGuilds()
	{
		Gson g = new Gson();
		System.out.println("Save Guilds");
		try (FileWriter writer = new FileWriter("guilds.json")) {


			String jsonstring = "[";
			for (int i = 0; i < Guild.list.size(); i++)
			{
				if(i>0)
					jsonstring+=";";
				jsonstring+=g.toJson(Guild.list.get(i), new TypeToken<Guild>() {}.getType());
			}
			jsonstring+= "]";


			if(jsonstring.length() > 0)
			{
				BufferedWriter bw = new BufferedWriter(writer);
				bw.write(jsonstring);
				bw.close();
			}

		}catch(Exception ex)
		{
			System.out.println("Fehler beim Speichern der Gildeninformationen: "+ex.getMessage());
		}

	}

	@Override
	public void killplayer(ServerPlayerEntity player) {

		int index = findGuildIndex(getPlayerGuildName(player.getName().getString()));

		if(index > -1) {
			Guild.list.get(index).talents++;

			this.saveGuilds();
			refresh_guilds(Guild.list.get(index).name);
		}
	}

	@Override
	public void warnGuild(String name) {
		int index = findGuildIndex(name);
		if(index > -1) {
			List<String> players = getGuildPlayers(index);

			ServerPlayerEntity player = null;


			for( int i = 0; i < players.size(); i++)
			{
				player = SERVER.getPlayerList().getPlayerByUsername(players.get(i));
				if(player != null)
				{
					Main.NETWORK.sendTo(new GuildWarning(), player);
				}
			}

		}
	}

	@Override
	public void showClaimMessage(boolean b) {

	}

	private void loadGuilds()
	{
		System.out.println("Load Guilds");
		if((new File("guilds.json").exists()))
			try (FileReader writer = new FileReader("guilds.json")) {
				BufferedReader bw = new BufferedReader(writer);
				String jsonstring = bw.readLine();
				bw.close();

				if(jsonstring != null)
					if(jsonstring.length() > 0)
						Guild.list = (new Gson()).fromJson(jsonstring, new TypeToken<List<Guild>>() {}.getType());

			}catch(Exception ex)
			{
				System.out.println("Fehler beim Laden der Gildeninformationen: "+ex.getMessage());
			}
		System.out.println(Guild.list.size()+" Guilds found");

	}
	
	
	@Override
	public boolean acceptGuild(String guild_name, String serverPlayer) {
// Gilde Suchen und member hinzufügen;
		try {
			int index = findGuildIndex(guild_name);

			if(index > -1)
			{


				for(int i = 0; i < 5; i++)
				{
					boolean acc = false;
					if(Guild.list.get(index).member[i] == null)
						acc = true;
					else
					if(Guild.list.get(index).member[i].length() == 0)
						acc = true;

					if(acc)
					{
						Guild.list.get(index).member[i] = serverPlayer;
						Guild.list.get(index).invites.remove(serverPlayer);
						this.saveGuilds();
						return true;
					}
				}

				Guild.list.get(index).invites.remove(serverPlayer);
				this.saveGuilds();
				refresh_guilds(Guild.list.get(index).name);
				return true;
			}else {
				return false;
			}
		}catch(Exception ex)
		{
			return false;
		}
	}

	@Override
	public void refuseGuild(String values, String name) {
		int index = findGuildIndex(values);
		System.out.println("Refuse Guildindex: "+index);
		if(index > -1)
		{
			Guild.list.get(index).invites.remove(name);
			this.saveGuilds();
		}
	}

	@Override
	public List<String> getInvites(String name) {

		List<String> lst = new ArrayList<>();

		for(int i = 0; i < Guild.list.size(); i++)
			if(Guild.list.get(i).getInvites(name))
				lst.add(Guild.list.get(i).name);

		return lst;
	}

	@Override
	public void inviteUser(String string, String string2) {
		int index = findGuildIndex(string);
		if(index > -1)
		{
			Guild.list.get(index).invites.add(string2);
			this.saveGuilds();
		}
	}

	@Override
	public List<Claim> getGuildClaims() {
		List<Claim> claims = new ArrayList<>();

		for(int i = 0; i < Guild.list.size(); i++)
			for(int j = 0; j < Guild.list.get(i).claims.size(); j++)
				claims.add(Guild.list.get(i).claims.get(j));
			Guild.all_claims = claims;
		return claims;
	}

	@Override
	public void donateGuild(String name, int amount) {


		int index = findGuildIndex(name);
		if(index > -1)
		{
			Guild.list.get(index).cash+=amount;
			this.saveGuilds();

			refresh_guilds(Guild.list.get(index).name);

		}
	}

	@Override
	public void playerdonateGuild(String name, int amount) {

		int index = findGuildIndex(getPlayerGuildName(name));
		if(index > -1)
		{
			Guild.list.get(index).cash+=amount;
			this.saveGuilds();

			refresh_guilds(Guild.list.get(index).name);

		}
	}

	@Override
	public void kickGuild(String name) {
		ServerPlayerEntity player = SERVER.getPlayerList().getPlayerByUsername(name);


		if(player != null)
			Main.NETWORK.sendTo(new GuildCheckAnswer(),player);


		int index = findGuildIndex(getPlayerGuildName(name));
		if(index > -1)
		{
			for(int i = 0; i < Guild.list.get(index).offi.length; i++)
				if(Guild.list.get(index).offi[i] != null)
					if(Guild.list.get(index).offi[i].equals(name))
					{
						Guild.list.get(index).offi[i] = "";
						break;
					}

			for(int i = 0; i < Guild.list.get(index).member.length; i++)
				if(Guild.list.get(index).member[i] != null)
					if(Guild.list.get(index).member[i].equals(name))
					{
						Guild.list.get(index).member[i] = "";
						break;
					}
			saveGuilds();
			refresh_guilds(Guild.list.get(index).name);
		}
	}

	@Override
	public void promoteGuild(String name) {

		int index = findGuildIndex(getPlayerGuildName(name));
		if(index > -1)
		{
			for(int i = 0; i < Guild.list.get(index).member.length; i++)
				if(Guild.list.get(index).member[i] != null)
					if(Guild.list.get(index).member[i].equals(name))
					{
						Guild.list.get(index).member[i] = "";
						break;
					}

			for(int i = 0; i < Guild.list.get(index).offi.length; i++)
				if(Guild.list.get(index).offi[i] == null)
				{
					Guild.list.get(index).offi[i] = name;
					break;
				}
				else
				if(Guild.list.get(index).offi[i].length() == 0)
				{
					Guild.list.get(index).offi[i] = name;
					break;
				}
			reorderMembers(Guild.list.get(index).name);
			refresh_guilds(Guild.list.get(index).name);
		}
	}

	@Override
	public void demoteGuild(String name) {
		int index = findGuildIndex(getPlayerGuildName(name));
		if(index > -1)
		{
			for(int i = 0; i < Guild.list.get(index).offi.length; i++)
				if(Guild.list.get(index).offi[i] != null)
					if(Guild.list.get(index).offi[i].equals(name))
					{
						Guild.list.get(index).offi[i] = "";
						break;
					}

			for(int i = 0; i < Guild.list.get(index).member.length; i++)
				if(Guild.list.get(index).member[i] == null)
				{
					Guild.list.get(index).member[i] = name;
					break;
				}
				else
				if(Guild.list.get(index).member[i].length() == 0)
				{
					Guild.list.get(index).member[i] = name;
					break;
				}

				reorderMembers(Guild.list.get(index).name);
			refresh_guilds(Guild.list.get(index).name);
		}
	}

	@Override
	public void showClaimMessage() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setServer(MinecraftServer server) {
		SERVER = server;
	}


	@Override
	public void guildChat(String username, String msg) {

		String guild_name = getPlayerGuildName(username);

		StringTextComponent parent = new StringTextComponent("");
		parent.appendSibling(new StringTextComponent(username+": ").setStyle(new Style().setColor(TextFormatting.GREEN).setBold(true)));
		parent.appendSibling(new StringTextComponent(msg).setStyle(new Style().setColor(TextFormatting.GREEN)));


		this.SERVER.sendMessage(parent);
		SChatPacket packetIn = new SChatPacket(parent, ChatType.CHAT);

		List<ServerPlayerEntity> players = this.SERVER.getPlayerList().getPlayers();

		for(int i = 0; i < players.size(); ++i) {
			if(getPlayerGuildName((players.get(i)).getName().getString()).equals(guild_name))
				(players.get(i)).connection.sendPacket(packetIn);
		}
	}

	@Override
	public void showSide() {
		LOGGER.debug("Side: "+this.side);
	}

	@Override
	public boolean inInGuild(CommandSource p_198868_0_) {
		try {
			return (getPlayerGuildName(p_198868_0_.asPlayer().getName().getString()) != null);
		} catch (CommandSyntaxException e) {
			LOGGER.error("Fehler beim Prüfen, ob in Gilde: "+e.getMessage());
			return false;
		}
	}

	private void reorderMembers(String guild_name)
	{
		int index = findGuildIndex(getPlayerGuildName(guild_name));
		if(index > -1) {

		}
	}
}
