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


package com.jcwhatever.bukkit.generic.file;

import com.jcwhatever.bukkit.generic.items.serializer.metahandlers.ItemMetaObject;
import com.jcwhatever.bukkit.generic.items.serializer.metahandlers.MetaHandler;
import com.jcwhatever.bukkit.generic.items.serializer.metahandlers.ItemMetaHandlerManager;
import com.jcwhatever.bukkit.generic.utils.EnumUtils;
import com.jcwhatever.bukkit.generic.utils.PreCon;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import javax.annotation.Nullable;

/**
 * Read bytes from a stream. Bytes from the stream need to have been
 * generated by {@code GenericsByteWriter} in order to be read.
 */
public class GenericsByteReader extends InputStream {

    private final InputStream _stream;
    private final byte[] _buffer = new byte[1024];
    private long _bytesRead = 0;

    private int _booleanReadCount = 7; // resets to 7
    private byte[] _booleanBuffer = new byte[1];
    private final byte[] _booleanFlags = new byte[] { 1, 2, 4, 8, 16, 32, 64 };

    /**
     * Constructor.
     *
     * @param stream  The input stream.
     */
    public GenericsByteReader(InputStream stream) {
        PreCon.notNull(stream);

        _stream = stream;
    }

    /**
     * Get the number of bytes read so far.
     */
    public long getBytesRead() {
        return _bytesRead;
    }

    @Override
    public int read() throws IOException {

        resetBooleanBuffer();

        _bytesRead++;
        return _stream.read();
    }

    /**
     * Skip over a number of bytes without returning them.
     *
     * @param byteDistance  The number of bytes to skip.
     *
     * @return  The number of bytes skipped.
     *
     * @throws IOException
     */
    @Override
    public long skip(long byteDistance) throws IOException {
        return _stream.skip(byteDistance);
    }

    /**
     * Get a boolean.
     *
     * <p>Booleans read sequentially are read as bits from the current byte. When the byte runs out of bits,
     * the next bit is read from the next byte.</p>
     *
     * <p>Bytes that store boolean values do not share bits with other data types.</p>
     *
     * @throws IOException
     */
    public boolean getBoolean() throws IOException {

        if (_booleanReadCount == 7) {
            _bytesRead += (byte)_stream.read(_booleanBuffer, 0, 1);
            _booleanReadCount = 0;
        }

        boolean result = (_booleanBuffer[0] & _booleanFlags[_booleanReadCount]) == _booleanFlags[_booleanReadCount];

        _booleanReadCount++;

        return result;
    }

    /**
     * Get the next byte.
     *
     * @throws IOException
     */
    public byte getByte() throws IOException {

        resetBooleanBuffer();

        _bytesRead += _stream.read(_buffer, 0, 1);
        return _buffer[0];
    }

    /**
     * Get the next byte array.
     *
     * <p>Gets an array by first reading an integer (4 bytes) which
     * indicates the length of the array, then reads the number of bytes indicated.</p>
     *
     * <p>If the number of bytes indicated is 0, then an empty byte array is returned.</p>
     *
     * @throws IOException
     */
    public byte[] getBytes() throws IOException {

        int size = getInteger();

        if (size == 0)
            return new byte[0];

        byte[] bytes = new byte[size];

        _bytesRead += _stream.read(bytes);

        return bytes;
    }

    /**
     * Read the next 4 bytes and return them as an integer.
     *
     * @throws IOException
     */
    public int getInteger() throws IOException {

        resetBooleanBuffer();

        _bytesRead += (long)_stream.read(_buffer, 0, 4);
        return ((_buffer[0] & 255) << 24)
                + ((_buffer[1] & 255) << 16)
                + ((_buffer[2] & 255) << 8)
                + (_buffer[3] & 255);
    }

    /**
     * Read the next 8 bytes an return them as a long value.
     *
     * @throws IOException
     */
    public long getLong() throws IOException {

        resetBooleanBuffer();

        _bytesRead+=(long)_stream.read(_buffer, 0, 8);
        return ((_buffer[0] & 255L) << 56)
                + ((_buffer[1] & 255L) << 48)
                + ((_buffer[2] & 255L) << 40)
                + ((_buffer[3] & 255L) << 32)
                + ((_buffer[4] & 255L) << 24)
                + ((_buffer[5] & 255L) << 16)
                + ((_buffer[6] & 255L) << 8)
                + (_buffer[7] & 255L);
    }

