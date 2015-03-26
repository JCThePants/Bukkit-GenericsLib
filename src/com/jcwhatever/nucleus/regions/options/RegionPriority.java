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

package com.jcwhatever.nucleus.regions.options;

/**
 * The priority/order the region is handled by the
 * region manager in relation to other regions.
 */
public enum RegionPriority {
    /**
     * The last to be handled.
     */
    LAST      (4),
    /**
     * Low priority. Handled second to last.
     */
    LOW       (3),
    /**
     * Normal priority.
     */
    DEFAULT   (2),
    /**
     * High priority. Handled second.
     */
    HIGH      (1),
    /**
     * Highest priority. Handled first.
     */
    FIRST     (0);

    private final int _order;

    RegionPriority(int order) {
        _order = order;
    }

    /**
     * Get a sort order index number.
     */
    public int getSortOrder() {
        return _order;
    }

    /**
     * Type of region priority.
     */
    public enum PriorityType {
        ENTER,
        LEAVE
    }
}
