package com.jcwhatever.nucleus.internal.providers.friends;

import com.jcwhatever.nucleus.providers.friends.FriendLevels;
import com.jcwhatever.nucleus.providers.friends.IFriend;
import com.jcwhatever.nucleus.providers.friends.IFriendTest;

import java.util.UUID;

public class NucleusFriendTest extends IFriendTest {

    NucleusFriendsProvider provider = new NucleusFriendsProvider();

    @Override
    protected IFriend createFriend(UUID playerId, UUID friendId) {
        return provider.getDefaultContext().add(playerId, friendId, FriendLevels.CASUAL);
    }
}