    /**
     * Get the next group of bytes as a UTF-16 string.
     *
     * <p>The first 4 bytes (integer) of the string indicate the length of the string in bytes.</p>
     *
     * <p>If the original string written was null, then null is returned.</p>
     *
     * @throws IOException
     */
    @Nullable
    public String getString() throws IOException {
        return getString(StandardCharsets.UTF_16);
    }

    /**
     * Get the next group of bytes as a string.
     *
     * <p>The first 4 bytes (integer) of the string indicate the length of the string in bytes.</p>
     *
     * <p>If the original string written was null, then null is returned.</p>
     *
     * @param charset  The character set encoding to use.
     *
     * @throws IOException
     */
    @Nullable
    public String getString(Charset charset) throws IOException {

        resetBooleanBuffer();

        int len = getInteger();
        if (len == -1)
            return null;

        if (len == 0)
            return "";

        if (len >= _buffer.length) {
            byte[] buffer = new byte[len];
            _bytesRead += (long) _stream.read(buffer, 0, len);
            return new String(buffer, 0, len, charset);
        }
        else {
            _bytesRead += (long) _stream.read(_buffer, 0, len);
            return new String(_buffer, 0, len, charset);
        }
    }

    /**
     * Get the next group of bytes as a float value.
     *
     * <p>Float values are read as a UTF-8 byte array preceded with a 4 byte
     * integer to indicate the number of bytes in the array.</p>
     *
     * @throws IOException
     *
     * @throws RuntimeException If the string value from the stream is null or empty.
     * @throws NumberFormatException If the value from the stream cannot be parsed to a float.
     */
    public float getFloat() throws IOException {
        String str = getString(StandardCharsets.UTF_8);
        if (str == null || str.isEmpty())
            throw new RuntimeException("Failed to read float value.");

        return Float.parseFloat(str);
    }

    /**
     * Get the next group of bytes as a double value.
     *
     * <p>Double values are read as a UTF-8 byte array preceded with a 4 byte
     * integer to indicate the number of bytes in the array.</p>
     *
     * @throws IOException
     *
     * @throws RuntimeException If the double value from the stream is null or empty.
     * @throws NumberFormatException If the value from the stream cannot be parsed to a double.
     */
    public double getDouble() throws IOException {
        String str = getString(StandardCharsets.UTF_8);
        if (str == null || str.isEmpty())
            throw new RuntimeException("Failed to read double value.");

        return Double.parseDouble(str);
    }

    /**
     * Get the next group of bytes as an enum.
     *
     * <p>Enum values are stored using a UTF-8 byte array preceded with a 4 byte
     * integer to indicate the number of bytes in the array. The UTF-8 byte array
     * is the string value of the enum constants name.</p>
     *
     * @param enumClass  The enum class.
     *
     * @param <T>  The enum type.
     *
     * @throws IOException
     *
     * @throws RuntimeException If the enum value from the stream is null.
     * @throws IllegalStateException If the enum value is not valid for the specified enum type.
     */
    public <T extends Enum<T>> T getEnum(Class<T> enumClass) throws IOException {
        String constantName = getString(StandardCharsets.UTF_8);
        if (constantName == null) {
            throw new RuntimeException(
                    "Could not find an enum: " + enumClass.getName());
        }

        T e = EnumUtils.getEnum(constantName, enumClass);
        if (e == null) {
            throw new IllegalStateException(
                    "The enum name retrieved is not a valid constant name for enum type: " + enumClass.getName());
        }

        return e;
    }

