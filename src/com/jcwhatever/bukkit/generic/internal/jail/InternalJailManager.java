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

package com.jcwhatever.bukkit.generic.internal.jail;

import com.jcwhatever.bukkit.generic.GenericsLib;
import com.jcwhatever.bukkit.generic.collections.HashMapMap;
import com.jcwhatever.bukkit.generic.internal.Lang;
import com.jcwhatever.bukkit.generic.internal.Msg;
import com.jcwhatever.bukkit.generic.jail.IJailManager;
import com.jcwhatever.bukkit.generic.jail.Jail;
import com.jcwhatever.bukkit.generic.jail.JailSession;
import com.jcwhatever.bukkit.generic.language.Localizable;
import com.jcwhatever.bukkit.generic.storage.IDataNode;
import com.jcwhatever.bukkit.generic.utils.DateUtils;
import com.jcwhatever.bukkit.generic.utils.DateUtils.TimeRound;
import com.jcwhatever.bukkit.generic.utils.DependencyRunner;
import com.jcwhatever.bukkit.generic.utils.PlayerUtils;
import com.jcwhatever.bukkit.generic.utils.PreCon;
import com.jcwhatever.bukkit.generic.utils.Scheduler;
import com.jcwhatever.bukkit.generic.utils.Utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;

/*
 * 
 */
public class InternalJailManager implements IJailManager {

    @Localizable static final String _RELEASE_TIME = "Release in {0} minutes.";

    private final IDataNode _dataNode;
    private final Jail _defaultJail;

    private Map<UUID, JailSession> _sessionMap = new HashMap<UUID, JailSession>(20);
    private HashMapMap<Plugin, String, Jail> _jails = new HashMapMap<>(5);
    private Map<UUID, Location> _lateReleases = new HashMap<UUID, Location>(20);
    private Warden _warden = new Warden();

    public InternalJailManager(IDataNode dataNode) {
        PreCon.notNull(dataNode);

        _dataNode = dataNode;
        _defaultJail = new Jail(GenericsLib.getPlugin(), "default", _dataNode.getNode("default"));

        loadSettings();

        // check for prisoner release every 1 minute.
        Scheduler.runTaskRepeat(GenericsLib.getPlugin(), 20, 1200, _warden);

        BukkitEventListener _eventListener = new BukkitEventListener();
        Bukkit.getPluginManager().registerEvents(_eventListener, GenericsLib.getPlugin());
    }

    @Override
    public void registerJail(Jail jail) {
        PreCon.notNull(jail);

        _jails.put(jail.getPlugin(), jail.getName().toLowerCase(), jail);
    }

    @Override
    public void unregisterJail(Jail jail) {
        PreCon.notNull(jail);

        _jails.remove(jail.getPlugin(), jail.getName());
    }

    @Override
    @Nullable
    public Jail getJail(Plugin plugin, String name) {
        PreCon.notNull(plugin);
        PreCon.notNullOrEmpty(name);

        return _jails.get(plugin, name.toLowerCase());
    }

    @Override
    public List<Jail> getJails() {
        return new ArrayList<>(_jails.valueSet());
    }

    @Override
    public JailSession registerJailSession(Jail jail, UUID playerId, int minutes) {
        PreCon.notNull(jail);
        PreCon.notNull(playerId);
        PreCon.positiveNumber(minutes);

        // get release time
        Date expires = DateUtils.addMinutes(new Date(), minutes);

        return registerJailSession(jail, playerId, expires);
    }

    @Override
    public JailSession registerJailSession(Jail jail, UUID playerId, Date expires) {
        PreCon.notNull(jail);
        PreCon.notNull(playerId);
        PreCon.notNull(expires);

        // create session
        JailSession jailSession = new JailSession(jail, playerId, expires);

        IDataNode node = _dataNode.getNode("sessions." + playerId.toString());
        node.set("plugin", jail.getPlugin().getName());
        node.set("jail", jail.getName());
        node.set("expires", expires.getTime());
        node.saveAsync(null);

        _sessionMap.put(playerId, jailSession);

        return jailSession;
    }

    @Override
    public void unregisterJailSession(UUID playerId) {
        PreCon.notNull(playerId);

        _sessionMap.remove(playerId);
    }

