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
import com.jcwhatever.bukkit.generic.utils.PreCon;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

/**
 * Write bytes to a stream. In order to read the stream
 * properly, {@code GenericsByteReader} needs to be used.
 */
public class GenericsByteWriter extends OutputStream {

    private final OutputStream _stream;
    private final byte[] _buffer = new byte[64];
    private long _bytesWritten = 0;

    private int _booleanCount = 0;
    private byte[] _booleanBuffer = new byte[1];
    private final byte[] _booleanFlags = new byte[] { 1, 2, 4, 8, 16, 32, 64 };


    /**
     * Constructor.
     *
     * @param outputStream  The output stream.
     */
    public GenericsByteWriter(OutputStream outputStream) {
        _stream = outputStream;
    }

    /**
     * Get the number of bytes written.
     */
    public long getBytesWritten() {
        return _bytesWritten;
    }

    /**
     * Write a boolean value.
     *
     * <p>Booleans written sequentially are written as bits into the current byte. When the byte runs out of bits,
     * the next bit is written to the next byte.</p>
     *
     * <p>Bytes that store the boolean values do not share their bits with other data types.</p>
     *
     * @param booleanValue  The boolean value.
     *
     * @throws IOException
     */
    public void write(boolean booleanValue) throws IOException {

        if (_booleanCount == 7) {
            writeBooleans();
        }

        if (booleanValue) {
            _booleanBuffer[0] |= _booleanFlags[_booleanCount];
        }

        _booleanCount++;
    }

    /**
     * Write a byte.
     *
     * @param byteValue  The byte.
     *
     * @throws IOException
     */
    public void write(byte byteValue) throws IOException {

        // write buffered booleans
        writeBooleans();

        _stream.write(byteValue);
        _bytesWritten++;
    }

    /**
     * Write a byte array.
     *
     * <p>Writes an array by first writing an integer (4 bytes) which
     * indicates the length of the array, then writes the entire byte array.</p>
     *
     * <p>If the array is null or empty then the integer 0 (4 bytes) is
     * written and no array bytes are added.</p>
     *
     * @param byteArray  The byte array.
     *
     * @throws IOException
     */
    @Override
    public void write(@Nullable byte[] byteArray) throws IOException {

        // write buffered booleans
        writeBooleans();

        if (byteArray == null || byteArray.length == 0) {
            write(0);
            return;
        }

        write(byteArray.length);
        _stream.write(byteArray, 0, byteArray.length);
        _bytesWritten += byteArray.length;
    }

    /**
     * Write a 32-bit number (4 bytes).
     *
     * @param integerValue  The integer.
     *
     * @throws IOException
     */
    @Override
    public void write(int integerValue) throws IOException {

        // write buffered booleans
        writeBooleans();

        _buffer[0] = (byte)(integerValue >> 24 & 255);
        _buffer[1] = (byte)(integerValue >> 16 & 255);
        _buffer[2] = (byte)(integerValue >> 8 & 255);
        _buffer[3] = (byte)(integerValue & 255);
        _stream.write(_buffer, 0, 4);
        _bytesWritten+=4;
    }

    /**
     * Write a 64 bit number (8 bytes).
     *
     * @param longValue  The long.
     *
     * @throws IOException
     */
    public void write(long longValue) throws IOException {

        // write buffered booleans
        writeBooleans();

        _buffer[0] = (byte)(longValue >> 56 & 255);
        _buffer[1] = (byte)(longValue >> 48 & 255);
        _buffer[2] = (byte)(longValue >> 40 & 255);
        _buffer[3] = (byte)(longValue >> 32 & 255);
        _buffer[4] = (byte)(longValue >> 24 & 255);
        _buffer[5] = (byte)(longValue >> 16 & 255);
        _buffer[6] = (byte)(longValue >> 8 & 255);
        _buffer[7] = (byte)(longValue & 255);
        _stream.write(_buffer, 0, 8);
        _bytesWritten+=8;
    }

    /**
     * Write a floating point number.
     *
     * <p>Float values are written as a UTF-8 byte array preceded with a 4 byte
     * integer to indicate the number of bytes in the array.</p>
     *
     * @param floatValue  The floating point value.
     *
     * @throws IOException
     */
    public void write(float floatValue) throws IOException {
        write(String.valueOf(floatValue), StandardCharsets.UTF_8);
    }

    /**
     * Write a double number.
     *
     * <p>Double values are written using a UTF-8 byte array preceded with a 4 byte
     * integer to indicate the number of bytes in the array.</p>
     *
     * @param doubleValue  The floating point double.
     *
     * @throws IOException
     */
    public void write(double doubleValue) throws IOException {
        write(String.valueOf(doubleValue), StandardCharsets.UTF_8);
    }

    /**
     * Write a text string using UTF-16 encoding.
     *
     * <p>The first 4 bytes (integer) of the string indicate the length
     * of the string in bytes.</p>
     *
     * <p>If the string is null then the integer -1 (4 bytes) is written
     * and no array bytes are written.</p>
     *
     * <p>If the string is empty then the integer 0 (4 bytes) is written
     * and no array bytes are written.</p>
     *
     * @param text  The text to write. Can be null.
     *
     * @throws IOException
     */
    public void write(@Nullable String text) throws IOException {
        write(text, StandardCharsets.UTF_16);
    }