    /**
     * Get the next group of bytes as a location.
     *
     * <p>The location is read as follows:</p>
     *
     * <ul>
     *     <li>The world name - UTF-8 String (See {@code getString})</li>
     *     <li>The X value - Double (See {@code getDouble})</li>
     *     <li>The Y value - Double (See {@code getDouble})</li>
     *     <li>The Z value - Double (See {@code getDouble})</li>
     *     <li>The Yaw value - Double (See {@code getDouble})</li>
     *     <li>The Pitch value - Double (See {@code getDouble})</li>
     * </ul>
     *
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public Location getLocation() throws IOException {

        String worldName = getString(StandardCharsets.UTF_8);
        double x = getDouble();
        double y = getDouble();
        double z = getDouble();
        float yaw = getFloat();
        float pitch = getFloat();

        return new Location(Bukkit.getWorld(worldName), x, y, z, yaw, pitch);
    }

    /**
     * Get the next group of bytes as an item stack.
     *
     * <p>Reads the item stack as follows:</p>
     * <ul>
     *     <li>Boolean (bit or byte depending on the data structure) indicating
     *         if the item stack is null. 1 = null. (See {@code getBoolean})</li>
     *     <li>Material - Enum (See {@code getEnum})</li>
     *     <li>Durability - Integer (See {@code getInteger})</li>
     *     <li>Meta count - Integer (See {@code getInteger})</li>
     *     <li>Meta collection</li>
     * </ul>
     *
     * <p>Meta is read as follows:</p>
     * <ul>
     *     <li>Meta Name - UTF-8 String (See {@code getString})</li>
     *     <li>Meta Data - UTF-16 String (See {@code getString})</li>
     * </ul>
     *
     * @throws IOException
     *
     * @throws RuntimeException If fail to read meta
     */
    @Nullable
    public ItemStack getItemStack() throws IOException {

        boolean isNull = getBoolean();
        if (isNull)
            return null;

        // read basic data
        Material type = getEnum(Material.class);
        short durability = (short)getInteger();
        int amount = getInteger();

        ItemStack result = new ItemStack(type, amount, durability);

        int totalMeta = getInteger();

        for (int i=0; i < totalMeta; i++) {

            String metaName = getString(StandardCharsets.UTF_8);
            if (metaName == null)
                throw new RuntimeException("Failed to read meta name of entry #" + i);

            String metaData = getString(StandardCharsets.UTF_16);
            if (metaData == null)
                throw new RuntimeException("Failed to read meta data of entry #" + i);

            MetaHandler handler = ItemMetaHandlerManager.getHandler(metaName);
            if (handler == null)
                continue;

            ItemMetaObject meta = new ItemMetaObject(metaName, metaData);

            handler.apply(result, meta);
        }

        return result;
    }

    /**
     * Get an {@code IGenericsSerializable} object.
     *
     * <p>Data read depends on the how the object reads the stream internally.</p>
     *
     * @param objectClass  The object class.
     *
     * @param <T>  The object type.
     *
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws IOException
     * @throws ClassNotFoundException
     */
    @Nullable
    public <T extends IGenericsSerializable> T getGenerics(Class<T> objectClass)
            throws IllegalAccessException, InstantiationException, IOException, ClassNotFoundException {
        PreCon.notNull(objectClass);

        boolean isNull = getBoolean();
        if (isNull)
            return null;

        T object = objectClass.newInstance();

        object.deserializeFromBytes(this);

        return object;
    }

    /**
     * Deserialize an object from the next set of bytes.
     *
     * @param objectClass  The object class.
     *
     * @param <T>  The object type.
     *
     * @return The deserialized object or null if the object was written as null.
     *
     * @throws IOException
     * @throws ClassNotFoundException
     */
    @Nullable
    public <T extends Serializable> T getObject(Class<T> objectClass) throws IOException, ClassNotFoundException {
        PreCon.notNull(objectClass);

        boolean isNull = getBoolean();

        if (isNull)
            return null;

        resetBooleanBuffer();

        ObjectInputStream objectStream = new ObjectInputStream(this);

        Object object = objectStream.readObject();

        if (!object.getClass().isAssignableFrom(objectClass))
            throw new ClassNotFoundException("The object returned by the stream is not of the specified class: " + objectClass.getName());

        return objectClass.cast(object);
    }

    /**
     * Close the stream.
     *
     * @throws IOException
     */
    @Override
    public void close() throws IOException {
        _stream.close();
        super.close();
    }

    private void resetBooleanBuffer() {
        _booleanReadCount = 7;
        _booleanBuffer[0] = 0;
    }
}
