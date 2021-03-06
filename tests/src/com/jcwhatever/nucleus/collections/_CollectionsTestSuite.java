package com.jcwhatever.nucleus.collections;

import com.jcwhatever.nucleus.collections.observer.agent._AgentCollectionTestSuite;
import com.jcwhatever.nucleus.collections.players._PlayerCollectionTestSuite;
import com.jcwhatever.nucleus.collections.timed._TimedCollectionTestSuite;
import com.jcwhatever.nucleus.collections.wrap._SyncCollectionTestSuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/*
 * 
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        _SyncCollectionTestSuite.class,
        _AgentCollectionTestSuite.class,
        _PlayerCollectionTestSuite.class,
        _TimedCollectionTestSuite.class,

        ArrayListPaginatorTest.class,
        CircularQueueTest.class,
        ElementCounterTest.class,
        OutputBufferListTest.class,
        PaginatedTest.class,
        TreeEntryNodeTest.class,
        TreeNodeTest.class
})
public class _CollectionsTestSuite {
}
