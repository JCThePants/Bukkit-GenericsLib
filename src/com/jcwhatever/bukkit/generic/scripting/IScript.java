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


package com.jcwhatever.bukkit.generic.scripting;

import com.jcwhatever.bukkit.generic.mixins.INamed;
import com.jcwhatever.bukkit.generic.scripting.api.IScriptApi;

import java.io.File;
import java.util.Collection;
import javax.annotation.Nullable;

/**
 * A data object that holds information and source for a script.
 */
public interface IScript extends INamed {

    /**
     * Get the name of the script.
     */
    @Override
    String getName();

    /**
     * Get the file the script is from.
     *
     * @return Null if script is not from a file.
     */
    @Nullable
    File getFile();

    /**
     * Get the script source.
     */
    String getScript();

    /**
     * Get the script type.
     */
    String getType();

    /**
     * Evaluate the script.
     *
     * @param apiCollection  The api to include.
     */
    @Nullable
    IEvaluatedScript evaluate(@Nullable Collection<? extends IScriptApi> apiCollection);
}
