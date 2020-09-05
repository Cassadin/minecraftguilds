package de.treinke.minecraftguilds.GUI;

import com.google.gson.Gson;
import com.mojang.blaze3d.matrix.MatrixStack;
import de.treinke.minecraftguilds.Main;
import de.treinke.minecraftguilds.network.Messages.*;
import de.treinke.minecraftguilds.objects.*;
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
    int guild_width = 165;
    int guild_height = 181;
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
            Main.NETWORK.sendToServer(new GuildCheck());
        else
            init_with_guild = true;

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
                            this.btn_claim = this.addButton(new GuiButton(BTN_CLAIM,(width/2)-(guild_width/2)+(guild_width-24),((height/2)-(invite_height/2))+(guild_height-24),20,20, I18n.format("guild.tick", new Object[0]), (p_213026_1_) -> {
                                this.actionPerformed(p_213026_1_);
                            }));
                        }

                        break;
                    case PAGE_MEMBERS:

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
                                                TexturedButton btn_acc = (TexturedButton) this.addButton(new TexturedButton(BTN_DEMOTE, (width / 2) - (guild_width / 2) + (guild_tabs / 2) + 130, (height / 2) - (guild_height / 2) + 50 + (i * 10), 175, 21, 10, 10, USED_TEXTURES, (p_213026_1_) -> {
                                                    this.actionPerformed(p_213026_1_);
                                                }));
                                                promote_demote.put(btn_acc, Guild.MyGuild.offi[i]);

                                            }

                            for(int i = 0; i < Guild.MyGuild.member.length; i++)
                                if(Guild.MyGuild.member[i] != null)
                                    if(Guild.MyGuild.member[i].length() > 0)
                                        if(free_offi > 0)
                                        {
                                            TexturedButton btn_acc = (TexturedButton)this.addButton(new TexturedButton(BTN_PROMOTE,(width/2)-(guild_width/2)+(guild_tabs/2)+130,(height/2)-(guild_height/2)+88+(i*10),165,21,10,10,USED_TEXTURES, (p_213026_1_) -> {
                                                this.actionPerformed(p_213026_1_);
                                            }));
                                            promote_demote.put(btn_acc,Guild.MyGuild.member[i]);
                                        }


                            if(free_member > 0) {
                                this.addButton(new GuiButton(BTN_INVITE, ((width - guild_width - guild_tabs) / 2) + (guild_width + guild_tabs - 24), ((height - guild_height) / 2) + (guild_height - 24), 20, 20, I18n.format("guild.tick", new Object[0]), (p_213026_1_) -> {
                                    this.actionPerformed(p_213026_1_);
                                }));

                                text_invite = new GuiTextField(TXT_INVITE, this.font, ((width - guild_width - guild_tabs) / 2)+guild_tabs +75, ((height - guild_height) / 2) + (guild_height - 24), guild_width-102, 20);
                                text_invite.setMaxStringLength(16);
                                text_invite.setText("");

                            }
                        }


                        for(int i = 0; i < Guild.MyGuild.offi.length; i++)
                            if(Guild.MyGuild.offi[i] != null)
                                if(Guild.MyGuild.offi[i].length() > 0)
                                    if(is_leader||Guild.MyGuild.offi[i].equals(Minecraft.getInstance().player.getName().getString())) {
                                        TexturedButton btn_acc = (TexturedButton) this.addButton(new TexturedButton(BTN_KICK, (width / 2) - (guild_width / 2) + (guild_tabs / 2) + 145, (height / 2) - (guild_height / 2) + 50 + (i * 10), 185, 21, 10, 10, USED_TEXTURES, (p_213026_1_) -> {
                                            this.actionPerformed(p_213026_1_);
                                        }));
                                        kick.put(btn_acc, Guild.MyGuild.offi[i]);
                                    }

                        for(int i = 0; i < Guild.MyGuild.member.length; i++)
                            if(Guild.MyGuild.member[i] != null)
                                if(Guild.MyGuild.member[i].length() > 0)
                                    if(is_leader||is_offi||Guild.MyGuild.member[i].equals(Minecraft.getInstance().player.getName().getString())) {
                                        TexturedButton btn_acc = (TexturedButton) this.addButton(new TexturedButton(BTN_KICK, (width / 2) - (guild_width / 2) + (guild_tabs / 2) + 145, (height / 2) - (guild_height / 2) + 88 + (i * 10), 185, 21, 10, 10, USED_TEXTURES, (p_213026_1_) -> {
                                            this.actionPerformed(p_213026_1_);
                                        }));
                                        kick.put(btn_acc, Guild.MyGuild.member[i]);
                                    }
                        break;
                    case PAGE_MAP:
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
    public void render(MatrixStack p_230430_1_, int p_render_1_, int p_render_2_, float p_render_3_) {
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
                blit(p_230430_1_,invite_centerx,invite_centery,0,0,invite_width,invite_height);

                drawCenteredString(p_230430_1_,this.font,I18n.format("guild.create_header", new Object[0]),(width/2),invite_centery+5,0xFFFFFF);
                drawCenteredString(p_230430_1_,this.font,I18n.format("guild.invites", new Object[0]),(width/2),invite_centery+50,0xFFFFFF);

                int max = Guild.single_user_invites.size();
                if(max>5)
                    max = 5;

                if(max > 0)
                    for(int i = 0; i < max; i++)
                        drawString(p_230430_1_,this.font,Guild.single_user_invites.get(i),invite_centerx+10,invite_centery+70+i*20,0xFFFFFF);

                text.render(p_230430_1_,p_render_1_, p_render_2_, p_render_3_);


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

                    blit(p_230430_1_,guild_centerx - guild_tabs, guild_centery, guild_width, 0, guild_tabs+5, 21);
                    blit(p_230430_1_,guild_centerx - guild_tabs, guild_centery + 21, guild_width, 0, guild_tabs, 21);
                    blit(p_230430_1_,guild_centerx - guild_tabs, guild_centery + 42, guild_width, 0, guild_tabs, 21);
                    blit(p_230430_1_,guild_centerx, guild_centery, 0, 0, guild_width, guild_height);


                    switch (current_page) {
                        case PAGE_OVERVIEW:


                            blit(p_230430_1_,guild_centerx +65, guild_centery + 69, 165, 31, 10, 10);
                            blit(p_230430_1_,guild_centerx +95, guild_centery + 69, 175, 31, 10, 10);

                            int money = Guild.MyGuild.cash;
                            String kosten = ""+((Double) Math.ceil((Guild.MyGuild.claims.size() + 1) * (Guild.MyGuild.claims.size() + 1) * Guild.guild_claim_factor)).intValue();



                            blit(p_230430_1_,guild_centerx +123+((""+(money/100)).length()-1)*6, guild_centery + 69, 185, 31, 10, 10);
                            blit(p_230430_1_,guild_centerx +65+(kosten.length()-1)*6, guild_centery + 84, 185, 31, 10, 10);

                            drawCenteredString(p_230430_1_,this.font, "Gilde", guild_centerx + 83, guild_centery + 10, 0xFFFFFF);

                            // Gildeninfos

                            drawString(p_230430_1_,this.font, "Name: " + Guild.MyGuild.name, guild_centerx + 10, guild_centery + 30, 0xFFFFFF);
                            drawString(p_230430_1_,this.font, "Claim", guild_centerx + 10, guild_centery + 45, 0xFFFFFF);
                            drawString(p_230430_1_,this.font, "X: " + Guild.MyGuild.claims.get(0).x, guild_centerx + 50, guild_centery + 45, 0xFFFFFF);
                            drawString(p_230430_1_,this.font, "Z: " + Guild.MyGuild.claims.get(0).z, guild_centerx + 100, guild_centery + 45, 0xFFFFFF);

                            drawString(p_230430_1_,this.font, "Kasse: ", guild_centerx + 10, guild_centery + 70, 0xFFFFFF);


                            drawCenteredString(p_230430_1_,this.font, "" + (money / 100), guild_centerx + 120, guild_centery + 70, 0xFFFFFF);
                            money = (money) - ((money / 100) * 100);
                            drawCenteredString(p_230430_1_,this.font, "" + (money / 10), guild_centerx + 90, guild_centery + 70, 0xFFFFFF);
                            money = money % 10;
                            drawCenteredString(p_230430_1_,this.font, "" + money, guild_centerx + 60, guild_centery + 70, 0xFFFFFF);

                            drawString(p_230430_1_,this.font, "Kosten: ", guild_centerx + 10, guild_centery + 85, 0xFFFFFF);
                            drawCenteredString(p_230430_1_,this.font, kosten, guild_centerx + 60, guild_centery + 85, 0xFFFFFF);

                            drawString(p_230430_1_,this.font, "Talentmarken: "+Guild.MyGuild.talents, guild_centerx + 10, guild_centery + 100, 0xFFFFFF);

                            break;
                        case PAGE_MEMBERS:

                            // Gildenmitglieder

                            drawString(p_230430_1_,this.font, "GildenLeiter", guild_centerx + 10, guild_centery + 10, 0xFFFFFF);

                            drawString(p_230430_1_,this.font, Guild.MyGuild.leader, guild_centerx + 30, guild_centery + 22, 0xFFFF80);


                            drawString(p_230430_1_,this.font, "Offiziere", guild_centerx + 10, guild_centery + 38, 0xFFFFFF);
                            drawString(p_230430_1_,this.font, Guild.MyGuild.offi[0], guild_centerx + 30, guild_centery + 50, 0xFFFF80);
                            drawString(p_230430_1_,this.font, Guild.MyGuild.offi[1], guild_centerx + 30, guild_centery + 60, 0xFFFF80);

                            drawString(p_230430_1_,this.font, "Mitglieder", guild_centerx + 10, guild_centery + 76, 0xFFFFFF);
                            drawString(p_230430_1_,this.font, Guild.MyGuild.member[0], guild_centerx + 30, guild_centery + 88, 0xFFFF80);
                            drawString(p_230430_1_,this.font, Guild.MyGuild.member[1], guild_centerx + 30, guild_centery + 98, 0xFFFF80);
                            drawString(p_230430_1_,this.font, Guild.MyGuild.member[2], guild_centerx + 30, guild_centery + 108, 0xFFFF80);
                            drawString(p_230430_1_,this.font, Guild.MyGuild.member[3], guild_centerx + 30, guild_centery + 118, 0xFFFF80);
                            drawString(p_230430_1_,this.font, Guild.MyGuild.member[4], guild_centerx + 30, guild_centery + 128, 0xFFFF80);

                            if (Guild.MyGuild.leader.equals(this.minecraft.player.getName().getString())) {
                                if (free_member > 0) {
                                    if(text_invite != null)
                                        text_invite.render(p_230430_1_,p_render_1_, p_render_2_, p_render_3_);
                                }
                            }
                            break;
                        case PAGE_MAP:
                            showClaimMap(p_230430_1_);
                            break;
                        default:
                    }

                    guild_centerx = (width / 2) - (guild_width / 2) + (guild_tabs / 2);
                    guild_centery = (height / 2) - (guild_height / 2);



                    drawCenteredString(p_230430_1_,this.font, I18n.format("guild.tab.overview", new Object[0]), guild_centerx - (guild_tabs / 2), guild_centery + 7, 0xFFFFFF);
                    drawCenteredString(p_230430_1_,this.font, I18n.format("guild.tab.members", new Object[0]), guild_centerx - (guild_tabs / 2), guild_centery + 21 + 7, 0xFFFFFF);
                    drawCenteredString(p_230430_1_,this.font, I18n.format("guild.tab.map", new Object[0]), guild_centerx - (guild_tabs / 2), guild_centery + 42 + 7, 0xFFFFFF);
                }
            }
        }catch(Exception ex)
        {
            LOGGER.error("Fehler beim Rendern der Gildendaten: "+ex.getMessage());
        }

        // Buttons
        super.render(p_230430_1_,p_render_1_, p_render_2_, p_render_3_);
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
            switch (button.id) {
                case BTN_CREATE:
                    String Gildenname = text.getText();
                    Main.NETWORK.sendToServer(new GuildCreate(Gildenname));
                    break;
                case BTN_ACCEPT:
                    Main.NETWORK.sendToServer(new GuildAccept(invite_accepts.get(button)));
                    buttons.clear();
                    break;
                case BTN_REFUSE:
                    Main.NETWORK.sendToServer(new GuildRefuse(invite_refuse.get(button)));
                    Guild.single_user_invites.remove(invite_refuse.get(button));
                    load_invites();
                    break;
                case BTN_PROMOTE:
                    Main.NETWORK.sendToServer(new GuildPromote(promote_demote.get(button)));
                    break;
                case BTN_DEMOTE:
                    Main.NETWORK.sendToServer(new GuildDemote(promote_demote.get(button)));
                    break;
                case BTN_KICK:
                    Main.NETWORK.sendToServer(new GuildKick(kick.get(button)));
                    break;
                case BTN_CLAIM:
                    Main.NETWORK.sendToServer(new GuildClaim(Guild.MyGuild.name));
                    break;
                case BTN_INVITE:
                    if (text_invite.getText().matches("[a-zA-Z0-9_]{1,16}")) {
                        Main.NETWORK.sendToServer(new GuildInviteUser("[\"" + Guild.MyGuild.name + "\",\"" + text_invite.getText() + "\"]"));
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
        Main.NETWORK.sendToServer(new GuildInvites());
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
                    int ly = ((Double)((y-centery)/23)).intValue();

                    switch(ly)
                    {
                        case PAGE_OVERVIEW:
                            current_page = PAGE_OVERVIEW;
                            break;
                        case PAGE_MEMBERS:
                            current_page = PAGE_MEMBERS;
                            break;
                        case PAGE_MAP:
                            current_page = PAGE_MAP;
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
                        boolean is_offi = false;
                        for(int i = 0; i < Guild.MyGuild.offi.length; i++)
                            if(Guild.MyGuild.offi[i] != null)
                                if(Guild.MyGuild.offi[i].length() > 0)
                                    is_offi = Guild.MyGuild.offi[i].equals(Minecraft.getInstance().player.getName().getString())||is_offi;

                        if(is_leader||is_offi) {
                            final int cx = (Minecraft.getInstance().player.getPosition().getX()) / 16 + (Minecraft.getInstance().player.getPosition().getX() < 0 ? -1 : 0);
                            final int cz = (Minecraft.getInstance().player.getPosition().getZ()) / 16 + (Minecraft.getInstance().player.getPosition().getZ() < 0 ? -1 : 0);

                            String dimen = Minecraft.getInstance().player.getEntity().getEntityWorld().func_234923_W_().toString();


                            boolean res = false;
                            if (Guild.all_claims.stream().filter(p -> p.x == cx && p.z == cz && p.dim.equals(dimen)).collect(Collectors.toList()).size() == 0 &&
                                    Guild.MyGuild.cash >= (((Double) Math.ceil((Guild.MyGuild.claims.size() + 1) * (Guild.MyGuild.claims.size() + 1) * Guild.guild_claim_factor)).intValue() * 100)) {
                                if (Guild.all_claims.stream().filter(p -> p.x == cx - 1 && p.z == cz && p.guild.equals(Guild.MyGuild.name) && p.dim.equals(dimen)).collect(Collectors.toList()).size() == 1)
                                    res = true;
                                else if (Guild.all_claims.stream().filter(p -> p.x == cx + 1 && p.z == cz && p.guild.equals(Guild.MyGuild.name) && p.dim.equals(dimen)).collect(Collectors.toList()).size() == 1)
                                    res = true;
                                else if (Guild.all_claims.stream().filter(p -> p.x == cx && p.z == cz - 1 && p.guild.equals(Guild.MyGuild.name) && p.dim.equals(dimen)).collect(Collectors.toList()).size() == 1)
                                    res = true;
                                else if (Guild.all_claims.stream().filter(p -> p.x == cx && p.z == cz + 1 && p.guild.equals(Guild.MyGuild.name) && p.dim.equals(dimen)).collect(Collectors.toList()).size() == 1)
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
                updateButton();
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

    public void showClaimMap(MatrixStack p_230430_1_)
    {
        final int x = (this.minecraft.player.getPosition().getX()) / 16 + (this.minecraft.player.getPosition().getX() < 0 ? -1 : 0);
        final int z = (this.minecraft.player.getPosition().getZ()) / 16 + (this.minecraft.player.getPosition().getZ() < 0 ? -1 : 0);
        final String dim = this.minecraft.player.getEntity().getEntityWorld().func_234923_W_().toString();

        final int dir_x = 13;
        final int dir_z = 15;

        List<Claim> claims = Guild.all_claims.stream().filter(p -> p.x >= x-dir_x && p.x <= x+dir_x && p.z >= z-dir_z && p.z <= z+dir_z && p.dim.equals(dim)).collect(Collectors.toList());

        for(int fz = 0; fz < 29; fz++)
            for(int fx = 0; fx < 25; fx++)
            {
                final int ffx = x-dir_x+fx+1;
                final int ffz = z-dir_z+fz+1;

                Object[] arr = claims.stream().filter(p -> p.x == ffx && p.z == ffz).toArray();
                if(arr.length > 0) {
                    if (((Claim) arr[0]).guild.equals(Guild.MyGuild.name))
                        blit(p_230430_1_,guild_centerx + 7+(fx*6), guild_centery + 3+(fz*6), 209, 21, 7, 7);
                    else
                        blit(p_230430_1_,guild_centerx+7+(fx*6), guild_centery+3+(fz*6), 202, 21, 7, 7);
                }else
                    blit(p_230430_1_,guild_centerx+7+(fx*6), guild_centery+3+(fz*6), 195, 21, 7, 7);

            }

        blit(p_230430_1_,guild_centerx+79, guild_centery+87, 216, 21, 7, 7);




    }
}
