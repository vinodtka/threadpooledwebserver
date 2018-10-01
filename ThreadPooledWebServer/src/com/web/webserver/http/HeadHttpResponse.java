package com.web.webserver.http;

import java.io.*;

import com.web.webserver.helpers.Logger;

/**
 * HttpResponse extension that only writes headers.
 */
public class HeadHttpResponse extends FileHttpResponse {

    private static String TAG = "HeadHttpResponse";

    /**
     * File to be sent to the user.
     */
    private File inputFile;

    public HeadHttpResponse(int statusCode, File inputFile) {
        super(statusCode, inputFile);
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

            writer.flush();
        } catch (IOException e) {
            Logger.error(TAG, e.getMessage());
        }
    }
}

