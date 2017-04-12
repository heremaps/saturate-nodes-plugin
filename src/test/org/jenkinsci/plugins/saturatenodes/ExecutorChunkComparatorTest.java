package org.jenkinsci.plugins.saturatenodes;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;

public class ExecutorChunkComparatorTest {
    @Test
    public void testComparatorWithDifferentTime() throws Exception {
        ExecutorChunkContainer executorChunk1 = Mockito.mock(ExecutorChunkContainer.class);
        when(executorChunk1.getConnectTime()).thenReturn(1L);

        ExecutorChunkContainer executorChunk2 = Mockito.mock(ExecutorChunkContainer.class);
        when(executorChunk2.getConnectTime()).thenReturn(2L);

        final List<ExecutorChunkContainer> chunks = new ArrayList<>();
        chunks.add(executorChunk1);
        chunks.add(executorChunk2);

        Collections.sort(chunks, new ExecutorChunkComparator());

        Assert.assertSame(chunks.get(0), executorChunk1);
        Assert.assertSame(chunks.get(1), executorChunk2);
    }

    @Test
    public void testComparatorWithSameTime() throws Exception {
        ExecutorChunkContainer executorChunk1 = getExecutorChunkContainer(10, "nodeD");
        ExecutorChunkContainer executorChunk2 = getExecutorChunkContainer(10, "nodeC");
        ExecutorChunkContainer executorChunk3 = getExecutorChunkContainer(1, "nodeB");
        ExecutorChunkContainer executorChunk4 = getExecutorChunkContainer(20, "nodeA");

        final List<ExecutorChunkContainer> chunks = new ArrayList<>();
        chunks.add(executorChunk1);
        chunks.add(executorChunk2);
        chunks.add(executorChunk3);
        chunks.add(executorChunk4);

        Collections.sort(chunks, new ExecutorChunkComparator());

        Assert.assertSame(chunks.get(0), executorChunk3);
        Assert.assertSame(chunks.get(1), executorChunk2);
        Assert.assertSame(chunks.get(2), executorChunk1);
        Assert.assertSame(chunks.get(3), executorChunk4);
    }

    @Test
    public void testComparatorWithSameChunks() throws Exception {
        ExecutorChunkContainer executorChunk = getExecutorChunkContainer(777, "node");

        int res = new ExecutorChunkComparator().compare(executorChunk, executorChunk);

        Assert.assertEquals(0, res);
    }

    private ExecutorChunkContainer getExecutorChunkContainer(long connectTime, String name) {
        ExecutorChunkContainer executorChunk3 = Mockito.mock(ExecutorChunkContainer.class);
        when(executorChunk3.getConnectTime()).thenReturn(connectTime);
        when(executorChunk3.getNodeName()).thenReturn(name);
        return executorChunk3;
    }
}