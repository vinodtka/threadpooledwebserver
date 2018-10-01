package com.web.webserver;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;

import com.web.webserver.http.EmptyHttpResponse;
import com.web.webserver.http.FileHttpResponse;
import com.web.webserver.http.HeadHttpResponse;
import com.web.webserver.http.HttpMethod;
import com.web.webserver.http.HttpRequest;
import com.web.webserver.http.HttpResponse;
import com.web.webserver.http.HttpStatus;
import com.web.webserver.http.RawHttpRequest;
import com.web.webserver.http.StreamHttpResponse;


/**
 * @author Orginally built by Jakob Jenkov http://tutorials.jenkov.com/java-multithreaded-servers/thread-pooled-server.html
 * @author Vinod Ananthapadmanabhan - added keep-alive behavior to server implementation
 * 
 * Worker thread implementation class for a thread pooled web server
 *
 */
public class WorkerRunnable implements Runnable{

    protected Socket clientSocket = null;
    protected String serverText = null;
    protected String documentRoot = null;

    /**
     * @param clientSocket Client socket
     * @param serverText Server name and info
     * @param documentRoot Root directory on server for file access
     */
    public WorkerRunnable(Socket clientSocket, String serverText, String documentRoot) {
        this.clientSocket = clientSocket;
        this.serverText   = serverText;
        this.documentRoot = documentRoot;
    }

    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    public void run() {
        try {
            InputStream input  = clientSocket.getInputStream();
            OutputStream output = clientSocket.getOutputStream();            
            try {
				HttpRequest request = HttpRequest.parse(input);

				if (request != null) {
				    HttpResponse response = handle(request);

				    response.getHeaders().put("Server", "Server v1"); // TODO parameterize this
				    response.getHeaders().put("Date", Calendar.getInstance().getTime().toString());
				    if (request.isKeepAlive()) {
				    	response.getHeaders().put("Connection", "keep-alive"); // TODO use a constant/enum
				    	clientSocket.setKeepAlive(true);
				    	clientSocket.setSoTimeout(5000);
				    }
				    response.write(output);
				} else {
				    new RawHttpRequest(501, "Server only accepts HTTP protocol").write(output);
				}
			} 
            catch(SocketTimeoutException e)
            {
                try {
                	clientSocket.close();
                } catch (IOException e1) {
                    e1.printStackTrace(); //TODO handle exception
                }
                System.out.println("Socket timeout, Connection closed.");
            }catch (Exception e) {
				e.printStackTrace(); //TODO handle exception
			}
        } catch (IOException e) {
            e.printStackTrace(); //TODO handle exception
        }
    }
    
    /**
     * @param request HttpRequest object
     * @return HttpResponse object
     */
    public HttpResponse handle(HttpRequest request) {

        String path = request.getPath();

        HttpResponse response;

        switch (request.getMethod()) {
            case HttpMethod.GET:
                Path requestedFile = Paths.get(documentRoot, path);
                if (requestedFile.normalize().startsWith(Paths.get(documentRoot).normalize())) {
                    if (Files.exists(requestedFile)) {
                        if (Files.isDirectory(requestedFile)) {
                            response = new EmptyHttpResponse(HttpStatus.FORBIDDEN);
                        } else {
                            response = new FileHttpResponse(HttpStatus.OK,
                                    new File(Paths.get(documentRoot, path).toString()));
                        }
                    } else {
                        response = new EmptyHttpResponse(HttpStatus.NOT_FOUND);
                    }
                } else {
                    response = new EmptyHttpResponse(HttpStatus.FORBIDDEN);
                }
                break;
            case HttpMethod.TRACE:
                response = new StreamHttpResponse(HttpStatus.OK, request.getInputStream());
                break;
            case HttpMethod.HEAD:
                if (Files.exists(Paths.get(documentRoot, path))) {
                    response = new HeadHttpResponse(HttpStatus.OK,
                            new File(Paths.get(documentRoot, path).toString()));
                } else {
                    response = new EmptyHttpResponse(HttpStatus.NOT_FOUND);
                }
                break;
            default:
                response = new EmptyHttpResponse(HttpStatus.NOT_IMPLEMENTED);
                break;
        }

        return response;
    }
}