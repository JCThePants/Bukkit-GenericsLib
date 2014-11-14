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


package com.jcwhatever.bukkit.generic.regions;

import com.jcwhatever.bukkit.generic.extended.serializable.SerializableBlockEntity;
import com.jcwhatever.bukkit.generic.extended.serializable.SerializableFurnitureEntity;
import com.jcwhatever.bukkit.generic.file.GenericsByteReader;
import com.jcwhatever.bukkit.generic.performance.queued.Iteration3DTask;
import com.jcwhatever.bukkit.generic.performance.queued.QueueProject;
import com.jcwhatever.bukkit.generic.performance.queued.QueueResult.CancelHandler;
import com.jcwhatever.bukkit.generic.performance.queued.QueueResult.FailHandler;
import com.jcwhatever.bukkit.generic.performance.queued.QueueResult.Future;
import com.jcwhatever.bukkit.generic.performance.queued.TaskConcurrency;
import com.jcwhatever.bukkit.generic.utils.PreCon;

import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Material;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedList;
import javax.annotation.Nullable;

/**
 * Loads a regions chunk block data from a file.
 */
public final class RegionChunkFileLoader {

    public static final int RESTORE_FILE_VERSION = 1;
    private Plugin _plugin;
    private RestorableRegion _region;
    private Chunk _chunk;
    private boolean _isLoading;
    private final LinkedList<BlockInfo> _blockInfo = new LinkedList<>();
    private final LinkedList<SerializableBlockEntity> _blockEntities = new LinkedList<>();
    private final LinkedList<SerializableFurnitureEntity> _entities = new LinkedList<>();

    /**
     * Specifies what blocks are loaded from the file.
     */
    public enum LoadType {
        /**
         * Loads all blocks info from the file
         */
        ALL_BLOCKS,
        /**
         * Loads blocks that do no match the current chunk.
         */
        MISMATCHED
    }

    /**
     * Constructor.
     *
     * @param region  The region to load a chunk file for.
     * @param chunk   The region chunk that the file was created from.
     */
    public RegionChunkFileLoader (RestorableRegion region, Chunk chunk) {
        _region = region;
        _chunk = chunk;
        _plugin = region.getPlugin();
    }

    /**
     * Get the chunk.
     */
    public Chunk getChunk() {
        return _chunk;
    }

    /**
     * Get the region.
     */
    public RestorableRegion getRegion() {
        return _region;
    }

    /**
     * Determine if the file is in the process
     * of being loaded.
     */
    public boolean isLoading() {
        return _isLoading;
    }

    /**
     * Get block info loaded from the file.
     *
     * <p>Do not access while loading from file.</p>
     */
    public LinkedList<BlockInfo> getBlockInfo() {
        if (_isLoading)
            throw new IllegalAccessError("Cannot access block info while data is being loaded from file.");

        return _blockInfo;
    }

    /**
     * Get block entity data loaded from the file.
     *
     * <p>Do not access while loading from file.</p>
     */
    public LinkedList<SerializableBlockEntity> getBlockEntityInfo() {
        if (_isLoading)
            throw new IllegalAccessError("Cannot access block entity info while data is being loaded from file.");

        return _blockEntities;
    }

    /**
     * Get entity data loaded from the file.
     *
     * <p>Do not access while loading from file.</p>
     */
    public LinkedList<SerializableFurnitureEntity> getFurnitureEntityInfo() {
        if (_isLoading)
            throw new IllegalAccessError("Cannot access furniture entity info while data is being loaded from file.");

        return _entities;
    }

    /**
     * Load data from a specific file.
     *
     * @param file      The file.
     * @param project   The project to add the loading task to.
     * @param loadType  The callback to run when load is finished.
     */
    public Future loadInProject(File file, QueueProject project, LoadType loadType) {
        PreCon.notNull(file);
        PreCon.notNull(project);

        if (isLoading())
            return project.cancel("Region cannot load because it is already loading.");

        _isLoading = true;

        RegionChunkSection section = new RegionChunkSection(_region, _chunk);
        LoadChunkIterator iterator = new LoadChunkIterator(file, loadType, 8192, section.getStartChunkX(), section.getStartY(),
                section.getStartChunkZ(), section.getEndChunkX(), section.getEndY(), section.getEndChunkZ());

        project.addTask(iterator);

        return iterator.getResult();
    }

    /**
     * Load data from a specific file.
     *
     * @param file      The file.
     * @param loadType  The block load type.
     * @param callback  The callback to run when load is finished.
     */
    public void load(File file, LoadType loadType, final LoadFileCallback callback) {

        QueueProject project = new QueueProject(_plugin);

        loadInProject(file, project, loadType)
                .onCancel(new CancelHandler() {
                    @Override
                    public void run(@Nullable String reason) {
                        callback.onFinish(false);
                    }
                })
                .onFail(new FailHandler() {

                    @Override
                    public void run(@Nullable String reason) {
                        callback.onFinish(false);
                    }
                })
                .onComplete(new Runnable() {
                    @Override
                    public void run() {
                        callback.onFinish(true);
                    }
                });
    }

