package com.web.webserver;

/**
 * @author Originally built by Jakob Jenkov http://tutorials.jenkov.com/java-multithreaded-servers/thread-pooled-server.html
 * @author Vinod Ananthapadmanabhan - Server startup class
 *
 */
public class Main {
	/**
	 * @param args Main arguments
	 */
	public static void main(String[] args) {
		//Instantiate server object with a port
		ThreadPooledServer server = new ThreadPooledServer(9000, "C:\\Temp"); // TODO parameterize this
		
		//Start the thread pool
		new Thread(server).start();
	
		try {
		    Thread.sleep(20 * 10000);
		} catch (InterruptedException e) {
		    e.printStackTrace();
		}
		System.out.println("Stopping Server"); // TODO log this instead
		server.stop();
	}
}
