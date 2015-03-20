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


package com.jcwhatever.nucleus.utils.signs;

import com.jcwhatever.nucleus.mixins.INamedInsensitive;
import com.jcwhatever.nucleus.mixins.IPluginOwned;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.text.TextUtils;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.regex.Matcher;

/**
 * Handles actions for a specific sign type.
 */
public abstract class SignHandler implements INamedInsensitive, IPluginOwned {

    private final Plugin _plugin;
    private final String _name;
    private final String _searchName;
    private String _displayName;

    /**
     * Specify the result of a sign change event.
     */
    public enum SignChangeResult {
        /**
         * The sign is valid.
         */
        VALID,
        /**
         * The sign is not valid.
         */
        INVALID
    }

    public enum SignClickResult {
        HANDLED,
        IGNORED
    }

    public enum SignBreakResult {
        ALLOW,
        DENY
    }

    /**
     * Constructor.
     *
     * @param plugin  The owning plugin.
     * @param name    The name of the sign (Used in the sign header)
     */
    public SignHandler(Plugin plugin, String name) {
        PreCon.notNull(plugin);
        PreCon.notNullOrEmpty(name);

        _plugin = plugin;
        _name = name;
        _searchName = name.toLowerCase();
    }

    /**
     * The owning plugin.
     */
    @Override
    public final Plugin getPlugin() {
        return _plugin;
    }

    /**
     * The name of the sign handler, must be a valid name.
     * Starts with a letter, alphanumerics only. Underscores allowed.
     */
    @Override
    public final String getName() {
        return _name;
    }

    /**
     * Get the sign handler name in all lower case.
     */
    @Override
    public final String getSearchName() {
        return _searchName;
    }

    /**
     * Get a display name for the sign. Returns the sign handler name
     * with underscores converted to spaces.
     */
    public final String getDisplayName() {
        if (_displayName == null) {
            Matcher headerMatcher = TextUtils.PATTERN_UNDERSCORE.matcher(getName());
            _displayName = headerMatcher.replaceAll(" ");
        }

        return _displayName;
    }

    /**
     * Get a prefix to append to the header of a sign.
     */
    public abstract String getHeaderPrefix();

    /**
     * Get a description of the sign handler.
     */
    public abstract String getDescription();

    /**
     * Get a string array describing sign usage. Each element of
     * the array represents a line on the sign. There must be 4 elements
     * in the array.
     */
    public abstract String[] getUsage();

    /**
     * Invoked when a sign handled by the sign handler is
     * loaded from the sign manager data node.
     *
     * @param sign  The loaded sign encapsulated.
     */
    protected abstract void onSignLoad(SignContainer sign);

    /**
     * Invoked when a sign handled by the sign handler is
     * changed/created.
     *
     * @param p     The player who changed/created the sign.
     * @param sign  The encapsulated sign.
     *
     * @return  {@literal VALID} if the change is valid/allowed, otherwise {@literal INVALID}.
     */
    protected abstract SignChangeResult onSignChange(Player p, SignContainer sign);

    /**
     * Invoked when a sign handled by the sign handler is
     * clicked on by a player.
     *
     * @param p     The player who clicked the sign.
     * @param sign  The encapsulated sign.
     *
     * @return  {@literal HANDLED} if the click was valid and handled,
     * otherwise {@literal CANCEL}.
     */
    protected abstract SignClickResult onSignClick(Player p, SignContainer sign);

    /**
     * Invoked when a sign handled by the sign handler is
     * broken by a player. Sign break is only invoked when the
     * player is capable of breaking a sign instantly. (i.e creative mode)
     * Therefore the sign cannot be broken unless the player is capable of
     * instant break.
     *
     * @param p     The player who is breaking the sign.
     * @param sign  The encapsulated sign.
     *
     * @return  {@literal ALLOW} if the break is allowed, otherwise {@literal DENY}.
     */
    protected abstract SignBreakResult onSignBreak(Player p, SignContainer sign);
}