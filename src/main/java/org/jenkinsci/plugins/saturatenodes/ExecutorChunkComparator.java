package org.jenkinsci.plugins.saturatenodes;

import java.io.Serializable;
import java.util.Comparator;

final class ExecutorChunkComparator implements Comparator<ExecutorChunkContainer>, Serializable {
    public int compare(ExecutorChunkContainer ecc1, ExecutorChunkContainer ecc2) {
        if (ecc1 == ecc2) {
            return 0;
        }

        if (ecc1.getConnectTime() == ecc2.getConnectTime()) {
            return ecc1.getNodeName().compareTo(ecc2.getNodeName());
        } else if (ecc1.getConnectTime() > ecc2.getConnectTime()) {
            return 1;
        } else {
            return -1;
        }
    }
}
