package it.andrea.test.controllers.apps;

import com.microsoft.cognitiveservices.speech.SpeechConfig;
import com.microsoft.cognitiveservices.speech.SpeechRecognitionResult;
import com.microsoft.cognitiveservices.speech.SpeechRecognizer;
import com.microsoft.cognitiveservices.speech.audio.AudioConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.*;
import java.net.URI;
import java.util.Base64;

@Slf4j
@RestController()
@RequestMapping("/speech")
@Profile("local")
public class SpeechController {

    private static String speechSubscriptionKey = "e735b37303a84ec4aa7e4f339693f729";
    private static String serviceRegion = "northeurope";

    @GetMapping(value = "")
    public ResponseEntity<?> translateSpeech() {
        log.info("translateSpeech");

        try {
            String audioFile = "C:\\Users\\asala1\\OneDrive - BUSINESS INTEGRATION PARTNERS SPA\\Documents\\Audacity\\test.wav";
            AudioConfig audioConfig = AudioConfig.fromWavFileInput(audioFile);

            URI uri = new URI("https://speech-serv-poc.cognitiveservices.azure.com/");
            SpeechConfig speechConfig = SpeechConfig.fromSubscription(speechSubscriptionKey, serviceRegion);
            speechConfig.setSpeechRecognitionLanguage("it-IT");
            SpeechRecognizer recognizer = new SpeechRecognizer(speechConfig, audioConfig);

            System.out.println("Recognizing...");
            SpeechRecognitionResult result = recognizer.recognizeOnceAsync().get();

            System.out.println("Recognized text: " + result.getText());

            recognizer.close();
        } catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
        }

        return ResponseEntity.ok("BE is responding");
    }

    @GetMapping(value = "/byte")
    public ResponseEntity<?> readByte() throws IOException, UnsupportedAudioFileException {
        String audioFile = "C:\\Users\\asala1\\OneDrive - BUSINESS INTEGRATION PARTNERS SPA\\Documents\\Audacity\\test.wav";
        String audioFileOut = "C:\\Users\\asala1\\OneDrive - BUSINESS INTEGRATION PARTNERS SPA\\Documents\\Audacity\\test2.wav";


        InputStream is=new FileInputStream(audioFile);
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        int nRead;
        byte[] data = new byte[16384];

        while ((nRead = is.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        byte[] bytes = buffer.toByteArray();
        String byteStr = Base64.getEncoder().encodeToString(bytes);
        log.info(byteStr);

        InputStream is2 = new ByteArrayInputStream(Base64.getDecoder().decode(byteStr));

        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(is2);
        File outFile = new File(audioFileOut);
        AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE , outFile);

        try {
            //PushAudioInputStream pushStream = com.microsoft.cognitiveservices.speech.audio.AudioInputStream.createPushStream();
            //pushStream.write(bytes);

            //PullAudioInputStreamCallback callback = new WavStream(is2);
            //AudioConfig audioConfig = AudioConfig.fromStreamInput(callback);

            //AudioConfig audioConfig = AudioConfig.fromStreamInput(pushStream);
            AudioConfig audioConfig = AudioConfig.fromWavFileInput(audioFileOut);


            SpeechConfig speechConfig = SpeechConfig.fromSubscription(speechSubscriptionKey, serviceRegion);
            speechConfig.setSpeechRecognitionLanguage("it-IT");
            SpeechRecognizer recognizer = new SpeechRecognizer(speechConfig, audioConfig);

            System.out.println("Recognizing...");
            SpeechRecognitionResult result = recognizer.recognizeOnceAsync().get();

            System.out.println("Recognized text: " + result.getText());

            recognizer.close();
        } catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
        }

        return ResponseEntity.ok("BE is responding");
    }
}
