package de.bloody9.raze.features.clanrole;

import de.bloody9.core.feature.Feature;
import de.bloody9.raze.features.clanrole.commands.ClanRoleCommand;
import org.jetbrains.annotations.NotNull;

public class ClanRoleFeature extends Feature {

    public static Feature INSTANCE;


    /**
     *  required sql functions:
     *
     *  # clan role feature
     *  CREATE TABLE clan_roles
     *  (
     * 	    guild_id char(18) primary key,
     * 	    role_id char(18)
     *  );
    */
    public ClanRoleFeature() {
        super();

        INSTANCE = this;

        addCommands();
    }

    @Override
    public @NotNull String getName() {
        return "ClanRoleFeature";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    public void addCommands() {
        addCommand("clan", new ClanRoleCommand());
    }
}
