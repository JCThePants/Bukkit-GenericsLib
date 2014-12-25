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


package com.jcwhatever.bukkit.generic.converters;

import com.jcwhatever.bukkit.generic.utils.text.TextColor;

import org.bukkit.ChatColor;

import javax.annotation.Nullable;

/**
 * Convert between chat color codes that use the '&' character and valid chat color codes.
 */
public class AlternativeChatColorConverter extends ValueConverter<String, String> {

    AlternativeChatColorConverter() {}

    /**
     * Convert chat color codes in a string that use the '&' character into valid chat color codes.
     *
     * @param value  The string to convert
     *
     * @return Null if a string is not provided.
     */
    @Override
    @Nullable
    protected String onConvert(Object value) {
        if (!(value instanceof String))
            return null;

        return ChatColor.translateAlternateColorCodes('&', (String)value);
    }

    /**
     * Convert valid chat color codes in a string into '&' codes;
     *
     * @param value  The string to convert
     *
     * @return  Null if a string is not provided.
     */
    @Override
    @Nullable
    protected String onUnconvert(Object value) {
        if (!(value instanceof String))
            return null;

        return ((String)value).replaceAll(String.valueOf(TextColor.FORMAT_CHAR), "&");
    }

}
