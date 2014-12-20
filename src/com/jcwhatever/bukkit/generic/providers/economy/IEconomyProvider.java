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

package com.jcwhatever.bukkit.generic.providers.economy;

import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;

/**
 * Interface for an economy provider.
 */
public interface IEconomyProvider {

    /**
     * Specifies how a currency name is used.
     */
    public enum CurrencyNoun {
        SINGULAR,
        PLURAL
    }

    /**
     * Format an amount into a string using the economy settings.
     *
     * @param amount  The amount to format.
     */
    String formatAmount(double amount);

    /**
     * Get the currency name.
     *
     * @param noun  The type of noun to return.
     */
    String getCurrencyName(CurrencyNoun noun);

    /**
     * Get a global economy account.
     *
     * @param playerId  The ID of the account owner.
     */
    IAccount getAccount(UUID playerId);

    /**
     * Determine if the economy has bank support.
     */
    boolean hasBankSupport();

    /**
     * Get a list of banks.
     *
     * @throws java.lang.UnsupportedOperationException if {@code hasBankSupport} returns false.
     */
    List<IBank> getBanks();

    /**
     * Get a bank by name.
     *
     * @param bankName  The name of the bank.
     *
     * @return  Null if the bank was not found.
     *
     * @throws java.lang.UnsupportedOperationException if {@code hasBankSupport} returns false.
     */
    @Nullable
    IBank getBank(String bankName);

    /**
     * Create a new bank account.
     *
     * @param bankName  The name of the bank.
     *
     * @return  Null if the bank was not created.
     *
     * @throws java.lang.UnsupportedOperationException if {@code hasBankSupport} returns false.
     */
    @Nullable
    IBank createBank(String bankName);

    /**
     * Create a new bank account with the specified player as the owner.
     *
     * @param bankName  The name of the bank.
     * @param playerId  The ID of the bank owner.
     *
     * @return  Null if the bank was not created.
     *
     * @throws java.lang.UnsupportedOperationException if {@code hasBankSupport} returns false.
     */
    @Nullable
    IBank createBank(String bankName, UUID playerId);

    /**
     * Delete a bank.
     *
     * @param bankName  The name of the bank.
     *
     * @return  True if the bank was found and deleted.
     *
     * @throws java.lang.UnsupportedOperationException if {@code hasBankSupport} returns false.
     */
    boolean deleteBank(String bankName);
}
