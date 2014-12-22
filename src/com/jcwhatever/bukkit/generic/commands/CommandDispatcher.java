/*
 * This file is part of GenericsLib for Bukkit, licensed under the MIT License (MIT).
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

package com.jcwhatever.bukkit.generic.commands;

import com.jcwhatever.bukkit.generic.commands.CommandParser.ParsedCommand;
import com.jcwhatever.bukkit.generic.commands.CommandParser.ParsedTabComplete;
import com.jcwhatever.bukkit.generic.commands.arguments.CommandArguments;
import com.jcwhatever.bukkit.generic.commands.exceptions.DuplicateParameterException;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidArgumentException;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidCommandSenderException;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidParameterException;
import com.jcwhatever.bukkit.generic.commands.exceptions.MissingArgumentException;
import com.jcwhatever.bukkit.generic.commands.exceptions.TooManyArgsException;
import com.jcwhatever.bukkit.generic.internal.Lang;
import com.jcwhatever.bukkit.generic.language.Localizable;
import com.jcwhatever.bukkit.generic.messaging.IMessenger;
import com.jcwhatever.bukkit.generic.messaging.MessengerFactory;
import com.jcwhatever.bukkit.generic.mixins.IPluginOwned;
import com.jcwhatever.bukkit.generic.permissions.Permissions;
import com.jcwhatever.bukkit.generic.utils.ArrayUtils;
import com.jcwhatever.bukkit.generic.utils.PreCon;
import com.jcwhatever.bukkit.generic.utils.text.TextUtils;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;

/*
 * 
 */
