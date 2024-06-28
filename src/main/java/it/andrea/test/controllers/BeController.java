package it.andrea.test.controllers;

import com.microsoft.cognitiveservices.speech.SpeechConfig;
import com.microsoft.cognitiveservices.speech.SpeechRecognitionResult;
import com.microsoft.cognitiveservices.speech.SpeechRecognizer;
import com.microsoft.cognitiveservices.speech.audio.AudioConfig;
import it.andrea.test.model.InputWav;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

@Slf4j
@RestController()
@RequestMapping("/be")
public class BeController {

    @Value("${speech.serv.url}")
    private String speechServUrl;
    @Value("${speech.serv.location}")
    private String location;
    @Value("${speech.serv.key}")
    private String speechSubscriptionKey;

    @GetMapping(value = "/health")
    public ResponseEntity<?> beResp() {
        log.info("BE is responding");
        return ResponseEntity.ok("BE is up and running");
    }

    @PostMapping(value = "/speechToText")
    public ResponseEntity<?> speechToText(@RequestBody InputWav inputWav) throws Exception {
        log.info("speechToText: {}",inputWav.getByteArrStr());
        String outputFile = "wav-file-"+ UUID.randomUUID()+".wav";
        File outFile = null;
        try{

            InputStream inputStream = new ByteArrayInputStream(Base64.getDecoder().decode(inputWav.getByteArrStr()));
            //write wav file to file system
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(inputStream);
            outFile = new File(outputFile);
            AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE , outFile);
            log.info("file saved {}" + outputFile);

            AudioConfig audioConfig = AudioConfig.fromWavFileInput(outputFile);
            log.info("url: {}",speechServUrl);
            URI uri = new URI(speechServUrl);
            SpeechConfig speechConfig = SpeechConfig.fromSubscription(speechSubscriptionKey, location);
            speechConfig.setSpeechRecognitionLanguage("it-IT");
            SpeechRecognizer recognizer = new SpeechRecognizer(speechConfig, audioConfig);

            log.info("Recognizing...");
            SpeechRecognitionResult result = recognizer.recognizeOnceAsync().get();
            log.info("Recognized text: " + result.getText());
            recognizer.close();

            return ResponseEntity.ok(result.getText());
        }catch (Exception ex){
            log.error(ex.getMessage());
            throw ex;
        }finally {
            if(outFile != null){
                outFile.delete();
            }
        }
    }
}
