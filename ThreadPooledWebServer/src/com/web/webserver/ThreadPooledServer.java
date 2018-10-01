package com.web.webserver;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Orginally built by Jakob Jenkov http://tutorials.jenkov.com/java-multithreaded-servers/thread-pooled-server.html
 * @author Vinod Ananthapadmanabhan - Server implementation for a thread pooled web server
 *
 */
public class ThreadPooledServer implements Runnable{

    protected int serverPort = 8080;
    protected String documentRoot = "";
    protected ServerSocket serverSocket = null;
    protected boolean isStopped = false;
    protected Thread runningThread = null;
    
    // Initialize the thread pool object with a size of 10 threads
    protected ExecutorService threadPool =
        Executors.newFixedThreadPool(2);

    /**
     * Constructor for initializing a ThreadPoolServer object
     * @param port - Port on which the web server runs
     * @param documentRoot - Root directory on server for file access
     */
    public ThreadPooledServer(int port, String documentRoot){
        this.serverPort = port;
        this.documentRoot = documentRoot;
    }

    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    public void run(){
        synchronized(this){
            this.runningThread = Thread.currentThread();
        }
        openServerSocket();
        while(! isStopped()){
            Socket clientSocket = null;
            try {
                clientSocket = this.serverSocket.accept();
            } catch (IOException e) {
                if(isStopped()) {
                    System.out.println("Server Stopped.") ;
                    break;
                }
                throw new RuntimeException(
                    "Error accepting client connection", e);
            }
            this.threadPool.execute(
                new WorkerRunnable(clientSocket,
                    "Thread Pooled Server", documentRoot));
        }
        this.threadPool.shutdown();
        System.out.println("Server Stopped.") ;
    }


    /**
     * @return - true if the server process has been stopped
     */
    private synchronized boolean isStopped() {
        return this.isStopped;
    }

    /**
     * Method to stop server process
     */
    public synchronized void stop(){
        this.isStopped = true;
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException("Error closing server", e);
        }
    }

    /**
     * Method to open server socket
     */
    private void openServerSocket() {
        try {
            this.serverSocket = new ServerSocket(this.serverPort);
        } catch (IOException e) {
            throw new RuntimeException("Cannot open port + " + this.serverPort, e);
        }
    }
}