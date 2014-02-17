Overview
========
Fetch metrics from a JMX (Java Management Extensions) source

Configuration
=============
```xml
<jmx name="jmx" url="url" username="username" password="password" provider-packages="provider.packages" persistent-connection="true">
	<ignore-object-names>
		<ignore-object-name regexp="ignored_.+" />
	</ignore-object-names>
	<connection-properties>
		<entry key="key" value="value" />
	</connection-properties>
	<socket-options connect-timeout="5" so-timeout="10" keep-alive="true" send-buffer-size="16384" receive-buffer-size="32768" />
</jmx> 
```
* The "jmx" input defined above will fetch metrics from a JMX server running at the given URL. It will use the specified credentials to authenticate against the JMX server.
* The attribute provider-packages contains the JMX provider packages separated by vertical bars (|). This parameter is passed directly to the connection factory.
* If the attribute persistent-connection is set to true, the JMX connection will be setup once and reused later when sampling the metrics. If set to false, each sampling will open a connection, fetch the metrics and close it again.
* The optional ignore-object-names is a list of regular expression of JMX objects that will be completely ignored by the input. They are optional and only make sense in cases when quering an object requires a lot of resources, causes warnings, etc.
* The optional connection-properties is a map of keys and values that will be passed directly to the JMX connection factory.
* The optional socket-options can be used to fine-tune TCP socket options for the JMX connection. The timeouts are in milliseconds and the buffer sizes in bytes.

Examples
========

Example 1 - query data over JMX from any java application (e.g. apache tomcat)
------------------------------------------------------------------------------
The following is an example to query metrics from a tomcat server running on tomcat.metrics-sampler.org with JMX port set to 7001 (the connection will be not be closed after each sampling to improve performance):
```xml
<jmx name="tomcat01" url="service:jmx:rmi:///jndi/rmi://tomcat.metrics-sampler.org:7001/jmxrmi" persistent-connection="true" />
```
You will need to start the tomcat server with enabled JMX remoting and a registry running on port 7001. You can achieve this by adding these startup arguments:

```
-Dcom.sun.management.jmxremote.port=7001 -Dcom.sun.management.jmxremote.rmi.port=7001 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false
```

This will set both the registry and the ephemeral ports to 7001 - you will be able to access the machine over firewalls (since version 1.7 on hotspot). JRockit uses different parameter names for the same settings.

Example 2 - query data over T3 from a weblogic server
-----------------------------------------------------
Query metrics from a weblogic managed server over t3 (alternatively you can enable JMX as in the case of tomcat):
```xml
<jmx name="wls01" url="service:jmx:t3://weblogic1.metrics-sampler.org:6001/jndi/weblogic.management.mbeanservers.runtime" username="weblogic" password="weblogic1" provider-packages="weblogic.management.remote" persistent-connection="true">
	<ignore-object-names>
		<ignore-object-name regexp="^com\.oracle\.jrockit:type=Flight.+" />
	</ignore-object-names>
	<connection-properties>
		<entry key="jmx.remote.x.request.waiting.timeout" value="100" />
	</connection-properties>
	<socket-options connect-timeout="100" so-timeout="200" keep-alive="false" send-buffer-size="16384" receive-buffer-size="16384" />
</jmx>
```
This will fetch metrics from the weblogic server running on weblogic1.metrics-sampler.org on port 6001 (the port you will see in the admin console). It will connect to the server over T3 (you will need to place the weblogic full client or the jmx weblogic jars on the classpath by copying them to lib.local). The connection will be authenticated with the supplied credentials. The input will ignore all Flight Recorder beans (as quering them produces warnings). It will set a connection factory property called jmx.remote.x.request.waiting.timeout to 100. It will also initialize some low-level socket options like the connection timeout which is set to 100 ms and the SO_TIMEOUT which is set to 200 ms. The TCP keep-alive option will be disabled and the send and receive buffers will be set to 16KB.


