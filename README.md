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

By default Jenkins is using consistent hashing algorithm for load balancing. It always tries to execute job on the node there it was executed before.
This approach has one big disadvantage in case of cloud based CI - it keeps a lot of nodes up and running.

Saturate Nodes Load Balancer addresses this issue using pretty simple approach:
- select node with applicable label
- it orders node based on connection time (time then node was connected to Jenkins master)
- pick-up first available executor

As result it should minimise number of used nodes.

Let's take a look on one example. You have only one node:
- nodeA with 4 executors (e1, e2, e3, e4)

And you need to run 10 builds from 10 different jobs. To avoid a queue you use some cloud plugin (let's say [EC2 Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Amazon+EC2+Plugin)). Now you raised more nodes:
- nodeB with 4 executors (e5, e6, e7, e8)
- nodeC with 4 executors (e9, e10, e11, e12)

According Jenkins history it would be something like:
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

And if new builds from some of this jobs will come they will be fired on same nodes.
- nodeA => j1#2
- nodeB => j5#2
- nodeB => j8#2
- nodeC => j9#2

As you result cloud plugin will not be able to shutdown any node, because it's not idle.

How this would look like for Saturate Nodes Load Plugin? Pretty simple:
- nodeA => j1#2
- nodeA => j5#2
- nodeA => j8#2
- nodeA => j9#2

And nodeB and nodeC are not needed anymore - cloud plugin can disconnect them. 

Of course, such implementation has some disadvantages as well:
- it always tries to use all executors of each node
- it doesn't take in account any kind of history to balance the jobs
- old nodes will have zero chance to be replaced


Building
---

Prerequisites:

- JDK 7 (or above)
- Apache Maven

Build process is quite simple:

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

Plugin allows to enable/disable Load Balancer on-the-fly. 

If you want to monitor logs, you can add "org.jenkinsci.plugins.saturatenodes.SaturateNodesLoadBalancer" to the [loggers](https://wiki.jenkins-ci.org/display/JENKINS/Logging) of your Jenkins master.

Also you can configure [system Groovy script](https://wiki.jenkins-ci.org/display/JENKINS/Groovy+plugin) and execute it during a night (to minimise number of used nodes in night builds):
```groovy
import hudson.model.LoadBalancer
import org.jenkinsci.plugins.saturatenodes.SaturateNodesLoadBalancer

Jenkins.getInstance().getQueue().setLoadBalancer(new SaturateNodesLoadBalancer())
Jenkins.getInstance().getQueue().setLoadBalancer(LoadBalancer.CONSISTENT_HASH)
```

Authors
---

Alexander Akbashev - <alexander.akbashev@here.com>

License
---

Licensed under the [MIT License (MIT)](LICENSE).
