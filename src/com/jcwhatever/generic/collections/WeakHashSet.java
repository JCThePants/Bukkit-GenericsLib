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

package com.jcwhatever.generic.collections;

import com.jcwhatever.generic.collections.wrappers.AbstractSetWrapper;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * A hash {@code Set} with weak referenced values.
 */
public class WeakHashSet<E> extends AbstractSetWrapper<E> {

    private final transient Set<E> _set;

    public WeakHashSet() {
        this(10);
    }

    public WeakHashSet(int size) {
        _set = Collections.newSetFromMap(new WeakHashMap<E, Boolean>(size));
    }

    public WeakHashSet(Collection<E> collection) {
        _set = Collections.newSetFromMap(new WeakHashMap<E, Boolean>(collection.size() + 5));
        _set.addAll(collection);
    }

    @Override
    protected Collection<E> getCollection() {
        return _set;
    }
}