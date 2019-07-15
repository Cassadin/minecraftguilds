package de.treinke.minecraftguilds.objects;

import java.util.ArrayList;
import java.util.List;

public class Guild {

    public static Guild MyGuild = null;
    public static double guild_claim_factor = 0.25;
    public static List<String> single_user_invites = new ArrayList<>();
    public static List<Claim> all_claims = new ArrayList<>();
    public static String current_claim_owner = "";
    public static List<Guild> list = new ArrayList<>();

    public String name = "";
    public String leader = "";
    public String[] offi = new String[2];
    public String[] member = new String[5];

    public List<String> invites = new ArrayList<>();
    public List<Claim> claims = new ArrayList<>();
    public int cash = 0;
    public int talents = 0;

    public Guild(String guild_name, String serverPlayer) {
        //System.out.println("["+Main.proxy.side+"] GUILD CREATED: "+guild_name);
        name = guild_name;
        leader = serverPlayer;
    }

    @Override
    public String toString()
    {
        return this.name;
    }


    public boolean getMember(String username)
    {
        if(leader.equals(username))
            return true;

        for(int i = 0; i < offi.length; i++)
            if(offi[i] != null)
                if(offi[i].equals(username))
                    return true;

        for(int i = 0; i < member.length; i++)
            if(member[i] != null)
                if(member[i].equals(username))
                    return true;
        return false;
    }

    public boolean getInvites(String username)
    {
        return invites.contains(username);
    }


}
