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

package com.jcwhatever.generic.utils.text;

import com.jcwhatever.generic.utils.PreCon;

import javax.annotation.Nullable;

/**
 * A segment of text that begins with a color/format.
 */
public class TextComponent {

    private final TextColor _textColor;
    private final String _text;
    private String _formatted;
    private int _hash;

    /**
     * Constructor.
     *
     * <p>No color/formatting.</p>
     *
     * @param text  The text of the component.
     */
    public TextComponent(String text) {
        this(null, text);
    }

    /**
     * Constructor.
     *
     * @param textColor  The text color/format.
     * @param text       The text of the component.
     */
    public TextComponent(@Nullable TextColor textColor, String text) {
        PreCon.notNull(text);

        _textColor = textColor;
        _text = text;

        _hash = text.hashCode();

        if (textColor != null) {
            _hash ^= textColor.hashCode();
        }
    }

    /**
     * Get the text color.
     */
    @Nullable
    public TextColor getTextColor() {
        return _textColor;
    }

    /**
     * Get the raw text.
     */
    public String getText() {
        return _text;
    }

    /**
     * Get the formatted text.
     */
    public String getFormatted() {
        if (_formatted == null) {
            _formatted = "";
            if (_textColor != null) {
                _formatted += _textColor.getColorCode();
            }
            _formatted += TextUtils.format(_text);
        }

        return _formatted;
    }

    @Override
    public int hashCode() {
        return _hash;
    }

    @Override
    public boolean equals(Object obj) {
        return obj == this;
    }

    @Override
    public String toString() {
        return getFormatted();
    }
}