package de.treinke.minecraftguilds.network;

import de.treinke.minecraftguilds.network.Messages.*;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class SimpleNetworkWrapper {

    private static final String PROTOCOL_VERSION = Integer.toString(1);
    private int ID = 0;
    private static SimpleChannel net = null;

    public SimpleNetworkWrapper(String modid) {
        net = NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation(modid, "main_channel"))
                .clientAcceptedVersions(PROTOCOL_VERSION::equals)
                .serverAcceptedVersions(PROTOCOL_VERSION::equals)
                .networkProtocolVersion(() -> PROTOCOL_VERSION)
                .simpleChannel();
        registerPackets();
    }


    public SimpleChannel getSimpleChannel()
    {
        return net;
    }

    public void registerPackets()
    {
        net.registerMessage(ID++, GuildAccept.class, GuildAccept::encode, GuildAccept::decode, GuildAccept.Handler::handle);
        net.registerMessage(ID++, GuildCheck.class, GuildCheck::encode, GuildCheck::decode, GuildCheck.Handler::handle);
        net.registerMessage(ID++, GuildCheckAnswer.class, GuildCheckAnswer::encode, GuildCheckAnswer::decode, GuildCheckAnswer.Handler::handle);
        net.registerMessage(ID++, GuildClaim.class, GuildClaim::encode, GuildClaim::decode, GuildClaim.Handler::handle);
        net.registerMessage(ID++, GuildClaimList.class, GuildClaimList::encode, GuildClaimList::decode, GuildClaimList.Handler::handle);
        net.registerMessage(ID++, GuildCreate.class, GuildCreate::encode, GuildCreate::decode, GuildCreate.Handler::handle);
        net.registerMessage(ID++, GuildCreateAnswer.class, GuildCreateAnswer::encode, GuildCreateAnswer::decode, GuildCreateAnswer.Handler::handle);
        net.registerMessage(ID++, GuildDemote.class, GuildDemote::encode, GuildDemote::decode, GuildDemote.Handler::handle);
        net.registerMessage(ID++, GuildDonation.class, GuildDonation::encode, GuildDonation::decode, GuildDonation.Handler::handle);
        net.registerMessage(ID++, GuildInvites.class, GuildInvites::encode, GuildInvites::decode, GuildInvites.Handler::handle);
        net.registerMessage(ID++, GuildInvitesAnswer.class, GuildInvitesAnswer::encode, GuildInvitesAnswer::decode, GuildInvitesAnswer.Handler::handle);
        net.registerMessage(ID++, GuildInviteUser.class, GuildInviteUser::encode, GuildInviteUser::decode, GuildInviteUser.Handler::handle);
        net.registerMessage(ID++, GuildKick.class, GuildKick::encode, GuildKick::decode, GuildKick.Handler::handle);
        net.registerMessage(ID++, GuildPromote.class, GuildPromote::encode, GuildPromote::decode, GuildPromote.Handler::handle);
        net.registerMessage(ID++, GuildRefuse.class, GuildRefuse::encode, GuildRefuse::decode, GuildRefuse.Handler::handle);
        net.registerMessage(ID++, GuildWarning.class, GuildWarning::encode, GuildWarning::decode, GuildWarning.Handler::handle);
        net.registerMessage(ID++, GuildWarningMessage.class, GuildWarningMessage::encode, GuildWarningMessage::decode, GuildWarningMessage.Handler::handle);
    }


    public <MSG> void sendTo(MSG msg, ServerPlayerEntity player)
    {
        if (!(player instanceof FakePlayer))
        {
            net.sendTo(msg, player.connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
        }
    }

    public <MSG> void sendToServer(MSG msg) {
        net.sendToServer(msg);
    }
}
