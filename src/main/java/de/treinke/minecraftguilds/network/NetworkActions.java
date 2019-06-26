package de.treinke.minecraftguilds.network;

public class NetworkActions {

	public static final int GuildCreate 			= 0;
	public static final int GuildCheck 				= 1;
	public static final int GuildCheckAnswer 		= 2;
	public static final int GuildInvites 			= 3;
	public static final int GuildInvitesAnswer 		= 4;
	public static final int GuildAccept 			= 5;
	public static final int GuildRefuse 			= 6;
	public static final int GuildInviteUser 		= 7;
	public static final int GuildClaimList 			= 8;
	public static final int GuildDonation 			= 9;
	public static final int GuildKick               = 10;
	public static final int GuildPromote            = 11;
	public static final int GuildDemote             = 12;
	public static final int GuildClaim              = 13;
    public static final int GuildChat               = 14;


	public static String Messagetype(int i)
	{
		
		switch(i)
		{
			case GuildCreate: return "GuildCreate";
			case GuildCheck: return "GuildCheck";
			case GuildCheckAnswer: return "GuildCheckAnswer";
			case GuildInvites: return "GuildInvites";
			case GuildInvitesAnswer: return "GuildInvitesAnswer";
			case GuildAccept: return "GuildAccept";
			case GuildRefuse: return "GuildRefuse";
			case GuildInviteUser: return "GuildInviteUser";
			case GuildClaimList: return "GuildClaimList";
			case GuildDonation: return "GuildDonation";
			case GuildKick: return "GuildKick";
			case GuildPromote: return "GuildPromote";
			case GuildDemote: return "GuildDemote";
			case GuildClaim: return "GuildClaim";
			case GuildChat: return "GuildChat";
		}
		
		
		return "Unbekannte ID "+i+" ... Muss in Networkactions.Messagetype eingetragen werden";
	}
}
