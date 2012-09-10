Overview
========
metrics-sampler is a java program which regularly queries metrics from a configured set of inputs, selects and renames them using regular expressions and sends them to a configured set of outputs. It supports JMX and JDBC as inputs and Graphite as output out of the box. Writing new extensions containing new inputs, outputs, samplers and selectors is pretty straight-forward.

Example Configuration
---------------------
Check out the following configuration as a quick-start:
	<!-- pool-size is the number of threads to use for the samplers -->
	<configuration pool-size="10">
		<inputs>
			<!-- this is an example of a template - its values will be copied to any input that references it using the template attribute. Due to abstract=true it can never be used in a sampler -->
			<jmx name="wls-template" abstract="true" username="admin" password="weblogic1" provider-packages="weblogic.management.remote" persistent-connection="true">
				<ignore-object-names>
					<ignore-object-name regexp="^com\.oracle\.jrockit:type=Flight.+" />
				</ignore-object-names>
			</jmx>
			
			<!-- WebLogic JMX server. Username, password etc. come from the template wls-template -->
			<jmx name="wls01" url="service:jmx:t3://weblogic1.metrics-sampler.org:6001/jndi/weblogic.management.mbeanservers.runtime" template="wls-template" />
			<jmx name="wls02" url="service:jmx:t3://weblogic2.metrics-sampler.org:6001/jndi/weblogic.management.mbeanservers.runtime" template="wls-template" />

			<!-- Execute the given query(ies) over JDBC and use the first column as metric name, the second as metric value and the third one as timestamp -->
			<jdbc name="oracle01" url="jdbc:oracle:thin:@//oracle1.metrics-sampler.org:1521/EXAMPLE" username="user" password="password" driver="oracle.jdbc.OracleDriver">
				<query>select replace(T2.host_name||'.'||T2.instance_name||'.'||replace(replace(replace(replace(metric_name,'/',''),'%','Perc'),'(',''),')',''),' ','_') as metric, value, (25200 + round((end_time - to_date('01-JAN-1970','DD-MON-YYYY')) * (86400),0))*1000 as dt from gv$sysmetric T1, gv$instance T2 where T1.intsize_csec between 1400 and 1600 and T1.inst_id = T2.INST_ID</query>
			</jdbc>
			
			<!-- Tomcat JMX server -->
			<jmx name="tomcat01" url="service:jmx:rmi:///jndi/rmi://tomcat.metrics-sampler.org:7001/jmxrmi" persistent-connection="true" />
			
			<!-- Apache mod_qos status page -->
			<mod-qos name="apache01" url="http://apache1.metrics-sampler.org:80/qos-viewer?auto" username="user" password="pass" auth="basic"/>
		</inputs>
		<outputs>
			<!-- Write to the standard output -->
			<console name="console" />
			<!-- Send to graphite running on port 2003 -->
			<graphite name="graphite" host="graphite.metrics-sampler.org" port="2003" />
		</outputs>
		<!-- we define some regular expressions in groups so that we can reuse them later in the samplers -->
		<selector-groups>
			<selector-group name="wls">
				<!-- from-name is a regular expression that is matched against e.g. the JMX Metric Name (consisting of canonical object name # attribute name). to-name is an expression (not a regular expression) that can use placeholders for things like captured groups from the name's regular expression. -->
				<regexp from-name="com\.bea:Name=DataSource_(.+),ServerRuntime=.+,Type=JDBCOracleDataSourceRuntime\.(ActiveConnectionsAverageCount|ActiveConnectionsCurrentCount|ActiveConnectionsHighCount|ConnectionDelayTime|ConnectionsTotalCount|CurrCapacity|CurrCapacityHighCount|FailuresToReconnectCount|HighestNumAvailable|HighestNumUnavailable|LeakedConnectionCount|NumAvailable|NumUnavailable|ReserveRequestCountWaitSecondsHighCount|WaitingForConnection.*)" to-name="${input.name}.jdbc.${name[1]}.${name[2]}" />
				<regexp from-name="com\.bea:Name=JTARuntime,ServerRuntime=.*,Type=JTARuntime\.(.*TotalCount)" to-name="${input.name}.jta.${name[1]}" />
				<regexp from-name="com\.bea:Name=ThreadPoolRuntime,ServerRuntime=.*,Type=ThreadPoolRuntime\.(CompletedRequestCount|ExecuteThreadIdleCount|ExecuteThreadTotalCount|HoggingThreadCount|MinThreadsConstraintsCompleted|MinThreadsConstraintsPending|PendingUserRequestCount|QueueLength|SharedCapacityForWorkManagers|StandbyThreadCount|Throughput)" to-name="${input.name}.threads.${name[1]}"/>
				<regexp from-name="com\.bea:Name=.*,ServerRuntime=.*,Type=JRockitRuntime\.(JvmProcessorLoad|TotalGarbageCollectionCount|TotalGarbageCollectionTime|FreePhysicalMemory|UsedPhysicalMemory|Uptime)" to-name="${input.name}.jrockit.${name[1]}" />
			</selector-group>
			<selector-group name="tomcat">
				<regexp from-name="Catalina:type=GlobalRequestProcessor,name=.http-bio-9240.\.(requestCount|bytesSent|bytesReceived)" to-name="${input.name}.http.${name[1]}"/>
			</selector-group>
			<selector-group name="mod_qos">
				<regexp from-name=".*,metric=([^,]+),path=/([^.]+)\.(current|limit)" to-name="${input.name}.${name[2]}.${name[1]}.${name[3]}"/>
				<regexp from-name=".*,metric=([^,]+)$" to-name="${input.name}.${name[1]}"/>
				<regexp from-name=".*,metric=([^,]+)\.(current|limit)" to-name="${input.name}.${name[1]}.${name[2]}"/>
			</selector-group>
		</selector-groups>
		<!-- These are the actual active runtime components that sample the date from their input, use the given selectors to determine which metrics are relevant (and rename them) and sends them to the given outputs. An input without a sampler does not do anything. -->
		<samplers>
			<!-- fetch data from wls01 input, use the regular expressions in a group named "wls" to select and rename metrics and send them to graphite every 10 seconds. -->
			<sampler input="wls01" outputs="graphite" interval="10">
				<selectors>
					<use-group name="wls" />
				</selectors>
			</sampler>
			<sampler input="wls02" outputs="graphite" interval="10">
				<selectors>
					<use-group name="wls" />
				</selectors>
			</sampler>
			<sampler input="tomcat01" outputs="graphite" interval="10">
				<selectors>
					<use-group name="tomcat" />
				</selectors>
			</sampler>
			<sampler input="apache01" outputs="graphite" interval="10">
				<selectors>
					<use-group name="mod_qos" />
				</selectors>
			</sampler>
			<sampler input="oracle01" outputs="graphite" interval="10">
				<selectors>
					<!-- we can of course specify regular expressions directly -->
					<regexp from-name="(.*)" to-name="${name[1]}"/>
				</selectors>
			</sampler>
		</samplers>
	</configuration>

Supported Inputs
-----------------
* Java Management Extensions (JMX) - queries object names and attributes from a remote JMX server. The reader caches all meta-data until a reconnect. The name of the metrics consist of the canonicalized object name + '#' + attribute name.
* JDBC - sequentially execute a list of SQL queries and interpret the returned rows as metrics. The reader currently does not reuse the data-base connection between samplings. Queries must return either two or three columns - the first one is the metric's name and the second one is its value. The optional third one is a timestamp (in milliseconds since epoch start).
* mod_qos - parses the output of the mod_qos status page (with option ?auto) and exposes the values in a more usable format. The reader uses non-persistent HTTP connection and queries both metadata and data when opened.

Supported Selectors
-------------------
* Regular expressions selector
Matches metrics by their names using regular expressions. Each metric can then be renamed using expressions which can refer to the input's name and the matching groups of the regular expressions.

Supported Outputs
-----------------
* Console (STDOUT)
* Graphite [http://graphite.wikidot.com]

Quick start
===========
1. Download the metrics-sampler-distribution-<version>-all.tar.gz
2. Unpack it into a directory of your choice, e.g. metrics-sampler-<version>
3. Create a configuration in config/config.xml using config/config.xml.example as starting point
4. If you want to list all the metrics from your configured inputs you can call "bin/metrics-sampler.sh metadata". This will output all names and descriptions of the available metrics for each input.
5. Run "bin/metrics-sampler.sh check" to verify that each selector of each sampler matches at least one metric
6. Start the daemon using "bin/metrics-sampler.sh start". Logs are located in logs/metrics-sampler.log and in logs/console.out
7. You can stop the daemon using "bin/metrics-sampler.sh stop"

Examples Configuration Files
============================
* Checkout the metrics-sampler-distribution/src/configs/ for configuration examples that gather JMX metrics from a WebLogic server, a Tomcat server and an apache server from mod_qos's status page and sends them to graphite. Each metric is sampled every 10 seconds.

Extensions
==========
It should be pretty easy to extend the program with new inputs, outputs, samplers and selectors. For this you will need to create a new module/project like this (you could also check out the extensions-* modules which use the same mechanism):
* Add metrics-sampler-core to the classpath of your program/module (e.g. maven dependency)
* Create the file "META-INF/services/org.metricssampler.service.Extension" in src/main/resources (or in any location that lands in your compiled jar) containg the fully qualified class name of a class that implements org.metricssampler.service.Extension
* Your org.metricssampler.service.Extension implementation will return your custom XBeans (XML configuration beans)
* You will have to implement an org.metricssampler.service.LocalObjectFactory (e.g. by extending org.metricssampler.service.AbstractLocalObjectFactory) so that you can create the actual input readers, output writers etc. from their configurations
* Put the resulting jar file on your classpath and you are ready to go (e.g. copy it to the lib/ directory of your installation)
* If you think the extension might be of any use to anyone else - please share it.

Internals
=========
* I chose to use slf4j in all classes with logback under the hood as it is pretty simple to configure
* The graphite writer currently disconnects on each sampling but could be improved to keep the connection (or even better let that be configurable)
* I use XStream to load the XML configuration. The XML is mapped to *XBean instances which are basically pojos with the some added abilities like validating their data and converting themselves to the more usable and configuration format independent *Config pojos. The *Config pojos are value objects.
* The core implementation took about 2 days. In that light it might be more understandable why there are no unit tests. I intend however to write some in the future.
* Currently the stop consists of killing the process. It would be nice to implement a graceful shutdown which can stop all samplers and disconnect all readers and writers
* mod_qos uses an URLConnection to fetch the data. Currently we also have a trivial implementation of basic authentication. It would be nice to switch to httpcomponents so that we get all the nice stuff out-of-the-box (also a better API) - maybe even persistent connections.

Compatibility
=============
* Tested with Hotspot/JRockit JVM 1.6
* Tested with Tomcat 7 and Weblogic Server 12c (provided that wlfullclient.jar (the jmx client and t3 protocol jars) is on the classpath)
* You might need to add -Dsun.lang.ClassLoader.allowArraySyntax=true as JVM parameter in the metrics-sampler.sh script if you are connecting using JVM 1.6 client to a JVM 1.5 server

Changelog
=========

Version 0.4.0
-------------
* Renamed most of the XML configuration elements
* Added support for input templates
* Fixed problems with multiple JDBC drivers
* Renamed to metrics-sampler (from jmx-sampler) as it better reflects the purpose of the application
* Improved debug logging

Version 0.3.2
-------------
* Use -f option for readlink for better compatibility
* Added support for ignoring JMX object names (see config.xml.example)
* Added support for placeholders definitions - global (directly under configuration) and in the default sampler (see config.xml.example)
* Added support for mapping of placeholder values using ${fn:map(dictionary_key,placeholder_for_entry_key)} (see config.xml.example) 

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
