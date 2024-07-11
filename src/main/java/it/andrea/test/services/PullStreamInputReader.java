package it.andrea.test.services;

import com.microsoft.cognitiveservices.speech.audio.PullAudioInputStreamCallback;

import java.io.*;

public class PullStreamInputReader extends PullAudioInputStreamCallback {
    private final InputStream input;

    public PullStreamInputReader(byte[] bytes) throws FileNotFoundException
    {
        // In this example the input stream is a file. Modify the code to use
        // a non-file source (e.g. audio API that returns data) as necessary.
        this.input = new ByteArrayInputStream(bytes);
    }

    // This method is called to synchronously get data from the input stream.
    // It returns the number of bytes copied into the data buffer.
    // If there is no data, the method must wait until data becomes available
    // or return 0 to indicate the end of stream.
    @Override
    public int read(byte[] buffer)
    {
        long bytesRead = 0;

        try {
            // Copy audio data from the input stream into a data buffer for the
            // Speech SDK to consume.
            // Data must NOT include any headers, only audio samples.
            bytesRead = this.input.read(buffer, 0, buffer.length);
        }
        catch (Exception e)
        {
            System.out.println("PullStreamInputReader: " + e.toString());
        }
        return (int)Math.max(0, bytesRead);
    }

    // This method is called for cleanup of resources.
    @Override
    public void close()
    {
        try {
            this.input.close();
        } catch (IOException e) {
            // ignored
        }
    }
}
