package de.treinke.minecraftguilds.GUI;

import com.google.gson.Gson;
import de.treinke.minecraftguilds.Main;
import de.treinke.minecraftguilds.network.NetworkActions;
import de.treinke.minecraftguilds.network.NetworkMessage;
import de.treinke.minecraftguilds.network.NetworkObject;
import de.treinke.minecraftguilds.objects.GuiButton;
import de.treinke.minecraftguilds.objects.GuiTextField;
import de.treinke.minecraftguilds.objects.Guild;
import de.treinke.minecraftguilds.objects.TexturedButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GuildGUI extends Screen {
    public static final Logger LOGGER = LogManager.getLogger(Main.MODID);

    public static boolean refreshed = true;

    final int BTN_CREATE = 0;
    final int TXT_CREATE = 1;
    final int BTN_ACCEPT = 2;
    final int BTN_REFUSE = 3;
    final int TXT_INVITE = 4;
    final int BTN_INVITE = 5;
    final int BTN_PROMOTE = 6;
    final int BTN_DEMOTE = 7;
    final int BTN_KICK = 8;
    final int BTN_CLAIM = 9;

    final int PAGE_OVERVIEW = 0;
    final int PAGE_MEMBERS = 1;
    final int PAGE_MAP = 2;
    final int PAGE_TALENTS = 3;

    int current_page = PAGE_OVERVIEW;

    private static final ResourceLocation TEXTURES_INVITE = new ResourceLocation(Main.MODID,"textures/gui/guild/invite.png");
    int invite_width = 154;
    int invite_height = 230;
    int invite_centerx = 0;
    int invite_centery = 0;
    
    List<String> invite_list = null;
    Button invite_create = null;
    Map<Button,String> invite_accepts = new HashMap<>();
    Map<Button,String> invite_refuse = new HashMap<>();
    private TextFieldWidget text = null;


    private static final ResourceLocation TEXTURES_GUILD = new ResourceLocation(Main.MODID,"textures/gui/guild/guild.png");
    int guild_width = 169;
    int guild_height = 230;
    int guild_centerx = 0;
    int guild_centery = 0;
    int guild_tabs = 60;
    Map<Button,String> promote_demote = new HashMap<>();
    Map<Button,String> kick = new HashMap<>();


    private static ResourceLocation USED_TEXTURES = null;

    private GuiTextField text_invite = null;

    private boolean init_with_guild = false;

    private boolean init_done = false;
    private GuiButton btn_claim = null;

    public GuildGUI(int page) {
        super(new StringTextComponent(""));
        LOGGER.debug("GuildGUI opened");
        current_page = page;
        initGui();
    }

    public GuildGUI() {
        super(new StringTextComponent(""));
        LOGGER.debug("GuildGUI opened");
        initGui();
    }

    public void initGui()
    {
        if(Guild.MyGuild == null)
        {
            NetworkObject answer = new NetworkObject();
            answer.action = NetworkActions.GuildCheck;
            Main.NETWORK.sendToServer(new NetworkMessage((new Gson()).toJson(answer)));

        }else {
            init_with_guild = true;
        }
        load_invites();
        refreshed = true;
    }

    @Override
    protected void init()
    {
        buttons.clear();

        if(Guild.MyGuild == null)
        {

            this.invite_create = (GuiButton)this.addButton(new GuiButton(BTN_CREATE,(width / 2) - (invite_width / 2) + 125, (height / 2) - (invite_height / 2) + 20, 20, 20, I18n.format("guild.tick", new Object[0]), (p_213026_1_) -> {
                this.actionPerformed(p_213026_1_);
            }));


            int max = Guild.single_user_invites.size();
            if(max>5)
                max = 5;

            if(max > 0)
                for(int i = 0; i < max; i++)
                {
                    invite_accepts.put((GuiButton)this.addButton(new GuiButton(BTN_ACCEPT,(width/2)+(invite_width/2)-49,(height/2)-(invite_height/2)+65+i*20,20,20, I18n.format("guild.tick", new Object[0]), (p_213026_1_) -> {
                        this.actionPerformed(p_213026_1_);
                    })),Guild.single_user_invites.get(i));

                    invite_accepts.put((GuiButton)this.addButton(new GuiButton(BTN_REFUSE,(width/2)+(invite_width/2)-27,(height/2)-(invite_height/2)+65+i*20,20,20, I18n.format("guild.cross", new Object[0]), (p_213026_1_) -> {
                        this.actionPerformed(p_213026_1_);
                    })),Guild.single_user_invites.get(i));

                }

            text = new GuiTextField(TXT_CREATE,this.font, (width/2)-(invite_width/2)+10, (height/2)-(invite_height/2)+20, 110, 20);
            text.setMaxStringLength(16);
            text.setText("");
            text.setFocused2(true);


        }else {
            if(init_with_guild)
            {
                int free_offi = Guild.MyGuild.offi.length;
                for(int i = 0; i < Guild.MyGuild.offi.length; i++)
                    if(Guild.MyGuild.offi[i] != null)
                        if(Guild.MyGuild.offi[i].length() > 0)
                            free_offi--;

                int free_member = Guild.MyGuild.member.length;
                for(int i = 0; i < Guild.MyGuild.member.length; i++)
                    if(Guild.MyGuild.member[i] != null)
                        if(Guild.MyGuild.member[i].length() > 0)
                            free_member--;

                switch(current_page)
                {
                    case PAGE_OVERVIEW:
                        boolean canClaim = false;
                        if(Guild.MyGuild.leader.equals(Minecraft.getInstance().player.getName().getString()))
                            canClaim = true;

                        if(!canClaim)
                            for(int i = 0; i < Guild.MyGuild.offi.length; i++)
                                if(Guild.MyGuild.offi[i] != null)
                                    if(Guild.MyGuild.offi[i].equals(Minecraft.getInstance().player.getName().getString()))
                                        canClaim = true;

                        if(canClaim)
                        {
                            this.btn_claim = this.addButton(new GuiButton(BTN_CLAIM,(width/2)-(guild_width/2)+(guild_width-24),(height/2)-(invite_height/2)+154,20,20, I18n.format("guild.tick", new Object[0]), (p_213026_1_) -> {
                                this.actionPerformed(p_213026_1_);
                            }));
                        }

                        break;
                    case PAGE_MEMBERS:
                        if(Guild.MyGuild.leader.equals(Minecraft.getInstance().player.getName().getString())) {
                            text_invite = new GuiTextField(TXT_INVITE, this.font, (width / 2) - (guild_width / 2) + (guild_width - 82), (height / 2) - (guild_height / 2) + 154, 57, 20);
                            text_invite.setMaxStringLength(16);
                            text_invite.setText("");
                        }

                        boolean is_leader = (Guild.MyGuild.leader.equals(Minecraft.getInstance().player.getName().getString()));
                        boolean is_offi = false;
                        for(int i = 0; i < Guild.MyGuild.offi.length; i++)
                            if(Guild.MyGuild.offi[i] != null)
                                if(Guild.MyGuild.offi[i].length() > 0)
                                    is_offi = Guild.MyGuild.offi[i].equals(Minecraft.getInstance().player.getName().getString())||is_offi;



                        if(is_leader)
                        {
                            for(int i = 0; i < Guild.MyGuild.offi.length; i++)
                                if(Guild.MyGuild.offi[i] != null)
                                    if(Guild.MyGuild.offi[i].length() > 0)
                                            if (free_member > 0 && is_leader) {
                                                TexturedButton btn_acc = (TexturedButton) this.addButton(new TexturedButton(BTN_DEMOTE, (width / 2) - (guild_width / 2) + (guild_tabs / 2) + 130, (height / 2) - (guild_height / 2) + 50 + (i * 10), 180, 16, 10, 10, USED_TEXTURES, (p_213026_1_) -> {
                                                    this.actionPerformed(p_213026_1_);
                                                }));
                                                promote_demote.put(btn_acc, Guild.MyGuild.offi[i]);

                                            }

                            for(int i = 0; i < Guild.MyGuild.member.length; i++)
                                if(Guild.MyGuild.member[i] != null)
                                    if(Guild.MyGuild.member[i].length() > 0)
                                        if(free_offi > 0)
                                        {
                                            TexturedButton btn_acc = (TexturedButton)this.addButton(new TexturedButton(BTN_PROMOTE,(width/2)-(guild_width/2)+(guild_tabs/2)+130,(height/2)-(guild_height/2)+88+(i*10),170,16,10,10,USED_TEXTURES, (p_213026_1_) -> {
                                                this.actionPerformed(p_213026_1_);
                                            }));
                                            promote_demote.put(btn_acc,Guild.MyGuild.member[i]);
                                        }


                            if(free_member > 0)
                                this.addButton(new GuiButton(BTN_INVITE,(width/2)-(guild_width/2)+(guild_width-24),(height/2)-(invite_height/2)+154,20,20,I18n.format("guild.tick", new Object[0]), (p_213026_1_) -> {
                                    this.actionPerformed(p_213026_1_);
                                }));

                        }


                        for(int i = 0; i < Guild.MyGuild.offi.length; i++)
                            if(Guild.MyGuild.offi[i] != null)
                                if(Guild.MyGuild.offi[i].length() > 0)
                                    if(is_leader||Guild.MyGuild.offi[i].equals(Minecraft.getInstance().player.getName().getString())) {
                                        TexturedButton btn_acc = (TexturedButton) this.addButton(new TexturedButton(BTN_KICK, (width / 2) - (guild_width / 2) + (guild_tabs / 2) + 145, (height / 2) - (guild_height / 2) + 50 + (i * 10), 190, 16, 10, 10, USED_TEXTURES, (p_213026_1_) -> {
                                            this.actionPerformed(p_213026_1_);
                                        }));
                                        kick.put(btn_acc, Guild.MyGuild.offi[i]);
                                    }

                        for(int i = 0; i < Guild.MyGuild.member.length; i++)
                            if(Guild.MyGuild.member[i] != null)
                                if(Guild.MyGuild.member[i].length() > 0)
                                    if(is_leader||is_offi||Guild.MyGuild.member[i].equals(Minecraft.getInstance().player.getName().getString())) {
                                        TexturedButton btn_acc = (TexturedButton) this.addButton(new TexturedButton(BTN_KICK, (width / 2) - (guild_width / 2) + (guild_tabs / 2) + 145, (height / 2) - (guild_height / 2) + 88 + (i * 10), 190, 16, 10, 10, USED_TEXTURES, (p_213026_1_) -> {
                                            this.actionPerformed(p_213026_1_);
                                        }));
                                        kick.put(btn_acc, Guild.MyGuild.member[i]);
                                    }
                        break;
                }
                refreshed = true;
            } else {
                Minecraft.getInstance().displayGuiScreen(new GuildGUI(current_page));
                return;
            }
        }

        updateButton();
        super.init();
    }


    @Override
    public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
        try{
            if(Guild.MyGuild == null)
            {
                if(init_with_guild) {
                    Minecraft.getInstance().displayGuiScreen(new GuildGUI());
                    return;
                }
                invite_centerx = (width/2)-(invite_width/2);
                invite_centery = (height/2)-(invite_height/2);
                USED_TEXTURES = TEXTURES_INVITE;
                this.minecraft.getRenderManager().textureManager.bindTexture(USED_TEXTURES);
                blit(invite_centerx,invite_centery,0,0,invite_width,invite_height);

                drawCenteredString(this.font,I18n.format("guild.create_header", new Object[0]),(width/2),invite_centery+5,0xFFFFFF);
                drawCenteredString(this.font,I18n.format("guild.invites", new Object[0]),(width/2),invite_centery+50,0xFFFFFF);

                int max = Guild.single_user_invites.size();
                if(max>5)
                    max = 5;

                if(max > 0)
                    for(int i = 0; i < max; i++)
                        drawString(this.font,Guild.single_user_invites.get(i),invite_centerx+10,invite_centery+70+i*20,0xFFFFFF);

                text.render(p_render_1_, p_render_2_, p_render_3_);


            }else {
                if (init_with_guild) {
                    if (!refreshed) {
                        this.minecraft.displayGuiScreen(new GuildGUI(current_page));
                        return;
                    }

                    /*
                    int free_offi = Guild.MyGuild.offi.length;
                    for(int i = 0; i < Guild.MyGuild.offi.length; i++)
                        if(Guild.MyGuild.offi[i] != null)
                            if(Guild.MyGuild.offi[i].length() > 0)
                                free_offi--;*/

                    int free_member = Guild.MyGuild.member.length;
                    for (int i = 0; i < Guild.MyGuild.member.length; i++)
                        if (Guild.MyGuild.member[i] != null)
                            if (Guild.MyGuild.member[i].length() > 0)
                                free_member--;


                    guild_centerx = (width / 2) - (guild_width / 2) + (guild_tabs / 2);
                    guild_centery = (height / 2) - (guild_height / 2);
                    USED_TEXTURES = TEXTURES_GUILD;
                    this.minecraft.getRenderManager().textureManager.bindTexture(USED_TEXTURES);

                    blit(guild_centerx - guild_tabs, guild_centery, guild_width, 0, guild_tabs, 16);
                    blit(guild_centerx - guild_tabs, guild_centery + 16, guild_width, 0, guild_tabs, 16);
                    blit(guild_centerx, guild_centery, 0, 0, guild_width, guild_height);


                    switch (current_page) {
                        case PAGE_OVERVIEW:

                            drawCenteredString(this.font, "Gilde", guild_centerx + 83, guild_centery + 10, 0xFFFFFF);

                            // Gildeninfos

                            drawString(this.font, "Name: " + Guild.MyGuild.name, guild_centerx + 10, guild_centery + 30, 0xFFFFFF);
                            drawString(this.font, "Claim", guild_centerx + 10, guild_centery + 45, 0xFFFFFF);
                            drawString(this.font, "X: " + Guild.MyGuild.claims.get(0).x, guild_centerx + 50, guild_centery + 45, 0xFFFFFF);
                            drawString(this.font, "Z: " + Guild.MyGuild.claims.get(0).z, guild_centerx + 100, guild_centery + 45, 0xFFFFFF);

                            drawString(this.font, "Kasse: ", guild_centerx + 10, guild_centery + 70, 0xFFFFFF);

                            int money = Guild.MyGuild.cash;

                            drawCenteredString(this.font, "" + (money / 100), guild_centerx + 120, guild_centery + 70, 0xFFFFFF);
                            money = (money) - ((money / 100) * 100);
                            drawCenteredString(this.font, "" + (money / 10), guild_centerx + 90, guild_centery + 70, 0xFFFFFF);
                            money = money % 10;
                            drawCenteredString(this.font, "" + money, guild_centerx + 60, guild_centery + 70, 0xFFFFFF);

                            drawString(this.font, "Kosten: ", guild_centerx + 10, guild_centery + 85, 0xFFFFFF);


                            drawCenteredString(this.font, "" + ((Double) Math.ceil((Guild.MyGuild.claims.size() + 1) * (Guild.MyGuild.claims.size() + 1) * Guild.guild_claim_factor)).intValue(), guild_centerx + 90, guild_centery + 85, 0xFFFFFF);


                            break;
                        case PAGE_MEMBERS:

                            // Gildenmitglieder

                            drawString(this.font, "GildenLeiter", guild_centerx + 10, guild_centery + 10, 0xFFFFFF);

                            drawString(this.font, Guild.MyGuild.leader, guild_centerx + 30, guild_centery + 22, 0xFFFF80);


                            drawString(this.font, "Offiziere", guild_centerx + 10, guild_centery + 38, 0xFFFFFF);
                            drawString(this.font, Guild.MyGuild.offi[0], guild_centerx + 30, guild_centery + 50, 0xFFFF80);
                            drawString(this.font, Guild.MyGuild.offi[1], guild_centerx + 30, guild_centery + 60, 0xFFFF80);

                            drawString(this.font, "Mitglieder", guild_centerx + 10, guild_centery + 76, 0xFFFFFF);
                            drawString(this.font, Guild.MyGuild.member[0], guild_centerx + 30, guild_centery + 88, 0xFFFF80);
                            drawString(this.font, Guild.MyGuild.member[1], guild_centerx + 30, guild_centery + 98, 0xFFFF80);
                            drawString(this.font, Guild.MyGuild.member[2], guild_centerx + 30, guild_centery + 108, 0xFFFF80);
                            drawString(this.font, Guild.MyGuild.member[3], guild_centerx + 30, guild_centery + 118, 0xFFFF80);
                            drawString(this.font, Guild.MyGuild.member[4], guild_centerx + 30, guild_centery + 128, 0xFFFF80);

                            if (Guild.MyGuild.leader.equals(this.minecraft.player.getName().getString())) {
                                if (free_member > 0) {
                                    if(text_invite != null)
                                        text_invite.render(p_render_1_, p_render_2_, p_render_3_);
                                }
                            }
                            break;
                        default:
                    }

                    guild_centerx = (width / 2) - (guild_width / 2) + (guild_tabs / 2);
                    guild_centery = (height / 2) - (guild_height / 2);

                    drawCenteredString(this.font, I18n.format("guild.tab.overview", new Object[0]), guild_centerx - (guild_tabs / 2), guild_centery + 5, 0xFFFFFF);
                    drawCenteredString(this.font, I18n.format("guild.tab.members", new Object[0]), guild_centerx - (guild_tabs / 2), guild_centery + 16 + 5, 0xFFFFFF);
                }
            }
        }catch(Exception ex)
        {
            LOGGER.error("Fehler beim Rendern der Gildendaten: "+ex.getMessage());
        }

        // Buttons
        super.render(p_render_1_, p_render_2_, p_render_3_);
        init_done = true;
    }

    @Override
    public void tick() {
        super.tick();
    }



    public void actionPerformed(Object p_button)
    {
        try {
            GuiButton button = (GuiButton) p_button;

            button.active = false;
            NetworkObject na = null;
            switch (button.id) {
                case BTN_CREATE:
                    String Gildenname = text.getText();
                    na = new NetworkObject();
                    na.action = NetworkActions.GuildCreate;
                    na.values = Gildenname;

                    Main.NETWORK.sendToServer(new NetworkMessage((new Gson()).toJson(na)));
                    Main.proxy.createGuild(Gildenname, this.minecraft.player);

                    this.minecraft.displayGuiScreen(new GuildGUI());
                    break;
                case BTN_ACCEPT:
                    na = new NetworkObject();
                    na.action = NetworkActions.GuildAccept;
                    na.values = invite_accepts.get(button);
                    Main.NETWORK.sendToServer(new NetworkMessage((new Gson()).toJson(na)));
                    buttons.clear();
                    break;
                case BTN_REFUSE:
                    na = new NetworkObject();
                    na.action = NetworkActions.GuildRefuse;
                    na.values = invite_refuse.get(button);
                    Main.NETWORK.sendToServer(new NetworkMessage((new Gson()).toJson(na)));

                    Guild.single_user_invites.remove(na.values);

                    load_invites();
                    break;
                case BTN_PROMOTE:
                    na = new NetworkObject();
                    na.action = NetworkActions.GuildPromote;
                    na.values = promote_demote.get(button);
                    Main.NETWORK.sendToServer(new NetworkMessage((new Gson()).toJson(na)));
                    break;
                case BTN_DEMOTE:
                    na = new NetworkObject();
                    na.action = NetworkActions.GuildDemote;
                    na.values = promote_demote.get(button);
                    Main.NETWORK.sendToServer(new NetworkMessage((new Gson()).toJson(na)));
                    break;
                case BTN_KICK:
                    na = new NetworkObject();
                    na.action = NetworkActions.GuildKick;
                    na.values = kick.get(button);
                    Main.NETWORK.sendToServer(new NetworkMessage((new Gson()).toJson(na)));
                    break;
                case BTN_CLAIM:
                    na = new NetworkObject();
                    na.action = NetworkActions.GuildClaim;
                    na.values = Guild.MyGuild.name;
                    Main.NETWORK.sendToServer(new NetworkMessage((new Gson()).toJson(na)));
                    break;
                case BTN_INVITE:
                    if (text_invite.getText().matches("[a-zA-Z0-9]{1,16}")) {
                        na = new NetworkObject();
                        na.action = NetworkActions.GuildInviteUser;
                        na.values = "[\"" + Guild.MyGuild.name + "\",\"" + text_invite.getText() + "\"]";
                        Main.NETWORK.sendToServer(new NetworkMessage((new Gson()).toJson(na)));
                        text_invite.setText("");
                    }
                default:
                    //button.id;
            }

            button.active = true;
        }catch(Exception ex)
        {
            LOGGER.error("Fehler beim Ausführen des Klicks: "+ex.getMessage());
        }
    }



    public void load_invites()
    {
        NetworkObject na = new NetworkObject();
        na.action = NetworkActions.GuildInvites;
        Main.NETWORK.sendToServer(new NetworkMessage((new Gson()).toJson(na)));

        init();
    }


    @Override
    public boolean mouseClicked(double x, double y, int btn) {
        if(Guild.MyGuild == null)
        {
            if(text != null)
                text.mouseClicked(x, y, btn);
        }else {
            if(init_with_guild)
            {

                int centerx = (width/2)-(guild_width/2)+(guild_tabs/2);
                int centery = (height/2)-(guild_height/2);


                if(x >= centerx-guild_tabs && x < centerx)
                {
                    int ly = ((Double)((y-centery)/18)).intValue();

                    switch(ly)
                    {
                        case PAGE_OVERVIEW:
                            current_page = PAGE_OVERVIEW;
                            break;
                        case PAGE_MEMBERS:
                            current_page = PAGE_MEMBERS;
                            break;
                    }
                    initGui();
                }

                switch(current_page)
                {
                    case PAGE_MEMBERS:
                        if(Guild.MyGuild.leader.equals(this.minecraft.player.getName().getString()))
                        {
                            if(text_invite != null)
                                text_invite.mouseClicked(x, y, btn);
                        }
                }
            }
        }
        updateButton();
        return super.mouseClicked(x, y, btn);
    }

    private void updateButton() {
        if(Guild.MyGuild == null)
        {
            if(text.getText().matches("[A-Za-z0-9]{4,}") && Guild.current_claim_owner.length() == 0)
                invite_create.active = true;
            else
                invite_create.active = false;
        }
        else
        {
            if(init_with_guild)
            {
                switch(current_page)
                {
                    case PAGE_OVERVIEW:
                        boolean is_leader = (Guild.MyGuild.leader.equals(Minecraft.getInstance().player.getName().getString()));
                        if(is_leader) {
                            final int cx = (Minecraft.getInstance().player.getPosition().getX()) / 16 + (Minecraft.getInstance().player.getPosition().getX() < 0 ? -1 : 0);
                            final int cz = (Minecraft.getInstance().player.getPosition().getZ()) / 16 + (Minecraft.getInstance().player.getPosition().getZ() < 0 ? -1 : 0);

                            int dimen = Minecraft.getInstance().player.dimension.getId();


                            boolean res = false;
                            if (Guild.all_claims.stream().filter(p -> p.x == cx && p.z == cz && p.dim == dimen).collect(Collectors.toList()).size() == 0 &&
                                    Guild.MyGuild.cash >= (((Double) Math.ceil((Guild.MyGuild.claims.size() + 1) * (Guild.MyGuild.claims.size() + 1) * Guild.guild_claim_factor)).intValue() * 100)) {
                                if (Guild.all_claims.stream().filter(p -> p.x == cx - 1 && p.z == cz && p.guild.equals(Guild.MyGuild.name) && p.dim == dimen).collect(Collectors.toList()).size() == 1)
                                    res = true;
                                else if (Guild.all_claims.stream().filter(p -> p.x == cx + 1 && p.z == cz && p.guild.equals(Guild.MyGuild.name) && p.dim == dimen).collect(Collectors.toList()).size() == 1)
                                    res = true;
                                else if (Guild.all_claims.stream().filter(p -> p.x == cx && p.z == cz - 1 && p.guild.equals(Guild.MyGuild.name) && p.dim == dimen).collect(Collectors.toList()).size() == 1)
                                    res = true;
                                else if (Guild.all_claims.stream().filter(p -> p.x == cx && p.z == cz + 1 && p.guild.equals(Guild.MyGuild.name) && p.dim == dimen).collect(Collectors.toList()).size() == 1)
                                    res = true;
                            }

                            if (btn_claim != null)
                                btn_claim.active = res;
                        }
                        break;
                    case PAGE_MEMBERS:
                        break;
                }
            }
        }
    }

    @Override
    public boolean keyPressed(int par1, int par2, int par3) {
        if(init_done) {
            if (Guild.MyGuild == null) {
                if (par1 != 256) {
                    if(text != null)
                        if (text.isFocused())
                            text.keyPressed(par1, par2, par3);
                } else {
                    if(text != null)
                        if (text.isFocused())
                            text.deleteFromCursor(1);
                }
            }else{
                if (par1 != 256) {
                    if(text_invite != null)
                        if (text_invite.isFocused())
                            text_invite.keyPressed(par1, par2, par3);
                } else {
                    if(text_invite != null)
                        if (text_invite.isFocused())
                            text_invite.deleteFromCursor(1);
                }

            }
        }

        return super.keyPressed(par1, par2, par3);
    }


    @Override
    public boolean charTyped(char par1, int par2) {
        if(init_done) {
            if (Guild.MyGuild == null) {
                text.charTyped(par1, par2);
            } else {
                if (init_with_guild) {
                    switch (current_page) {
                        case PAGE_MEMBERS:
                            if (Guild.MyGuild.leader.equals(this.minecraft.player.getName().getString())) {
                                if(text_invite != null)
                                    if(text_invite.isFocused())
                                        text_invite.charTyped(par1, par2);
                            }
                    }
                }
            }
            updateButton();
        }
        return super.charTyped(par1, par2);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    public void showClaimMap()
    {
        final int x = (this.minecraft.player.getPosition().getX()) / 16 + (this.minecraft.player.getPosition().getX() < 0 ? -1 : 0);
        final int z = (this.minecraft.player.getPosition().getZ()) / 16 + (this.minecraft.player.getPosition().getZ() < 0 ? -1 : 0);
        final int dim = this.minecraft.player.dimension.getId();




    }
}