    @Override
    @Nullable
    public JailSession getSession(UUID playerId) {
        return _sessionMap.get(playerId);
    }

    @Override
    public List<JailSession> getSessions() {
        return new ArrayList<>(_sessionMap.values());
    }

    @Override
    public boolean isPrisoner(UUID playerId) {
        return _sessionMap.containsKey(playerId);
    }

    @Override
    public boolean release(UUID playerId) {

        JailSession session = getSession(playerId);
        if (session != null) {
            _warden.run();
            return true;
        }

        return false;
    }

    @Override
    public boolean isLateRelease(UUID playerId) {
        return _lateReleases.containsKey(playerId);
    }

    // register a late release so the next time a player re-spawns or logs
    // in they will be teleported to the release location.
    private void registerLateRelease(UUID playerId, Location releaseLocation) {
        PreCon.notNull(playerId);
        PreCon.notNull(releaseLocation);

        _lateReleases.put(playerId, releaseLocation);

        IDataNode node = _dataNode.getNode("late-release");
        node.set(playerId.toString(), releaseLocation);
        node.saveAsync(null);
    }

    private void loadSettings() {

        // load late releases
        IDataNode lateNode = _dataNode.getNode("late-release");
        Set<String> rawIds = lateNode.getSubNodeNames();

        for (String rawId : rawIds) {
            UUID playerId = Utils.getId(rawId);
            if (playerId == null)
                continue;

            Location release = lateNode.getLocation(rawId);
            if (release == null)
                continue;

            _lateReleases.put(playerId, release);
        }

        // Load jail sessions
        DependencyRunner<JailDependency> _sessionLoader =
                new DependencyRunner<JailDependency>(GenericsLib.getPlugin());

        IDataNode sessions = _dataNode.getNode("sessions");
        Set<String> rawSessionIds = sessions.getSubNodeNames();

        for (String rawId : rawSessionIds) {

            IDataNode node = sessions.getNode(rawId);

            UUID playerId = Utils.getId(rawId);
            if (playerId == null)
                continue;

            String pluginName = node.getString("plugin");
            String jailName = node.getString("jail");
            long expireTime = node.getLong("expires", 0);

            if (pluginName == null || jailName == null) {
                node.remove();
                continue;
            }

            JailDependency dependency = new JailDependency(
                    pluginName, jailName, playerId, new Date(expireTime));

            _sessionLoader.add(dependency);
        }

        _sessionLoader.start();
    }

    /**
     * Scheduled task responsible for determining when
     * to release players from prison.
     */
    class Warden implements Runnable {

        @Override
        public void run () {
            run(false);
        }

        public void run(boolean silent) {
            if (_sessionMap.isEmpty())
                return;

            List<JailSession> jailSessions = new ArrayList<JailSession>(_sessionMap.values());

            Date now = new Date();

            for (JailSession session : jailSessions) {
                if (session.isExpired() || _lateReleases.containsKey(session.getPlayerId())) {

                    _sessionMap.remove(session.getPlayerId());
                    Location releaseLoc = _lateReleases.remove(session.getPlayerId());

                    if (!session.isReleased()) {
                        session.release();
                    }

                    if (releaseLoc == null) {
                        releaseLoc = session.getReleaseLocation();
                        if (releaseLoc == null)
                            throw new AssertionError();
                    }

                    Player p = PlayerUtils.getPlayer(session.getPlayerId());
                    if (p != null) {
                        p.teleport(releaseLoc);
                    }
                    else {
                        // register player to be released at next login
                        registerLateRelease(session.getPlayerId(), releaseLoc);
                    }
                }
                else if (!silent) {

                    long releaseMinutes = DateUtils.getDeltaMinutes(now, session.getExpiration(), TimeRound.ROUND_UP);

                    if (releaseMinutes <= 5 || releaseMinutes % 10 == 0) {
                        Player p = PlayerUtils.getPlayer(session.getPlayerId());

                        if (p != null) {
                            Msg.tellAnon(p, Lang.get(_RELEASE_TIME, releaseMinutes));
                        }
                    }
                }
            }
        }
    }
}