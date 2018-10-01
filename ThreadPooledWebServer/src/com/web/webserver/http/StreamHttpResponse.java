package com.web.webserver.http;

import java.io.*;
import com.web.webserver.helpers.Logger;

/**
 * HTTP Response class used in communication with the client.
 * This class contains the data that will be sent to the client,
 * including status line, headers, and response body.
 */
public class StreamHttpResponse extends HttpResponse {

    private final static String TAG = "me.homework.server.http.StreamHttpResponse";

    /**
     * Stream to be sent to the user.
     */
    private InputStream inputStream;

    public StreamHttpResponse(int statusCode, InputStream inputStream) {
        super();
        this.statusCode = statusCode;
        this.inputStream = inputStream;
    }

    /**
     * This function writes the HTTP response to an output stream.
     *
     * @param out the target {@link OutputStream} for writing
     */
    public void write(OutputStream out) {
        try {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));
            writer.write(getResponseLine());
            writer.write("\r\n");

            for (String key: headers.keySet()) {
                writer.write(key + ":" + headers.get(key));
                writer.write("\r\n");
            }
            writer.write("\r\n");

            if (inputStream != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                char[] buffer = new char[1024];
                int read;
                while ((read = reader.read(buffer)) != -1) {
                    writer.write(buffer, 0, read);
                }
                reader.close();
            }

            writer.flush();
        } catch (IOException e) {
            Logger.error(TAG, e.getMessage());
        }
    }
}

