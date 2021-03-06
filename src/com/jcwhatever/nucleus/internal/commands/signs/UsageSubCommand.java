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


package com.jcwhatever.nucleus.internal.commands.signs;

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.internal.NucLang;
import com.jcwhatever.nucleus.managed.commands.CommandInfo;
import com.jcwhatever.nucleus.managed.commands.arguments.ICommandArguments;
import com.jcwhatever.nucleus.managed.commands.exceptions.CommandException;
import com.jcwhatever.nucleus.managed.commands.mixins.IExecutableCommand;
import com.jcwhatever.nucleus.managed.commands.utils.AbstractCommand;
import com.jcwhatever.nucleus.managed.language.Localizable;
import com.jcwhatever.nucleus.managed.messaging.ChatPaginator;
import com.jcwhatever.nucleus.managed.signs.SignHandler;
import com.jcwhatever.nucleus.utils.text.TextUtils;

import org.bukkit.command.CommandSender;

@CommandInfo(
        parent="signs",
        command="usage",
        staticParams={ "typeName" },
        description="Get usage information about a sign type.",

        paramDescriptions = {
                "typeName= The name of the sign type."})

class UsageSubCommand extends AbstractCommand implements IExecutableCommand {

    @Localizable static final String _PAGINATOR_TITLE =
            "Usage for '{0: sign type name}'";

    @Localizable static final String _HANDLER_NOT_FOUND =
            "A sign handler named '{0: sign type name}' was not found.";

    @Localizable static final String _FORMAT =
            "{GRAY}{0: line index} {WHITE}{1: centered text}";

    @Override
    public void execute(CommandSender sender, ICommandArguments args) throws CommandException {

        String typeName = args.getName("typeName");

        SignHandler handler = Nucleus.getSignManager().getSignHandler(typeName);
        if (handler == null)
            throw new CommandException(NucLang.get(_HANDLER_NOT_FOUND, typeName));

        ChatPaginator pagin = createPagin(args, 7, NucLang.get(_PAGINATOR_TITLE, handler.getName()));

        String[] usage = handler.getUsage();

        for (int i=0; i < 4; i++) {

            String line = usage[i];
            if (line == null)
                line = "";

            int centerPadding = (int)Math.round(Math.max(0.0D, (32.0D - line.length()) / 2.0D));

            pagin.add((i + 1) + ".", TextUtils.padLeft(line, centerPadding));
        }

        pagin.show(sender, 1, NucLang.get(_FORMAT));
    }
}