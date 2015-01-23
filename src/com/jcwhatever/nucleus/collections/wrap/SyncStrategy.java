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

package com.jcwhatever.nucleus.collections.wrap;

import javax.annotation.Nullable;

/**
 * A strategy for synchronization that provides the sync object.
 */
public class SyncStrategy {

    /**
     * No synchronization. {@code #getSync} returns null.
     */
    public static final SyncStrategy NONE = new SyncStrategy(null);

    /**
     * Synchronize on source. {@code #getSync} returns the source object.
     */
    public static final SyncStrategy SYNC = new SyncStrategy(null);

    private final Object _sync;

    /**
     * Constructor.
     *
     * @param sync  The synchronization object to use.
     */
    public SyncStrategy(@Nullable Object sync) {
        _sync = sync;
    }

    /**
     * Get the synchronization object to use.
     *
     * @param source  The source object that needs synchronization.
     *
     * @return  The synchronization object.
     */
    @Nullable
    public Object getSync(Object source) {
        if (this == SyncStrategy.NONE) {
            return null;
        }
        else {
            return this == SyncStrategy.SYNC
                    ? source
                    : _sync;
        }
    }
}