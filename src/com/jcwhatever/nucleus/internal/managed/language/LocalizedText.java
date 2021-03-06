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


package com.jcwhatever.nucleus.internal.managed.language;

import com.jcwhatever.nucleus.utils.PreCon;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a single line from a language file.
 */
class LocalizedText {

    private static final Pattern PATTERN_ESCAPED_NEW_LINE = Pattern.compile("\\n");

    private final int _index;
    private final String _text;

    /**
     * Constructor.
     *
     * @param index  The key index.
     * @param text   The text.
     */
    public LocalizedText(int index, String text) {
        PreCon.notNull(text);

        _index = index;

        Matcher matcher = PATTERN_ESCAPED_NEW_LINE.matcher(text);
        _text = matcher.replaceAll("\n");
    }

    /**
     * Get the key index.
     */
    public int getIndex() {
        return _index;
    }

    /**
     * Get the text.
     */
    public String getText() {
        return _text;
    }

    @Override
    public int hashCode() {
        return _index;
    }

    @Override
    public boolean equals(Object obj) {

        return obj instanceof LocalizedText &&
                ((LocalizedText) obj).getIndex() == _index;
    }
}
