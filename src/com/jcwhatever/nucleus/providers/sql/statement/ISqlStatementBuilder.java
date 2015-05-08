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

package com.jcwhatever.nucleus.providers.sql.statement;

/**
 * Sql statement builder.
 */
public interface ISqlStatementBuilder<S, U, I, D> {

    /**
     * Insert the beginning of a transaction.
     */
    ISqlStatementBuilder<S, U, I, D> beginTransaction();

    /**
     * Construct a Select query.
     *
     * <p>Equivalent to invoking {@link #selectRows}.</p>
     *
     * @param columns  The columns to select. Leave empty to select all.
     */
    S selectRow(String... columns);

    /**
     * Construct a Select query.
     *
     * <p>Equivalent to invoking {@link #selectRow}.</p>
     *
     * @param columns  The columns to select. Leave empty to select all.
     */
    S selectRows(String... columns);

    /**
     * Construct an Update statement.
     *
     * <p>Equivalent to invoking {@link #updateRows}.</p>
     */
    U updateRow();

    /**
     * Construct an Update statement.
     *
     * <p>Equivalent to invoking {@link #updateRow}.</p>
     */
    U updateRows();

    /**
     * Construct an Insert statement.
     *
     * <p>Equivalent to invoking {@link #insertRows}.</p>
     *
     * @param columns  The columns with values to insert. Leave empty to auto
     *                 insert all.
     */
    I insertRow(String... columns);

    /**
     * Construct an Insert statement.
     *
     * <p>Equivalent to invoking {@link #insertRow}.</p>
     *
     * @param columns  The columns with values to insert. Leave empty to auto
     *                 insert all.
     */
    I insertRows(String... columns);

    /**
     * Construct a Delete statement.
     *
     * <p>Equivalent to invoking {@link #deleteRows}.</p>
     */
    D deleteRow();

    /**
     * Construct a Delete statement.
     *
     * <p>Equivalent to invoking {@link #deleteRow}.</p>
     */
    D deleteRows();
}
