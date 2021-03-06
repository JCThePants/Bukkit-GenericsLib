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

package com.jcwhatever.nucleus.providers;

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.storage.DataPath;
import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.storage.YamlDataNode;
import com.jcwhatever.nucleus.utils.PreCon;

import java.io.File;

/**
 * Abstract implementation of a NucleusFramework service provider.
 */
public abstract class Provider implements IProvider {

    private IProviderInfo _info;
    private File _dataFolder;
    private IDataNode _dataNode;
    private boolean _isTypesRegistered;
    private boolean _isEnabled;
    private boolean _isDisabled;

    @Override
    public boolean isLoaded() {
        return _isTypesRegistered && _isEnabled && !_isDisabled;
    }

    @Override
    public final IProviderInfo getInfo() {
        if (_info == null)
            throw new IllegalStateException("Provider info not set yet.");

        return _info;
    }

    @Override
    public final File getDataFolder() {

        if (_dataFolder == null) {
            File base = Nucleus.getPlugin().getDataFolder();
            File providers = new File(base, "providers");
            _dataFolder = new File(providers, getInfo().getName());

            if (!_dataFolder.exists() && !_dataFolder.mkdirs()) {
                throw new RuntimeException(
                        "Failed to create data folder for provider '" + getInfo().getName() + "'.");
            }
        }

        return _dataFolder;
    }

    @Override
    public final IDataNode getDataNode() {

        if (_dataNode == null) {
            _dataNode = new YamlDataNode(
                    Nucleus.getPlugin(), new DataPath("providers." + getInfo().getName() + ".config"));
            _dataNode.load();
        }

        return _dataNode;
    }

    @Override
    public DataPath getDataPath(String path) {
        PreCon.notNullOrEmpty(path);

        return new DataPath("providers." + getInfo().getName() + '.' + path);
    }

    @Override
    public final void registerTypes() {
        if (_isTypesRegistered)
            throw new IllegalStateException("Types can only be registered once.");

        _isTypesRegistered = true;

        onRegister();
    }

    @Override
    public final void enable() {
        if (_isEnabled)
            throw new IllegalStateException("Provider can only be enabled once.");

        _isEnabled = true;

        onEnable();
    }

    @Override
    public final void disable() {
        if (!_isEnabled)
            throw new IllegalStateException("Provider cannot be disabled until it is enabled.");

        if (_isDisabled)
            throw new IllegalStateException("Provider can only be disabled once.");

        _isDisabled = true;

        onDisable();
    }

    @Override
    public final void setInfo(IProviderInfo info) {
        PreCon.notNull(info);

        if (_info != null)
            throw new IllegalStateException("Provider info can only be set once.");

        _info = info;
    }

    /**
     * Invoked to register provider supplied types.
     *
     * <p>Intended for optional override.</p>
     */
    protected void onRegister() {}

    /**
     * Invoked when the provider is enabled.
     *
     * <p>Intended for optional override.</p>
     */
    protected void onEnable() {}

    /**
     * Invoked when the provider is disabled.
     *
     * <p>Intended for optional override.</p>
     */
    protected void onDisable() {}
}