    /**
     * Write a text string.
     *
     * <p>The first 4 bytes (integer) of the string indicate the length
     * of the string in bytes.</p>
     *
     * <p>If the string is null then the integer -1 (4 bytes) is written
     * and no array bytes are written.</p>
     *
     * <p>If the string is empty then the integer 0 (4 bytes) is written
     * and no array bytes are written.</p>
     *
     * @param text     The text to write. Can be null.
     * @param charset  The charset encoding to use.
     *
     * @throws IOException
     */
    public void write(@Nullable String text, Charset charset) throws IOException {

        // write buffered booleans
        writeBooleans();

        // handle null text
        if (text == null) {
            write(-1);
            return;
        }
        // handle empty text
        else if (text.length() == 0) {
            write(0);
            return;
        }

        byte[] bytes = text.getBytes(charset);

        // write string byte length
        write(bytes.length);

        // write string bytes
        _stream.write(bytes);

        // record bytes written
        _bytesWritten+=bytes.length;
    }

    /**
     * Write an enum.
     *
     * <p>Enum values are written using a UTF-8 byte array preceded with a 4 byte
     * integer to indicate the number of bytes in the array. The UTF-8 byte array
     * is the string value of the enum constants name.</p>
     *
     * @param enumConstant  The enum constant.
     *
     * @param <T>  The enum type.
     *
     * @throws IOException
     */
    public <T extends Enum<T>> void write(T enumConstant) throws IOException {
        PreCon.notNull(enumConstant);

        write(enumConstant.name(), StandardCharsets.UTF_8);
    }

    /**
     * Write a {@code Location}.
     *
     * <p>The location is written as follows:</p>
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
     * @param location  The location.
     *
     * @throws IOException
     */
    public void write(Location location) throws IOException {
        PreCon.notNull(location);

        write(location.getWorld().getName(), StandardCharsets.UTF_8);
        write(location.getX());
        write(location.getY());
        write(location.getZ());
        write(location.getYaw());
        write(location.getPitch());
    }

    /**
     * Write an {@code ItemStack}.
     *
     * <p>Writes the item stack as follows:</p>
     * <ul>
     *     <li>Boolean (bit or byte depending on the data structure) indicating
     *         if the item stack is null. 1 = null. (See {@code getBoolean})</li>
     *     <li>Material - Enum (See {@code getEnum})</li>
     *     <li>Durability - Integer (See {@code getInteger})</li>
     *     <li>Meta count - Integer (See {@code getInteger})</li>
     *     <li>Meta collection</li>
     * </ul>
     *
     * <p>Meta is written as follows:</p>
     * <ul>
     *     <li>Meta Name - UTF-8 String (See {@code getString})</li>
     *     <li>Meta Data - UTF-16 String (See {@code getString})</li>
     * </ul>
     *
     * @param itemStack  The item stack.
     *
     * @throws IOException
     */
    public void write(@Nullable ItemStack itemStack) throws IOException {

        write(itemStack == null);
        if (itemStack == null)
            return;

        // write basic data
        write(itemStack.getType());
        write((int)itemStack.getDurability());
        write(itemStack.getAmount());

        List<MetaHandler> handlers = ItemMetaHandlerManager.getHandlers();

        List<ItemMetaObject> metaObjects = new ArrayList<>(10);

        for (MetaHandler handler : handlers) {
            metaObjects.addAll(handler.getMeta(itemStack));
        }

        write(metaObjects.size());

        for (ItemMetaObject metaObject : metaObjects) {
            write(metaObject.getName(), StandardCharsets.UTF_8);
            write(metaObject.getRawData(), StandardCharsets.UTF_16);
        }
    }

    /**
     * Serialize an {@code IGenericsSerializable} object.
     *
     * <p>Data written depends on the how the object writes to the stream internally.</p>
     *
     * @param object  The object to serialize.
     *
     * @param <T>  The object type.
     *
     * @throws IOException
     */
    public <T extends IGenericsSerializable> void write(@Nullable T object) throws IOException {
        write(object == null);
        if (object == null)
            return;

        object.serializeToBytes(this);
    }

    /**
     * Serialize an object.
     *
     * @param object  The object to serialize. Can be null.
     *
     * @throws IOException
     */
    public <T extends Serializable> void write(@Nullable T object) throws IOException {

        // write null flag
        write(object == null);
        if (object == null)
            return;

        // clear boolean buffer
        writeBooleans();

        ObjectOutputStream objectStream = new ObjectOutputStream(this);
        objectStream.writeObject(object);
    }

    /**
     * Close the stream.
     *
     * @throws IOException
     */
    @Override
    public void close() throws IOException {

        flush();

        _stream.close();
    }

    /**
     * Flush buffers to stream.
     *
     * @throws IOException
     */
    @Override
    public void flush() throws IOException {

        // write buffered booleans
        writeBooleans();

        _stream.flush();
    }


    private void writeBooleans() throws IOException {
        if (_booleanCount > 0) {
            _stream.write(_booleanBuffer, 0, 1);
            _booleanBuffer[0] = 0;
            _bytesWritten++;
            _booleanCount = 0;
        }
    }
}
