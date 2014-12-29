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


package com.jcwhatever.nucleus.internal.commands.kits;

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.commands.AbstractCommand;
import com.jcwhatever.nucleus.commands.CommandInfo;
import com.jcwhatever.nucleus.commands.arguments.CommandArguments;
import com.jcwhatever.nucleus.commands.exceptions.CommandException;
import com.jcwhatever.nucleus.internal.Lang;
import com.jcwhatever.nucleus.kits.IKit;
import com.jcwhatever.nucleus.kits.KitManager;
import com.jcwhatever.nucleus.language.Localizable;

import org.bukkit.command.CommandSender;

@CommandInfo(
        parent="kits",
        command="add",
        staticParams={ "kitName" },
        description="Add a new chest kit.",

        paramDescriptions = {
                "kitName= The name of the kit items will be added to. {NAME16}"})

public final class AddSubCommand extends AbstractCommand {

    @Localizable
    static final String _KIT_ALREADY_EXISTS = "An chest kit named '{0}' already exists.";
    @Localizable static final String _FAILED = "Failed to create chest kit.";
    @Localizable static final String _SUCCESS = "Inventory kit '{0}' created.";

    @Override
    public void execute(CommandSender sender, CommandArguments args) throws CommandException {

        String kitName = args.getName("kitName");

        KitManager manager = Nucleus.getKitManager();

        IKit kit = manager.getKit(kitName);
        if (kit != null) {
            tellError(sender, Lang.get(_KIT_ALREADY_EXISTS, kitName));
            return; // finish
        }

        kit = manager.addKit(kitName);
        if (kit == null) {
            tellError(sender, Lang.get(_FAILED));
            return; // finish
        }

        tellSuccess(sender, Lang.get(_SUCCESS, kit.getName()));
    }
}