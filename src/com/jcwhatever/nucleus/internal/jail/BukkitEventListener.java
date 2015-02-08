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

package com.jcwhatever.nucleus.internal.jail;

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.jail.IJailManager;
import com.jcwhatever.nucleus.jail.JailSession;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.UUID;

/**
 * Jail event listener
 */
public final class BukkitEventListener  implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    private void onCommandPreprocess(PlayerCommandPreprocessEvent event) {

        // prevent prisoners from using commands
        if (Nucleus.getJailManager().isPrisoner(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    private void onPlayerInteract(PlayerInteractEvent event) {

        // prevent prisoners from interacting
        if (Nucleus.getJailManager().isPrisoner(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event) {

        // make sure player that is logging in is released if no longer a prisoner
        if (Nucleus.getJailManager().isLateRelease(event.getPlayer().getUniqueId())) {
            Nucleus.getJailManager().release(event.getPlayer().getUniqueId());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onPlayerRespawn(PlayerRespawnEvent event) {

        IJailManager manager = Nucleus.getJailManager();
        UUID playerId = event.getPlayer().getUniqueId();

        // release prisoner
        if (manager.isLateRelease(playerId)) {
            manager.release(playerId);
        }
        else if (manager.isPrisoner(playerId)) {

            // send prisoner back to jail
            JailSession session = manager.getSession(playerId);
            if (session != null) {
                Location location = session.getJail().getRandomTeleport();
                if (location != null) {
                    event.setRespawnLocation(location);
                }
            }
        }
    }
}
