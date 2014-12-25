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

package com.jcwhatever.generic.views.workbench;

import com.jcwhatever.generic.utils.PreCon;
import com.jcwhatever.generic.views.ViewFactory;
import com.jcwhatever.generic.views.ViewSession;
import com.jcwhatever.generic.views.data.ViewArguments;

import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;

/**
 * Factory for creating new {@code WorkbenchView} instances.
 */
public class WorkbenchViewFactory extends ViewFactory {

    /**
     * Constructor.
     *
     * @param plugin  The owning plugin.
     * @param name    The factory's name.
     */
    public WorkbenchViewFactory(Plugin plugin, String name) {
        super(plugin, name);
    }

    @Override
    public WorkbenchView create(@Nullable String title, ViewSession session,
                                ViewArguments arguments) {
        PreCon.notNull(session);
        PreCon.notNull(arguments);

        return new WorkbenchView(session, this, arguments);
    }

    @Override
    protected void onDispose() {
        // do nothing
    }
}