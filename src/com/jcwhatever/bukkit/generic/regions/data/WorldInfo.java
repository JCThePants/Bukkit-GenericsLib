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

package com.jcwhatever.bukkit.generic.regions.data;

import com.jcwhatever.bukkit.generic.file.GenericsByteReader;
import com.jcwhatever.bukkit.generic.file.GenericsByteWriter;
import com.jcwhatever.bukkit.generic.file.IGenericsSerializable;
import com.jcwhatever.bukkit.generic.storage.IDataNode;
import com.jcwhatever.bukkit.generic.storage.IDataNodeSerializable;
import com.jcwhatever.bukkit.generic.storage.UnableToDeserializeException;
import com.jcwhatever.bukkit.generic.utils.PreCon;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.World.Environment;

import java.io.IOException;
import java.util.UUID;
import javax.annotation.Nullable;

/**
 * Contains information about a World.
 */
public class WorldInfo implements IWorldInfo, IDataNodeSerializable, IGenericsSerializable {

    private UUID _id;
    private String _worldName;
    private Environment _environment;

    /**
     * Empty Constructor required for {@code IDataNodeSerializable}.
     */
    private WorldInfo() {}

    /**
     * Constructor.
     *
     * @param world  The world to get info from.
     */
    public WorldInfo (World world) {
        this(world.getUID(), world.getName(), world.getEnvironment());
    }

    /**
     * Constructor.
     *
     * @param id           The world id.
     * @param name         The world name.
     * @param environment  The world environment.
     */
    public WorldInfo (UUID id, String name, Environment environment) {
        PreCon.notNull(id);
        PreCon.notNullOrEmpty(name);
        PreCon.notNull(environment);

        _id = id;
        _worldName = name;
        _environment = environment;
    }

    /**
     * Get the world unique ID.
     */
    @Override
    public UUID getId() {
        return _id;
    }

    /**
     * Get the world name.
     */
    @Override
    public String getName() {
        return _worldName;
    }

    /**
     * Get the world environment type.
     */
    @Override
    public Environment getEnvironment() {
        return _environment;
    }

    /**
     * Determine if the world is loaded.
     */
    @Override
    public boolean isLoaded() {
        World world = getBukkitWorld();
        if (world == null)
            return false;

        return true;
    }

    /**
     * Get the world instance.
     */
    public World getBukkitWorld() {
        return Bukkit.getWorld(_id);
    }

    /**
     * Determine if the instance represents the specified
     * bukkit world.
     *
     * @param world  The world to check.
     */
    public boolean equalsWorld(@Nullable World world) {
        return world != null && world.getUID().equals(_id);
    }

    @Override
    public int hashCode() {
        return _id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof WorldInfo &&
                ((WorldInfo) obj)._id.equals(_id);
    }

    @Override
    public void serializeToDataNode(IDataNode dataNode) {
        dataNode.set("id", _id);
        dataNode.set("name", _worldName);
        dataNode.set("environment", _environment);
    }

    @Override
    public void deserializeFromDataNode(IDataNode dataNode) throws UnableToDeserializeException {
        _id = dataNode.getUUID("id");
        _worldName = dataNode.getString("name");
        _environment = dataNode.getEnum("environment", Environment.class);

        checkDeserialized(_id);
        checkDeserialized(_worldName);
        checkDeserialized(_environment);
    }

    @Override
    public void serializeToBytes(GenericsByteWriter writer) throws IOException {
        writer.write(_id);
        writer.writeSmallString(_worldName);
        writer.write(_environment);
    }

    @Override
    public void deserializeFromBytes(GenericsByteReader reader) throws IOException, ClassNotFoundException, InstantiationException {
        _id = reader.getUUID();
        _worldName = reader.getSmallString();
        _environment = reader.getEnum(Environment.class);
    }

    private void checkDeserialized(Object object) throws UnableToDeserializeException {
        if (object == null) {
            throw new UnableToDeserializeException();
        }
    }
}
