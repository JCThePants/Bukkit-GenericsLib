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


package com.jcwhatever.nucleus.regions.file.basic;

import com.jcwhatever.nucleus.internal.NucMsg;
import com.jcwhatever.nucleus.regions.IRegion;
import com.jcwhatever.nucleus.regions.data.RegionChunkSection;
import com.jcwhatever.nucleus.regions.file.IRegionFileData;
import com.jcwhatever.nucleus.regions.file.IRegionFileLoader.LoadType;
import com.jcwhatever.nucleus.utils.EnumUtils;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.coords.ICoords2Di;
import com.jcwhatever.nucleus.utils.file.BasicByteReader;
import com.jcwhatever.nucleus.utils.file.SerializableBlockEntity;
import com.jcwhatever.nucleus.utils.file.SerializableFurnitureEntity;
import com.jcwhatever.nucleus.utils.observer.future.IFuture;
import com.jcwhatever.nucleus.utils.performance.queued.Iteration3DTask;
import com.jcwhatever.nucleus.utils.performance.queued.QueueProject;
import com.jcwhatever.nucleus.utils.performance.queued.QueueTask;
import com.jcwhatever.nucleus.utils.performance.queued.TaskConcurrency;

import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.annotation.Nullable;

/**
 * Loads a regions chunk block data from a file.
 */
public class RegionChunkFileLoader {

    public static final int COMPATIBLE_FILE_VERSION = 3;
    public static final int RESTORE_FILE_VERSION = 4;

    private Plugin _plugin;
    private IRegion _region;
    private ICoords2Di _coords;
    private boolean _isLoading;

    /**
     * Constructor.
     *
     * @param region  The region to load a chunk file for.
     * @param coords  The coords of the chunk that the file was created from.
     */
    public RegionChunkFileLoader (IRegion region, ICoords2Di coords) {
        _region = region;
        _coords = coords;
        _plugin = region.getPlugin();
    }

    /**
     * Get the chunk.
     */
    public ICoords2Di getChunkCoord() {
        return _coords;
    }

    /**
     * Get the region.
     */
    public IRegion getRegion() {
        return _region;
    }

    /**
     * Determine if the file is in the process of being loaded.
     */
    public boolean isLoading() {
        return _isLoading;
    }

    /**
     * Load data from a specific file.
     *
     * @param file      The file.
     * @param loadType  The type of data to load from the file.
     * @param builder      The data container to load data into.
     */
    public IFuture loadInProject(File file, QueueProject project, LoadType loadType, IRegionFileData builder) {
        PreCon.notNull(file);
        PreCon.notNull(loadType);
        PreCon.notNull(builder);

        if (isLoading())
            return project.cancel("Region cannot load because it is already loading.");

        World world = _region.getWorld();

        if (world == null) {
            return project.cancel("Failed to get world while loading region '{0}'.",
                    _region.getName());
        }

        Chunk chunk = _region.getWorld().getChunkAt(_coords.getX(), _coords.getZ());
        if (chunk == null) {
            return project.cancel("Failed to get chunk ({0}, {1}) in world '{0}' while building region '{1}'.",
                    _coords.getX(), _coords.getZ(), world.getName());
        }

        if (!chunk.isLoaded())
            chunk.load();

        _isLoading = true;

        RegionChunkSection section = new RegionChunkSection(_region, _coords);
        LoadChunkIterator iterator = new LoadChunkIterator(project, file, loadType, builder, 8192,
                section.getStartChunkX(), section.getStartY(), section.getStartChunkZ(),
                section.getEndChunkX(), section.getEndY(), section.getEndChunkZ(), chunk);

        project.addTask(iterator);

        return iterator.getResult();
    }

    /**
     * Load data from a specific file.
     *
     * @param file      The file.
     * @param loadType  The block load type.
     *
     * @return  A future to receive the load results.
     */
    public IFuture load(File file, LoadType loadType, IRegionFileData data) {

        QueueProject project = new QueueProject(_plugin);

        IFuture future = loadInProject(file, project, loadType, data);

        project.run();

        return future;
    }

    /*
     * Iteration worker for loading region chunk data from a file.
     */
    private final class LoadChunkIterator extends Iteration3DTask {

        private BasicByteReader reader;
        private final ChunkSnapshot snapshot;
        private final File file;
        private final LoadType loadType;
        private final IRegionFileData builder;
        private final QueueProject project;

        /**
         * Constructor
         */
        LoadChunkIterator (QueueProject project, File file, LoadType loadType, IRegionFileData builder,
                           long segmentSize, int xStart, int yStart, int zStart,
                           int xEnd, int yEnd, int zEnd, Chunk chunk) {

            super(_plugin, TaskConcurrency.ASYNC, segmentSize, xStart, yStart, zStart, xEnd, yEnd, zEnd);

            this.project = project;
            this.snapshot = chunk.getChunkSnapshot();
            this.file = file;
            this.loadType = loadType;
            this.builder = builder;
        }

