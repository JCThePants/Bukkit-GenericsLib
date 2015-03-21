/*
 * This file is part of NucleusFramework for Bukkit, licensed under the MIT License (MIT).
 *
 * Copyright (c) JCThePants (www.jcwhatever.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */


package com.jcwhatever.nucleus.internal.commands.jail;

import com.jcwhatever.nucleus.commands.AbstractCommand;
import com.jcwhatever.nucleus.commands.CommandInfo;
import com.jcwhatever.nucleus.commands.arguments.CommandArguments;
import com.jcwhatever.nucleus.commands.exceptions.CommandException;
import com.jcwhatever.nucleus.internal.NucLang;
import com.jcwhatever.nucleus.providers.jail.IJail;
import com.jcwhatever.nucleus.utils.Jails;
import com.jcwhatever.nucleus.utils.coords.NamedLocation;
import com.jcwhatever.nucleus.utils.language.Localizable;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandInfo(
        parent="jail",
        command="addtp",
        staticParams = { "name" },
        description="Add a location where players are teleported within the jail using your current position.",

        paramDescriptions = { "name= The name of the tp location. {NAME16}"})

public final class AddTPSubCommand extends AbstractCommand {

    @Localizable
    static final String _DUPLICATE_NAME = "There is already a location named '{0}'.";
    @Localizable static final String _FAILED = "Failed to add location.";
    @Localizable static final String _SUCCESS = "Your current location has been added to the default jail and is named '{0}'.";

    @Override
    public void execute(CommandSender sender, CommandArguments args) throws CommandException {

        CommandException.checkNotConsole(this, sender);

        String name = args.getName("name");

        Player p = (Player)sender;

        Location loc = p.getLocation();

        IJail jail = Jails.getServerJail();

        NamedLocation current = jail.getTeleport(name);
        if (current != null) {
            tellError(sender, NucLang.get(_DUPLICATE_NAME, name));
            return; // finished
        }

        if (!jail.addTeleport(name, loc)) {
            tellError(sender, NucLang.get(_FAILED));
            return; // finished
        }

        tellSuccess(sender, NucLang.get(_SUCCESS, name));
    }

}
