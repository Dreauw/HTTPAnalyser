# HTTPAnalyser
A simple tool to analyse and capture the HTTP traffic


## Features
- Live capture of all the HTTP requests (and associate them with their responses)
- Save HTTP response to a file (reassemble the TCP packets if the response was fragmented)
- Download of the requested resource using the same HTTP Header


## Requirements
- Java 1.6 or higher
- [jnetpcap](http://jnetpcap.com/) (I used the 1.4.r1425 version but 1.3 should work too)
