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

package com.jcwhatever.generic.collections.timed;

import java.util.Collection;

/**
 * Represents a collection whose elements have individual
 * lifespans. When the elements lifespan ends, the element is
 * removed.
 */
public interface ITimedCollection<E> extends Collection<E> {

    /**
     * Add an item to the collection and specify its lifetime in ticks.
     *
     * @param item           The item to add.
     * @param lifespanTicks  The amount of time in ticks it will stay in the list.
     */
    boolean add(E item, int lifespanTicks);

    /**
     * Add a collection to the list and specify the lifetime in ticks.
     *
     * @param collection       The collection to add.
     * @param lifespanTicks    The amount of time in ticks it will stay in the list.
     */
    boolean addAll(Collection<? extends E> collection, int lifespanTicks);
}