public class CommandDispatcher implements
        CommandExecutor, TabCompleter, ICommandOwner, IPluginOwned {

    @Localizable static final String _TO_MANY_ARGS = "Too many arguments. Type '{0}' for help.";
    @Localizable static final String _MISSING_ARGS = "Missing arguments. Type '{0}' for help.";
    @Localizable static final String _ACCESS_DENIED = "Access denied.";
    @Localizable static final String _COMMAND_INCOMPLETE = "Command incomplete. Type '/{0} ?' for help.";
    @Localizable static final String _COMMAND_NOT_FOUND = "Command not found. Type '/{0} ?' for help.";
    @Localizable static final String _INVALID_PARAMETER = "Invalid parameter detected: {0}";
    @Localizable static final String _DUPLICATE_PARAMETER = "The parameter named '{0}' has a duplicate.";
    @Localizable static final String _CANT_EXECUTE_AS = "Cannot execute command as {0}.";
    @Localizable static final String _PARAMETER_DESCRIPTION = "{WHITE}Parameter description: {GRAY}{0}";
    @Localizable static final String _REASON = "Reason: {0}";

    private final Plugin _plugin;
    private AboutCommand _defaultRoot;
    private CommandCollection _rootCommands;
    private final IMessenger _msg;
    private final Set<String> _pluginCommands;
    private final CommandUtils _utils;
    private final UsageGenerator _usageGenerator;

    public CommandDispatcher(Plugin plugin) {
        PreCon.notNull(plugin);
        PreCon.isValid(plugin instanceof JavaPlugin);

        _plugin = plugin;

        _rootCommands = new CommandCollection();
        _msg = MessengerFactory.create(plugin);
        _utils = new CommandUtils(plugin);
        _usageGenerator = new UsageGenerator();

        _pluginCommands = new HashSet<>(plugin.getDescription().getCommands().keySet());

        _defaultRoot = new AboutCommand();
        _defaultRoot.setDispatcher(this, null);

        registerCommands();
    }

    @Override
    public Plugin getPlugin() {
        return _plugin;
    }

    public CommandUtils getUtils() {
        return _utils;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String rootName, String[] rootArguments) {

        AbstractCommand rootCommand = _rootCommands.getCommand(rootName);
        if (rootCommand == null) {
            rootCommand = _defaultRoot;
        }

        rootCommand.getInfo().setSessionRootName(rootName);

        CommandParser parser = new CommandParser(rootCommand);
        ParsedCommand parsed = parser.parseCommand(rootCommand.getCommandCollection(), rootArguments);

        if (parsed == null) {
            // command not found
            _utils.tellError(sender, Lang.get(_COMMAND_NOT_FOUND, rootName));
            return true; // finish
        }

        AbstractCommand command = parsed.getCommand();
        String[] rawArguments = parsed.getArguments();

        // Check if the player has permissions to run the command
        if (!Permissions.has(sender, command.getPermission().getName())) {
            _utils.tellError(sender, Lang.get(_ACCESS_DENIED));
            return true;
        }

        // handle command help, display if the command argument is '?' or 'help'
        if (isCommandHelp(rawArguments)) {

            int page = TextUtils.parseInt(
                    ArrayUtils.get(rawArguments, 1, null), 1);

            command.showHelp(sender, page);

            return true; // finished
        }

        if (isDetailedHelp(rawArguments)) {

            int page = TextUtils.parseInt(
                    ArrayUtils.get(rawArguments, 1, null), 1);

            command.showDetailedHelp(sender, page);

            return true; // finished
        }

        // Determine if the command can execute or if it requires sub commands
        if (!command.canExecute()) {
            _utils.tellError(sender, Lang.get(_COMMAND_INCOMPLETE, rootName));
            return true; // finished
        }

        // get arguments
        CommandArguments arguments = getCommandArguments(sender, command, rawArguments, true);
        if (arguments == null)
            return true; // finished

        // execute the command
        executeCommand(sender, command, arguments, true);

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String s, String[] args) {

        if (args.length == 0)
            return new ArrayList<>(0);

        AbstractCommand rootCommand = _rootCommands.getCommand(args[0]);
        if (rootCommand == null) {
            rootCommand = _defaultRoot;
        }

        CommandParser _parser = new CommandParser(rootCommand);
        ParsedTabComplete parsed = _parser.parseTabComplete(rootCommand, sender, args);

        AbstractCommand command = parsed.getCommand();
        String[] arguments = parsed.getArguments();
        List<String> matches = parsed.getMatches();

        if (command != null) {

            // give the command the opportunity to modify the list
            command.onTabComplete(
                    sender,
                    arguments,
                    matches);
        }

        // add the command help
        if (command != null && matches.size() > 1 &&
                (arguments.length == 0 ||
                        (arguments.length == 1 && arguments[0].isEmpty()))) {
            parsed.getMatches().add("?");
        }

        return parsed.getMatches();
    }

    @Override
    public boolean registerCommand(Class<? extends AbstractCommand> commandClass) {
        PreCon.notNull(commandClass);

        String rootName = _rootCommands.addCommand(commandClass);
        if (rootName == null) {
            _msg.debug("Failed to register command '{0}' possibly because another command with the " +
                            "same name is already registered and no alternative command names were provided.",
                    commandClass.getName());
            return false;
        }

        AbstractCommand command = _rootCommands.getCommand(rootName);
        if (command == null)
            throw new AssertionError();

        if (!_pluginCommands.contains(rootName)) {
            _rootCommands.removeAll(command);
            if (!_defaultRoot.registerCommand(commandClass))
                return false;

            command = _defaultRoot.getCommandCollection().getCommand(commandClass);
            if (command == null)
                throw new AssertionError();
        }

        command.setDispatcher(this, null);
        return true;
    }

    @Override
    public boolean unregisterCommand(Class<? extends AbstractCommand> commandClass) {

        if (!_rootCommands.unregisterCommand(commandClass) &&
                !_defaultRoot.unregisterCommand(commandClass)) {
            return false;
        }
        return true;
    }

    @Nullable
    @Override
    public AbstractCommand getCommand(String commandName) {

        AbstractCommand result = _rootCommands.getCommand(commandName);
        if (result == null) {
            result = _defaultRoot.getCommand(commandName);
        }

        return result;
    }

    @Override
    public Collection<AbstractCommand> getCommands() {
        Collection<AbstractCommand> commands = _rootCommands.getCommands();
        commands.add(_defaultRoot);
        return commands;
    }

    @Override
    public Collection<String> getCommandNames() {
        return _rootCommands.getCommandNames();
    }

    private boolean isCommandHelp(String[] args) {
        return args.length > 0 &&
                ((args[0].equals("?")) || args[0].equalsIgnoreCase("help"));
    }

    private boolean isDetailedHelp(String[] args) {
        return args.length > 0 &&
                ((args[0].equals("??")));
    }

    protected void registerCommands () {
        // do nothing
    }

    @Nullable
    private CommandArguments getCommandArguments(CommandSender sender,
                                                 AbstractCommand command,
                                                 String[] argArray,
                                                 boolean showMessages) {
        // Parse command arguments
        CommandArguments arguments;

        try {
            arguments = new CommandArguments(getPlugin(), command, argArray);

        } catch (TooManyArgsException e) {

            if (showMessages) {

                _utils.tellError(sender, Lang.get(_TO_MANY_ARGS,
                        _usageGenerator.generate(command)));
            }

            return null; // finished

        } catch (InvalidArgumentException e) {

            if (showMessages && e.getMessage() != null) {
                _utils.tellError(sender, e.getMessage());

                if (e.getParameterDescription() != null) {
                    _utils.tell(sender, Lang.get(_PARAMETER_DESCRIPTION, e.getParameterDescription()));
                }
            }

            return null; // finished

        } catch (DuplicateParameterException e) {

            if (showMessages) {
                if (e.getMessage() != null) {
                    _utils.tellError(sender, e.getMessage());
                } else {
                    _utils.tellError(sender, Lang.get(_DUPLICATE_PARAMETER, e.getParameterName()));
                }
            }

            return null; // finished

        } catch (InvalidParameterException e) {

            if (showMessages)
                _utils.tellError(sender, Lang.get(_INVALID_PARAMETER, e.getParameterName()));

            return null; // finished
        } catch (MissingArgumentException e) {
            e.printStackTrace();

            if (showMessages)
                _utils.tellError(sender, Lang.get(_MISSING_ARGS,
                        _usageGenerator.generate(command)));

            return null;
        }

        return arguments;
    }

    private boolean executeCommand(CommandSender sender, AbstractCommand command,
                                   CommandArguments parameters, boolean showMessages) {
        // execute the command
        try {
            command.execute(sender, parameters);
        }
        // catch invalid argument values
        catch (InvalidArgumentException e) {

            if (showMessages && e.getMessage() != null) {
                _utils.tellError(sender, e.getMessage());

                if (e.getParameterDescription() != null) {
                    _utils.tell(sender, Lang.get(_PARAMETER_DESCRIPTION, e.getParameterDescription()));
                }
            }
            return false;
        }
        // catch invalid command senders
        catch (InvalidCommandSenderException e) {
            if (showMessages) {
                _utils.tellError(sender, Lang.get(_CANT_EXECUTE_AS, e.getSenderType().name()));

                if (e.getReason() != null) {
                    _utils.tellError(sender, Lang.get(_REASON, e.getReason()));
                }
            }
            return false;
        }
        return true;
    }
}
