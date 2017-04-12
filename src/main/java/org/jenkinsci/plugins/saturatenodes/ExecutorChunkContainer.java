package org.jenkinsci.plugins.saturatenodes;

import hudson.model.queue.MappingWorksheet;

public interface ExecutorChunkContainer {
    String getNodeName();

    long getConnectTime();

    int getNumberOfIdleExecutors();

    MappingWorksheet.ExecutorChunk getExecutorChunk();
}
