# Saturate Nodes Load Balancer Plugin

A Jenkins plugin which allows to saturate nodes as much as possible.

Table of contents
---

1. [Overview](#overview)
1. [Building](#building)
1. [Basic Usage](#basic-usage)
1. [Authors](#authors)
1. [License](#license)

Overview
---

By default Jenkins uses a consistent hashing algorithm for load balancing: It always tries to execute a job on the same node where it was executed before.
This approach has one big disadvantage in case of cloud based CI - it keeps a lot of nodes up and running.

The Saturate Nodes Load Balancer plugin addresses this issue using a pretty simple algorithm:
- Select nodes of the specified label.
- Order node based on the time it was connected to the Jenkins master.
- Pick-up the available executor.

As a result it should minimize the number of used nodes.

Let's take a look at an example. Suppose there is only one node:
- nodeA with 4 executors (e1, e2, e3, e4)

Now you need to run 10 builds from 10 different jobs. To avoid a queue you use some cloud plugin (let's say [EC2 Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Amazon+EC2+Plugin)), which will dynamically spawn more nodes:
- nodeB with 4 executors (e5, e6, e7, e8)
- nodeC with 4 executors (e9, e10, e11, e12)

According to Jenkins history this would result in something like:
- nodeA => j1#1
- nodeA => j2#1
- nodeA => j3#1
- nodeA => j4#1
- nodeB => j5#1
- nodeB => j6#1
- nodeB => j7#1
- nodeB => j8#1
- nodeC => j9#1
- nodeC => j10#1

When new builds from some of these jobs will be started, they will get executed on same nodes:
- nodeA => j1#2
- nodeB => j5#2
- nodeB => j8#2
- nodeC => j9#2

As a result, the cloud plugin will not be able to shutdown any node, because it none is idle.

With the Saturate Nodes Load Balancer Plugin this would simply look like:
- nodeA => j1#2
- nodeA => j5#2
- nodeA => j8#2
- nodeA => j9#2

And nodeB and nodeC are not needed anymore - the cloud plugin can shut them down.

Of course, such a simple implementation has some disadvantages as well:
- It always tries to use all executors of each node.
- It does not take into account any kind of history to balance the jobs.
- Old nodes will have no chance to get replaced.


Building
---

Prerequisites:

- JDK 7 (or above)
- Apache Maven

Build process is straight forward:

```Shell
mvn install
```

Basic Usage
---

To get started:

1. Install the plugin.
1. Go to the Jenkins Configuration.
1. Find "Saturate Nodes Load Balancer".
1. Enable "Override default load balancer".

The plugin allows you to enable/disable the Load Balancer on-the-fly.

If you want to monitor its logs, you can add `org.jenkinsci.plugins.saturatenodes.SaturateNodesLoadBalancer` to the [loggers](https://wiki.jenkins-ci.org/display/JENKINS/Logging) of your Jenkins master.

Also, you can configure the [system Groovy script](https://wiki.jenkins-ci.org/display/JENKINS/Groovy+plugin) and execute it during the night (to minimize number of used nodes in nightly builds):
```groovy
import org.jenkinsci.plugins.saturatenodes.SaturateNodesLoadBalancer

Jenkins.getInstance().getQueue().setLoadBalancer(new SaturateNodesLoadBalancer())
```
And to restore default one:
```groovy
import hudson.model.LoadBalancer

Jenkins.getInstance().getQueue().setLoadBalancer(LoadBalancer.CONSISTENT_HASH)
```

Authors
---

Alexander Akbashev - <alexander.akbashev@here.com>

License
---

Licensed under the [MIT License (MIT)](LICENSE).
