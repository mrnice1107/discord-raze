package de.bloody9.raze.features.clanrole.commands;

import static de.bloody9.core.logging.Logger.*;
import static de.bloody9.core.helper.PermissionHelper.memberHasPermission;

import de.bloody9.core.exceptions.Command.*;
import de.bloody9.core.exceptions.Feature.FeatureCommandException;
import de.bloody9.core.exceptions.Mentioned.NoMentionedMembersCommandException;
import de.bloody9.core.exceptions.Mentioned.NoMentionedRolesCommandException;
import de.bloody9.core.helper.Helper;
import de.bloody9.core.models.interfaces.BotCommand;
import de.bloody9.core.models.objects.GuildObject;
import de.bloody9.core.models.objects.PermissionObject;
import de.bloody9.raze.features.clanrole.ClanRoleFeature;
import net.dv8tion.jda.api.entities.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ClanRoleCommand implements BotCommand {

    private static final String TABLE = "clan_roles";
    private static final String GUILD_ID = "guild_id";
    private static final String ROLE_ID = "role_id";

    @SuppressWarnings("SpellCheckingInspection")
    private static final String generalPermission = "commands.clanrole";
    @SuppressWarnings("SpellCheckingInspection")
    private static final String managePermission = "commands.clanrole.manage";

    private final List<PermissionObject> permissionObjects;
    private final List<String> aliases;

    private final Map<Guild, Role> clanRoles;

    public ClanRoleCommand() {

        clanRoles = new HashMap<>();

        permissionObjects = new ArrayList<>();
        permissionObjects.add(new PermissionObject(generalPermission, "Execute ClanRoleCommand"));
        permissionObjects.add(new PermissionObject(managePermission, "Set new clan role"));

        aliases = new ArrayList<>();
        //noinspection SpellCheckingInspection
        aliases.add("unclan");
        //noinspection SpellCheckingInspection
        aliases.add("clanset");
        //noinspection SpellCheckingInspection
        aliases.add("setclan");
    }

    private void sendHelp(User user) {
        Helper.sendPrivateMessage(user, getHelp());
    }

    @Override
    public String getDescription() {
        return "With the clan role command you can set a role.\n" +
                "Members with the permission to execute this command can then give/remove a clan role to/from members";
    }

    @Override
    public String getHelp() {
        //noinspection SpellCheckingInspection
        return Helper.constructHelp("ClanRole Command\n" +
                "<prefix> clan <@member> | *add the clan role to the mentioned member*\n" +
                "<prefix> unclan <@member> | *remove the clan role from the mentioned member*\n" +
                "<prefix> clanset <@role / role id> | *set a new clan role*\n" +
                "<prefix> clan get | *returns the current clan role*");
    }

    @Override
    public List<PermissionObject> getPermissions() {
        return permissionObjects;
    }

    @Override
    public List<String> getAlias() {
        return aliases;
    }

    // if return true the initial command message will be removed
    @Override
    public boolean performCommand(String command, User sender, Message message, String[] args) {

        debug("start ClanRoleCommand");

        if (!memberHasPermission(generalPermission, message.getMember())) {
            throw new NoPermissionCommandException(sender, generalPermission);
        }

        debug("checking args.length > 0");
        if (args.length <= 0) {
            sendHelp(sender);

            throw new NotEnoughArgumentCommandException(args.length);
        }

        debug("check if args[0] == help: " + args[0].toLowerCase());
        if (args[0].equalsIgnoreCase("help")) {
            sendHelp(sender);

            return true;
        }

        final Guild guild = message.getGuild();

        // SET CLAN ROLE
        //noinspection SpellCheckingInspection
        if (command.equalsIgnoreCase("clanset") || command.equalsIgnoreCase("setclan")) {
            if (!memberHasPermission(managePermission, message.getMember())) {
                throw new NoPermissionCommandException(sender, managePermission);
            }

            Role role = null;
            try {
                role = guild.getRoleById(args[0]);
            } catch (NumberFormatException ignored) {}

            if (role == null && message.getMentionedRoles().isEmpty()) {

                Helper.sendPrivateMessage("You need to mention a @role for this command!", sender);
                throw new NoMentionedRolesCommandException();
            }
            if (role == null) { role = message.getMentionedRoles().get(0); }
            setClanRole(role);

            return true;
        }

        Role clanRole = getClanRole(guild, sender);

        if (args[0].equalsIgnoreCase("get")) {
            Helper.sendPrivateMessage("The clan role is: @" + clanRole.getName() + " (" + clanRole.getId() + ")", sender);
            return true;
        }

        // CLAN / UN CLAN
        if (message.getMentionedMembers().isEmpty()) {
            Helper.sendPrivateMessage("You need to mention members with @member!", sender);

            throw new NoMentionedMembersCommandException();
        }

        Member member = message.getMentionedMembers().get(0);
        //noinspection SpellCheckingInspection
        if (command.equalsIgnoreCase("unclan")) {
            guild.removeRoleFromMember(member, clanRole).queue();
        } else {
            guild.addRoleToMember(member, clanRole).queue();
        }

        return true;
    }

    private void setClanRole(@NotNull Role role) {
        clanRoles.put(role.getGuild(), role);

        GuildObject guildObject = new GuildObject(role.getGuild());

        final String contentType = GUILD_ID + "," + ROLE_ID;
        final String content = guildObject.getGuildId() + "," + role.getId();
        final String onUpdate = ROLE_ID + "=" + role.getId();

        Helper.executeInsertUpdateOnDuplicateSQL(TABLE, contentType, content, onUpdate);

        guildObject.modLog("Clan role changed: " + role.getAsMention());
    }

    @NotNull
    private Role getClanRole(@NotNull Guild guild, @NotNull User sender) {
        Role clanRole = clanRoles.get(guild);

        if (clanRole == null) {
            final String query = GUILD_ID + "=" + guild.getId();
            final String roleId = Helper.getFirstObjectFromDB(ROLE_ID, TABLE, query);

            if (roleId == null) {
                Helper.sendPrivateMessage("There is no clan role defined!", sender);
                throw new FeatureCommandException(ClanRoleFeature.INSTANCE, "Failed to get clan role from database, undefined");
            }

            clanRole = guild.getRoleById(roleId);

            if (clanRole == null) {
                Helper.sendPrivateMessage("Failed to get the clan role, the role got deleted or is for the bot unreachable!", sender);

                throw new FeatureCommandException(ClanRoleFeature.INSTANCE, "Failed to get clan role from database, role deleted");
            }
        }

        return clanRole;
    }
}