        /**
         * Read file header
         */
        @Override
        protected void onIterateBegin() {

            try {

                reader = new BasicByteReader(new FileInputStream(file));

                // Read restore file version
                int restoreFileVersion = reader.getInteger();

                // make sure the file version is correct
                if (restoreFileVersion != RESTORE_FILE_VERSION &&
                        restoreFileVersion != COMPATIBLE_FILE_VERSION) {
                    cancel("Invalid region file. File version is not compatible.");
                    _isLoading = false;
                    return;
                }

                // get name of the region associated with the restore file
                reader.getString();

                // get the name of the world the region was saved from.
                reader.getString();


                if (restoreFileVersion == COMPATIBLE_FILE_VERSION) {
                    // get the coordinates of the region.
                    reader.getLocation();
                    reader.getLocation();
                }
                else {
                    // get the chunk section info
                    reader.deserialize(RegionChunkSection.class);
                }

                // get the volume of the region
                long volume = reader.getLong();

                // Make sure the volume matches expected volume
                if (volume != getVolume()) {
                    cancel("Invalid region file. Volume mismatch.");
                    _isLoading =  false;
                }
            }
            catch (IOException | InstantiationException e) {
                handleException(e, "Failed to read header from file for chunk ({0}, {1}).",
                        snapshot.getX(), snapshot.getZ());
            }
        }

        /**
         * Iterate block items from file
         */
        @Override
        protected void onIterateItem(int x, int y, int z) {

            Material type;
            int data;
            int light;
            int skylight;

            try {

                String typeName = reader.getSmallString();
                if (typeName == null) {
                    handleException(null, "Failed to read from file for chunk ({0}, {1}). " +
                                    "Found a null block type in file.",
                            snapshot.getX(), snapshot.getZ());
                    return;
                }

                type = EnumUtils.getEnum(typeName, Material.class);
                if (type == null) {
                    handleException(null, "Failed to read from file for chunk ({0}, {1}). " +
                                    "Found a block type in file that is not a valid type: {2}",
                            snapshot.getX(), snapshot.getZ(), typeName);
                    return;
                }

                data = reader.getShort();

                int ls = reader.getByte();

                light = ls >> 4;
                skylight = (ls & 0x0F);
            } catch (IOException e) {
                handleException(e, "Failed to read from file for chunk ({0}, {1}).",
                        snapshot.getX(), snapshot.getZ());
                return;
            }

            if (loadType == LoadType.ALL_BLOCKS || !isBlockMatch(x, y, z, type, data)) {
                //_blockInfo.add(new ChunkBlockInfo(x, y, z, type, data, light, skylight));
                this.builder.addBlock(
                        (snapshot.getX() * 16) + x, y, (snapshot.getZ() * 16) + z,
                        type, data, light, skylight);
            }
        }

        /**
         * Read block entities and entities from file on successful completion
         * of loading blocks.
         */
        @Override
        protected void onPreComplete() {
            // Read block entities
            try {
                int totalEntities = reader.getInteger();

                for (int i=0; i < totalEntities; i++) {

                    SerializableBlockEntity state =
                            reader.deserialize(SerializableBlockEntity.class);

                    if (state == null)
                        continue;

                    this.builder.addSerializable(state);
                }
            }
            catch (IOException | IllegalArgumentException | InstantiationException e) {
                handleException(e, "Failed to read Block Entities from file for chunk ({0}, {1}).",
                        snapshot.getX(), snapshot.getZ());
                return;
            }

            // Read entities
            try {
                int totalEntities = reader.getInteger();

                for (int i=0; i < totalEntities; i++) {

                    SerializableFurnitureEntity state =
                            reader.deserialize(SerializableFurnitureEntity.class);

                    if (state == null)
                        continue;

                    this.builder.addSerializable(state);
                }
            } catch (IOException | IllegalArgumentException | InstantiationException e) {
                handleException(e, "Failed to read Entities from file for chunk ({0}, {1}).",
                        snapshot.getX(), snapshot.getZ());
                return;
            }

            QueueTask task = this.builder.commit();
            if (task != null)
                this.project.addTask(task);
        }

        @Override
        protected void onEnd() {
            if (reader != null) {
                try {
                    // close the file
                    reader.close();
                }
                catch (IOException e) {
                    handleException(e, "Failed to close region data file for chunk ({0}, {1}).",
                            snapshot.getX(), snapshot.getZ());
                }
            }

            _isLoading = false;
        }

        /*
         * Determine if the block at the specified coordinates of the chunk snapshot
         * matches the specified material type and data.
         */
        private boolean isBlockMatch(int x, int y, int z, Material type, int data) {
            Material chunkType = Material.getMaterial(snapshot.getBlockTypeId(x, y, z));
            int chunkData = snapshot.getBlockData(x, y, z);

            return chunkType == type && chunkData == data;
        }

        /*
         * Handles displaying error messages and failing the task.
         */
        private void handleException(@Nullable Exception e, String message, Object... args) {
            fail(message, args);
            NucMsg.warning(message, args);
            if (e != null)
                e.printStackTrace();
        }
    }
}
