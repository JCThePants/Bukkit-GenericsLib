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

package com.jcwhatever.nucleus.internal.commands.economy;

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.internal.commands.economy.admin.AdminCommand;
import com.jcwhatever.nucleus.managed.commands.CommandInfo;
import com.jcwhatever.nucleus.managed.commands.IRegisteredCommand;
import com.jcwhatever.nucleus.managed.commands.arguments.ICommandArguments;
import com.jcwhatever.nucleus.managed.commands.exceptions.CommandException;
import com.jcwhatever.nucleus.managed.commands.mixins.IExecutableCommand;
import com.jcwhatever.nucleus.managed.commands.utils.AbstractCommand;
import com.jcwhatever.nucleus.managed.commands.utils.ICommandUsageGenerator;

import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

@CommandInfo(
        command="neconomy",
        description="Economy commands.",
        permissionDefault = PermissionDefault.TRUE)

public final class NEconomyCommand extends AbstractCommand implements IExecutableCommand {

    public NEconomyCommand() {
        super();

        registerCommand(AdminCommand.class);

        registerCommand(BalanceSubCommand.class);
        registerCommand(SendBankSubCommand.class);
        registerCommand(SendSubCommand.class);
    }

    @Override
    public void execute(CommandSender sender, ICommandArguments args) throws CommandException {

        // show balance
        IRegisteredCommand command = getCommand("balance");
        assert command != null;

        if (command.getCommand() instanceof IExecutableCommand)
            ((IExecutableCommand) command.getCommand()).execute(sender, args.forCommand(command));

        ICommandUsageGenerator generator =
                Nucleus.getCommandManager().getUsageGenerator(ICommandUsageGenerator.INLINE_HELP);

        tell(sender, "{GRAY}Type '{0: usage}' for more commands.", generator.generate(getRegistered()));
    }
}