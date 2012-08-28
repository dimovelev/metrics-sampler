Overview
========
jmx-sampler is a java program which regularly queries values from a configured set of sources (called readers), transforms them using a set of rules and sends them to a configured set of destinations (called writers). 
It is also pretty easy to write new extensions containing additional readers, writeres, samplers and transformers.

Supported readers
-----------------
* Java Management Extensions (JMX) server
The jmx-reader lists all beans and their attributes each time it connects to the server and caches them (metadata). Listing the beans and their attributes for each sample will cost too much overhead (weblogic has about 30K attributes). Updating the metadata upon reconnect is a trade-off which should work good enough assuming that each application redeployment is accompanied by a server restart and thus a reconnect of the reader. When updated, the metadata gets pushed to all transformers so that they can determine the names of the beans they should sample. This also means that without a reconnect no new beans will be detected. For performance reasons the jmx-readers should normally use persistent connections (meaning that they will not disconnect after each sampling).
* mod_qos status metrics
The mod-qos-reader parses the output of mod_qos status page (with option ?auto) and exposes the values in a more usable format. The reader uses non-persistent HTTP connection and queries both metadata and data when opened.
* JDBC
The jdbc-reader can execute SQL queries regularly and makes them available as metrics for further processing. The reader currently does not keep the data-base connection between samplings. Queries must return either two or three columns - the first one is the metric's name and the second one is the value. The optional third one is a timestamp (in milliseconds since epoch start).

Supported transformations
-------------------------
* Regular expressions transformer
Matches metrics by their names using regular expressions. Each metric can then be renamed using expressions which can refer to the reader's name and the matching groups of the regular expressions.

Supported writers
-----------------
* Console (STDOUT)
* Graphite [http://graphite.wikidot.com]

Quick start
===========
1. Download the jmx-sampler-distribution-<version>-all.tar.gz to a linux machine
2. Unpack it into a directory of your choice, e.g. jmx-sampler-distribution-<version>-all
3. Create a configuration in config/config.xml using config/config.xml.example as base
4. If you want to list all the metrics from your configured readers you can call bin/jmx-sampler.sh metadata. This will output all names and descriptions of the available metrics
5. Run bin/jmx-sampler.sh check to verify that each transformer matches at least one metric
6. Start the daemon using bin/jmx-sampler.sh start. Logs are located in log/jmx-sampler.log and in nohup.out
7. You can stop the daemon using bin/jmx-sampler.sh stop

Examples
========
* Checkout the jmx-sampler-distribution/src/configs/config.xml.example for a configuration that gathers JMX metrics from a WebLogic server, a Tomcat server and an apache server from mod_qos's status page and sends them to graphite. Each metric is sampled every 10 seconds.

Extensions
==========
It should be pretty easy to extend the program with new readers, writers, samplers and transformers. For this you will need to create a new module/project like this (you could also check out the extensions-* modules which use the same mechanism):
* Put jmx-sampler-core on the classpath to your program/module (e.g. maven dependency)
* Create the file "META-INF/services/org.jmxsampler.service.Extension" in src/main/resources (or in any location that lands in your compiled jar) containg the fully qualified class name of a class that implements org.jmxsampler.service.Extension
* Your org.jmxsampler.service.Extension implementation will return your custom ReaderXBean, WriterXBean, SamplerXBean, SimpleMappingXBean classes
* You will have to implement an org.jmxsampler.service.LocalObjectFactory (e.g. by extending org.jmxsampler.service.AbstractLocalObjectFactory) so that you can create the actual readers, writers etc. from their configurations
* Put the resulting jar on your classpath and you are ready to go (e.g. copy it to the lib/ directory of your installation)

Internals
=========
* I chose to use slf4j in all classes with logback under the hood as it is pretty simple to configure
* The graphite writer currently disconnects on each sampling but could be improved to keep the connection (or even better let that be configurable)
* I use XStream to load the XML configuration. The XML is mapped to *XBean instances which are basically pojos with the some added abilities like validating their data and converting themselves to the more usable and configuration format independent *Config pojos. The *Config pojos are value objects.
* The core implementation took about 2 days. In that light it might be more understandable why there are no unit tests. I intend however to write some in the future.
* Currently the stop consists of killing the process. It would be nice to implement a graceful shutdown which can stop all samplers and disconnect all readers and writers
* mod_qos uses the URLConnection to fetch the data. Currently we also have a trivial implementation of basic authentication. It would be nice to switch to httpcomponents so that we get all the nice stuff right away (also a better API) - maybe even persistent connections.

Compatibility
=============
* Tested with Hotspot/JRockit JVM 1.6
* Tested with tomcat 7 and Weblogic Server 12c (provided that wlfullclient.jar is on the classpath)
* You might need to add -Dsun.lang.ClassLoader.allowArraySyntax=true as JVM parameters in the jmx-sampler.sh script if you are connecting using JVM 1.6 client to a JVM 1.5 server

Changelog
=========

Version 0.3.2
-------------
* Use -f option for readlink for better compatibility

Version 0.3.1
-------------
* Switched to three number versioning
* Readers wrap the metric values in a MetricValue object containing a timestamp. This way metrics for older time intervals (than the current time) can be returned.
* Readers may not know the metadata before actually quering the metrics. In such cases the transformers fetch all metrics through a different method (readAllMetrics()).
* Added JDBC reader support
* The console output uses the metric's timestamp not the current timestamp
* The console output has correct time now (hh:mm not mm:hh)
* Graphite writer replaces spaces with underscores in metric names
* The check command outputs the number of matched metrics for each transformer
* Improved example configuration with hotspot and jrockit metrics
* Support for composite JMX metrics
* Switched to canonical jmx object names for better compatibility
* Do not check disabled samplers

Version 0.3
-----------
* Switched from ant to maven and modularized the readers/writers/etc
* Implemented a mod_qos reader
* Added check() method to samplers so that they can check their configurations

Version 0.2
-----------
* Added extension support using java SPI

Version 0.1
-----------
* Initial implementation of a simple jmx reader, graphite writer and regular expression transformations