    /**
     * Load callback interface.
     */
    public interface LoadFileCallback {
        void onFinish(boolean isLoadSuccess);
    }

    /**
     * Data object to hold information about a single block
     */
    public static final class BlockInfo implements Comparable<BlockInfo> {
        public final int data, x, y, z;
        public final Material material;

        public BlockInfo (Material material, int data, int x, int y, int z) {
            this.material = material;
            this.data = data;
            this.x = x;
            this.y = y;
            this.z = z;
        }

        @Override
        public int compareTo(BlockInfo o) {
            //noinspection SuspiciousNameCombination
            return Integer.compare(this.y, o.y);
        }
    }

    /**
     * Iteration worker for loading region chunk data from a file.
     */
    private final class LoadChunkIterator extends Iteration3DTask {

        private GenericsByteReader reader;
        private final ChunkSnapshot snapshot;
        private final File file;
        private final LoadType loadType;

        /**
         * Constructor
         */
        LoadChunkIterator (File file, LoadType loadType, long segmentSize, int xStart, int yStart, int zStart,
                           int xEnd, int yEnd, int zEnd) {

            super(_plugin, TaskConcurrency.ASYNC, segmentSize, xStart, yStart, zStart, xEnd, yEnd, zEnd);

            this.snapshot = _chunk.getChunkSnapshot();
            this.file = file;
            this.loadType = loadType;
        }

        /**
         * Read file header
         */
        @Override
        protected void onIterateBegin() {
            _blockInfo.clear();
            _blockEntities.clear();

            try {

                reader = new GenericsByteReader(new FileInputStream(file));

                // Read restore file version
                int restoreFileVersion = reader.getInteger();

                // make sure the file version is correct
                if (restoreFileVersion != RESTORE_FILE_VERSION) {
                    cancel("Invalid region file. File is an old version and is no longer valid.");
                    _isLoading = false;
                    return;
                }

                // get name of arena associated with the restore file
                reader.getString();

                // get the name of the world the restore file is in
                reader.getString();

                // get the coordinates of the region the file chunk is part of
                reader.getLocation();
                reader.getLocation();

                // get the volume of the file data
                long volume = reader.getLong();

                // Make sure the volume matches expected volume
                if (volume != getVolume()) {
                    cancel("Invalid region file. Volume mismatch.");
                    _isLoading =  false;
                }
            }
            catch (IOException e) {
                e.printStackTrace();
                fail("Failed to read file header.");
            }
        }

        /**
         * Iterate block items from file
         */
        @Override
        protected void onIterateItem(int x, int y, int z) {

            Material chunkType = Material.getMaterial(snapshot.getBlockTypeId(x, y, z));
            int chunkData = snapshot.getBlockData(x, y, z);

            Material type;
            int data;

            try {
                type = reader.getEnum(Material.class);
                data = reader.getInteger();
            }
            catch (IOException io) {
                io.printStackTrace();
                fail("Failed to read from file.");
                return;
            }

            if (loadType == LoadType.ALL_BLOCKS || chunkType != type || chunkData != data) {
                _blockInfo.add(new BlockInfo(type, data, x, y, z));
            }
        }

        /**
         * Read block entities and entities from file on
         * successful completion of loading blocks
         */
        @Override
        protected void onPreComplete() {
            // Read block entities
            try {
                int totalEntities = reader.getInteger();

                for (int i=0; i < totalEntities; i++) {

                    SerializableBlockEntity state = reader.getGenerics(SerializableBlockEntity.class);
                    if (state == null)
                        continue;

                    _blockEntities.push(state);
                }
            }
            catch (IOException | IllegalArgumentException | InstantiationException |
                    IllegalAccessException | ClassNotFoundException e) {
                e.printStackTrace();
                fail("Failed to read Block Entities from file.");
            }

            // Read entities
            try {
                int totalEntities = reader.getInteger();

                for (int i=0; i < totalEntities; i++) {

                    SerializableFurnitureEntity state = reader.getGenerics(SerializableFurnitureEntity.class);
                    if (state == null)
                        continue;

                    _entities.push(state);
                }
            }
            catch (IOException | IllegalArgumentException | InstantiationException |
                    IllegalAccessException | ClassNotFoundException e) {
                e.printStackTrace();
                fail("Failed to read Entities from file.");
            }
        }

        @Override
        protected void onEnd() {
            cleanup();
        }

        private void cleanup() {

            if (reader != null) {
                try {
                    // close the file
                    reader.close();
                }
                catch (IOException io) {
                    cancel("Failed to close region data file.");
                    io.printStackTrace();
                }
            }

            _isLoading = false;
        }

    }
}
