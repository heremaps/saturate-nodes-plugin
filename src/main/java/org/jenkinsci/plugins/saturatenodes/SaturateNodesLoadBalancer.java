package org.jenkinsci.plugins.saturatenodes;

import hudson.Extension;
import hudson.init.InitMilestone;
import hudson.init.Initializer;
import hudson.model.Describable;
import hudson.model.Descriptor;
import hudson.model.LoadBalancer;
import hudson.model.Queue;
import hudson.model.queue.MappingWorksheet;
import hudson.model.queue.MappingWorksheet.ExecutorChunk;
import hudson.model.queue.MappingWorksheet.Mapping;
import jenkins.model.Jenkins;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.StaplerRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;


public class SaturateNodesLoadBalancer extends LoadBalancer implements Describable<SaturateNodesLoadBalancer> {
    private static final Logger LOGGER = Logger.getLogger(SaturateNodesLoadBalancer.class.getName());
    private static final Comparator<ExecutorChunkContainer> EXECUTOR_CHUNK_COMPARATOR = new ExecutorChunkComparator();
    private static final DescriptorImpl DESCRIPTOR = new DescriptorImpl();

    @Override
    public MappingWorksheet.Mapping map(Queue.Task task, MappingWorksheet ws) {
        List<ExecutorChunkContainer> usableChunks = getApplicableSortedByConnectTime(ws);
        Mapping m = ws.new Mapping();
        if (assignExecutors(m, usableChunks) && m.isCompletelyValid()) {
            return m;
        }

        LOGGER.info(String.format("Unable to define ExecutorChunks for %s. Will re-try", task.getName()));
        return null;
    }

    @SuppressWarnings("unused")
    @Initializer(after= InitMilestone.PLUGINS_STARTED, fatal=false)
    public static void installLoadBalancer()
    {
        final SaturateNodesLoadBalancer.DescriptorImpl descriptor = (SaturateNodesLoadBalancer.DescriptorImpl) Jenkins.getActiveInstance().getDescriptorOrDie(SaturateNodesLoadBalancer.class);
        updateLoadBalancer(descriptor.getEnableSaturateNodesBalancer());
    }

    private static void updateLoadBalancer(boolean enableCloudLoadBalancer) {
        if (enableCloudLoadBalancer) {
            LOGGER.info("Use saturate nodes load balancer");
            Jenkins.getActiveInstance().getQueue().setLoadBalancer(new SaturateNodesLoadBalancer());
        } else {
            LOGGER.info("Use default load balancer");
            Jenkins.getActiveInstance().getQueue().setLoadBalancer(LoadBalancer.CONSISTENT_HASH);
        }
    }

    private List<ExecutorChunkContainer> getApplicableSortedByConnectTime(MappingWorksheet ws) {
        final List<ExecutorChunkContainer> chunks = new ArrayList<>();
        for (MappingWorksheet.WorkChunk workChunk : ws.works) {
            for (ExecutorChunk ec : workChunk.applicableExecutorChunks()) {
                if (ec.computer.isPartiallyIdle()) {
                    chunks.add(new ExecutorChunkContainerImpl(ec));
                }
            }
        }

        Collections.sort(chunks, EXECUTOR_CHUNK_COMPARATOR);
        return chunks;
    }

    private boolean assignExecutors(Mapping m, List<ExecutorChunkContainer> executors) {
        int i = 0;

        for (ExecutorChunkContainer executorChunkContainer : executors) {
            for (int j = 0; j < executorChunkContainer.getNumberOfIdleExecutors(); j++) {
                m.assign(i, executorChunkContainer.getExecutorChunk());
                i++;

                if (m.size() == i) {
                    return true;
                }
            }
        }

        m.assign(i, null);
        return false;
    }

    @Override
    public Descriptor<SaturateNodesLoadBalancer> getDescriptor() {
        return DESCRIPTOR;
    }

    @Extension
    public static final class DescriptorImpl extends Descriptor<SaturateNodesLoadBalancer> {
        private boolean enableSaturateNodesBalancer;

        public DescriptorImpl() {
            load();
        }

        public String getDisplayName() {
            return "Saturate Nodes Load Balancer";
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            enableSaturateNodesBalancer = formData.getBoolean("enableSaturateNodesBalancer");
            save();

            updateLoadBalancer(enableSaturateNodesBalancer);

            return super.configure(req,formData);
        }

        @SuppressWarnings("unused")
        public boolean getEnableSaturateNodesBalancer() {
            return enableSaturateNodesBalancer;
        }
    }
}