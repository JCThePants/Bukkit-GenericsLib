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

package com.jcwhatever.nucleus.collections.timed;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.jcwhatever.nucleus.utils.TimeScale;

import org.bukkit.plugin.Plugin;

/**
 * An implementation of {@link TimedMultimap} that utilizes an internal
 * {@link Multimap} with hash keys and hash set values.
 *
 * <p>Thread safe.</p>
 *
 * <p>The sets iterators must be used inside a synchronized block which locks the
 * set instance. Otherwise, a {@link java.lang.IllegalStateException} is thrown.</p>
 */
public class TimedSetMultimap<K, V> extends TimedMultimap<K, V> {

    /**
     * Constructor.
     *
     * @param plugin  The owning plugin.
     */
    public TimedSetMultimap(Plugin plugin) {
        super(plugin);
    }

    /**
     * Constructor.
     *
     * @param plugin           The owning plugin.
     * @param defaultLifespan  The default lifespan for keys.
     * @param timeScale        The time scale of the specified lifespan.
     */
    public TimedSetMultimap(Plugin plugin, int defaultLifespan, TimeScale timeScale) {
        super(plugin, defaultLifespan, timeScale);
    }

    @Override
    protected Multimap<K, V> createMultimap() {
        return MultimapBuilder.hashKeys().hashSetValues().build();
    }
}
