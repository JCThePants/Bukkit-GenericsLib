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

package com.jcwhatever.nucleus.internal.commands.respacks;

import com.jcwhatever.nucleus.internal.NucLang;
import com.jcwhatever.nucleus.internal.commands.kits.AbstractKitCommand;
import com.jcwhatever.nucleus.managed.commands.CommandInfo;
import com.jcwhatever.nucleus.managed.commands.arguments.ICommandArguments;
import com.jcwhatever.nucleus.managed.commands.exceptions.CommandException;
import com.jcwhatever.nucleus.managed.commands.mixins.IExecutableCommand;
import com.jcwhatever.nucleus.managed.language.Localizable;
import com.jcwhatever.nucleus.managed.resourcepacks.IPlayerResourcePacks;
import com.jcwhatever.nucleus.managed.resourcepacks.IResourcePack;
import com.jcwhatever.nucleus.managed.resourcepacks.ResourcePacks;
import com.jcwhatever.nucleus.utils.player.PlayerUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandInfo(
        parent="respacks",
        command="status",
        staticParams={ "playerName=" },
        description="View the status of a players resource pack.",

        paramDescriptions = {
                "playerName= Optional. The name of the player. If omitted, "
                        + "the command sender is used."
        })

class StatusSubCommand extends AbstractKitCommand implements IExecutableCommand {

    @Localizable static final String _PLAYER_NOT_FOUND = "A player named '{0}' was not found.";
    @Localizable static final String _SUCCESS = "Player '{0}' is viewing resource pack '{1}'. Current status is {2}.";

    @Override
    public void execute(CommandSender sender, ICommandArguments args) throws CommandException {

        Player player;

        if (args.isDefaultValue("playerName")) {
            CommandException.checkNotConsole(getRegistered(), sender);

            player = (Player)sender;
        }
        else {
            String playerName = args.getString("playerName");
            player = PlayerUtils.getPlayer(playerName);
            if (player == null)
                throw new CommandException(NucLang.get(_PLAYER_NOT_FOUND, playerName));
        }

        IPlayerResourcePacks packs = ResourcePacks.get(player);
        IResourcePack current = packs.getCurrent();

        tellSuccess(sender, NucLang.get(_SUCCESS,
                player.getName(), current == null ? "<>" : current.getName(), packs.getStatus().name()));
    }
}

