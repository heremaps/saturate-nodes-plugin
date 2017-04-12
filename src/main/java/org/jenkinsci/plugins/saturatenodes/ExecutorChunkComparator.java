package org.jenkinsci.plugins.saturatenodes;

import hudson.model.Computer;
import hudson.model.queue.MappingWorksheet;

import java.io.Serializable;
import java.util.Comparator;

final class ExecutorChunkComparator implements Comparator<MappingWorksheet.ExecutorChunk>, Serializable {
    public int compare(MappingWorksheet.ExecutorChunk executorChunk1, MappingWorksheet.ExecutorChunk executorChunk2) {
        if (executorChunk1 == executorChunk2) {
            return 0;
        }

        final Computer computer1 = executorChunk1.computer;
        final Computer computer2 = executorChunk2.computer;

        if (computer1.getConnectTime() == computer2.getConnectTime()) {
            return executorChunk1.computer.getName().compareTo(executorChunk2.computer.getName());
        } else if (computer1.getConnectTime() > computer2.getConnectTime()) {
            return 1;
        } else {
            return -1;
        }
    }
}
