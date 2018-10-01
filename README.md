# threadpooledwebserver
This project is a Java based thread pooled implementation of a web server.

It is based on a tutorial provided by Jakob Jenkov (http://tutorials.jenkov.com/java-multithreaded-servers/thread-pooled-server.html). It also contains libraries developed by Mihail (https://github.com/warchildmd/webserver)

The server can be launched by calling the Main.java class. It runs on port 9000 and provides file access to path "C:\Temp"on the server.

The thread pool is set to a size of 2.

Additionally, http header parameter "keep-alive" has been implemented to maintain TCP connections within a timeout of 5 seconds. Local testing shows the first request for a small "index.html" file to take ~ 80 msec and subsequent calls to take ~ 15 msecs.
