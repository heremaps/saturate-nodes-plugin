package org.jenkinsci.plugins.saturatenodes;

import hudson.model.queue.MappingWorksheet;

public class ExecutorChunkContainerImpl implements ExecutorChunkContainer {
    private final MappingWorksheet.ExecutorChunk chunk;

    ExecutorChunkContainerImpl(MappingWorksheet.ExecutorChunk chunk) {
        this.chunk = chunk;
    }

    @Override
    public MappingWorksheet.ExecutorChunk getExecutorChunk() {
        return chunk;
    }

    @Override
    public int getNumberOfIdleExecutors() {
        return chunk.computer.countIdle();
    }

    @Override
    public String getNodeName() {
        return chunk.computer.getName();
    }

    @Override
    public long getConnectTime() {
        return chunk.computer.getConnectTime();
    }
}
