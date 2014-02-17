Overview
========
Fetch a HTTP URL and extract metrics from the response.

Configuration
=============
```xml
<http name="example1" url="http://localhost" username="username" password="password" preemptive-auth="false">
	<headers>
		<entry key="header" value="val" />
	</headers>
	<regexp-response-parser>
		<regexp-line-format expression="\s*(\S+)\s*=\s*(\S+)\s*" name-index="1" value-index="2" />
		<regexp-line-format expression="\s*(\S+)\s*:\s*(\S+)\s*" name-index="1" value-index="2" />
	</regexp-response-parser>
</http>
```

The example above will fetch the HTTP URL "http://localhost" using "username" and "password" as HTTP authentication credentials. It will not send
the credentials automatically (preemptive-auth=false) but will rather first request the URL and provide them if the server responds with HTTP 403
Forbidden. The request will also contain the HTTP header "header" with value "val". Each line of the response will be matched against the two 
regular expressions. If a regex matches, the metric name will be taken from the first capturing group and the value from the second one. Lines that
do not match any regex will be ignored. If a line matches both regular expressions, the first one matching will be used.

