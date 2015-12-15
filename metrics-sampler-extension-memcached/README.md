Overview
========
Fetch metrics from a memcached server.

Configuration
=============
```xml
<memcached name="memcached1" host="memcached.example.com" port="11211">
    <!-- optional socket options -->
	<socket-options connect-timeout="5" so-timeout="10" keep-alive="true" send-buffer-size="16384" receive-buffer-size="32768" />
</memcached>
```

* The "memcached1" input defined above will fetch metrics from a memcached server running at the given host and listening on the given port.
* It will use the "stats" command to fetch the metrics. The metric names will be stats.general.<key>=<value>. The <key> will be for example "uptime", "get_hits" etc.
* The optional socket-options can be used to fine-tune TCP socket options for the connection. The timeouts are in milliseconds and the buffer sizes in bytes.
* The TCP connection to the memcached server will be created and closed every time metrics are sampled 
