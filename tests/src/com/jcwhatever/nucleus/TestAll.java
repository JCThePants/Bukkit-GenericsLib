package com.jcwhatever.nucleus;

import com.jcwhatever.nucleus.collections._CollectionsTestSuite;
import com.jcwhatever.nucleus.commands._CommandsTestSuite;
import com.jcwhatever.nucleus.events.manager._ManagerTestSuite;
import com.jcwhatever.nucleus.internal.providers.bankitems._InternalBankItemsTestSuite;
import com.jcwhatever.nucleus.internal.providers.economy._InternalEconomyTestSuite;
import com.jcwhatever.nucleus.internal.providers.friends._InternalFriendsTestSuite;
import com.jcwhatever.nucleus.regions._RegionsTestSuite;
import com.jcwhatever.nucleus.utils.signs._SignsTestSuite;
import com.jcwhatever.nucleus.sounds._SoundsTestSuite;
import com.jcwhatever.nucleus.storage._StorageTestSuite;
import com.jcwhatever.nucleus.utils._UtilsTestSuite;
import com.jcwhatever.nucleus.views._ViewTestSuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        _CollectionsTestSuite.class,
        _CommandsTestSuite.class,
        _ManagerTestSuite.class,
        _InternalBankItemsTestSuite.class,
        _InternalEconomyTestSuite.class,
        _InternalFriendsTestSuite.class,
        _RegionsTestSuite.class,
        _SignsTestSuite.class,
        _SoundsTestSuite.class,
        _StorageTestSuite.class,
        _UtilsTestSuite.class,
        _ViewTestSuite.class
})
public class TestAll {
}
