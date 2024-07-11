package it.andrea.test.controllers.apps;

import com.microsoft.cognitiveservices.speech.*;
import com.microsoft.cognitiveservices.speech.audio.*;
import it.andrea.test.model.InputWav;
import it.andrea.test.services.PullStreamInputReader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;

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
        log.info("speechToText");
        try{
            AudioStreamFormat audioFormat = AudioStreamFormat.getWaveFormatPCM(44100, (short)16, (short)2);
            PullAudioInputStream pullStream = PullAudioInputStream.createPullStream(new PullStreamInputReader(inputWav.getByteArr()),audioFormat);
            AudioConfig audioConfig = AudioConfig.fromStreamInput(pullStream);

            log.info(speechSubscriptionKey);
            log.info(location);
            SpeechConfig speechConfig = SpeechConfig.fromSubscription(speechSubscriptionKey, location);
            speechConfig.setSpeechRecognitionLanguage("it-IT");
            SpeechRecognizer recognizer = new SpeechRecognizer(speechConfig, audioConfig);

            log.info("Recognizing...");
            SpeechRecognitionResult result = recognizer.recognizeOnceAsync().get();
            log.info("TEXT: {}",result.getText());
            //recognizeSpeechAsync(recognizer);
            recognizer.close();

            return ResponseEntity.ok(result.getText());
        }catch (Exception ex){
            log.error(ex.getMessage());
            throw ex;
        }
    }

    private static void recognizeSpeechAsync(SpeechRecognizer recognizer) throws InterruptedException, ExecutionException
    {
        Semaphore recognitionEnd = new Semaphore(0);

        // Subscribes to events.
        recognizer.recognizing.addEventListener((s, e) ->
        {
            // Intermediate result (hypothesis).
            if (e.getResult().getReason() == ResultReason.RecognizingSpeech)
            {
                log.info("Recognizing:" + e.getResult().getText());
            }
            else if (e.getResult().getReason() == ResultReason.RecognizingKeyword)
            {
                // ignored
            }
        });

        recognizer.recognized.addEventListener((s, e) ->
        {
            if (e.getResult().getReason() == ResultReason.RecognizedKeyword)
            {
                // Keyword detected, speech recognition will start.
                log.info("KEYWORD: Text=" + e.getResult().getText());
            }
            else if (e.getResult().getReason() == ResultReason.RecognizedSpeech)
            {
                // Final result. May differ from the last intermediate result.
                log.info("RECOGNIZED: Text=" + e.getResult().getText());

                // See where the result came from, cloud (online) or embedded (offline)
                // speech recognition.
                // This can change during a session where HybridSpeechConfig is used.
                /*
                log.info("Recognition backend: " + e.getResult().getProperties().getProperty(PropertyId.SpeechServiceResponse_RecognitionBackend));
                */

                // Recognition results in JSON format.
                //
                // Offset and duration values are in ticks, where a single tick
                // represents 100 nanoseconds or one ten-millionth of a second.
                //
                // To get word level detail, set the output format to detailed.
                // See embeddedRecognitionFromWavFile() in this source file for
                // a configuration example.
                //
                // If an embedded speech recognition model does not support word
                // timing, the word offset and duration values are always 0, and the
                // phrase offset and duration only indicate a time window inside of
                // which the phrase appeared, not the accurate start and end of speech.
                /*
                String jsonResult = e.getResult().getProperties().getProperty(PropertyId.SpeechServiceResponse_JsonResult);
                log.info("JSON result: " + jsonResult);
                */
                // Word level detail (if enabled in speech config).
                /*
                // Convert the JSON string to a JSON object.
                JSONObject json = new JSONObject(jsonResult);

                // Extract word level detail from the JSON.
                JSONArray nbestArray = json.getJSONArray("NBest");
                if (nbestArray != null && nbestArray.length() > 0)
                {
                    JSONObject bestResult = nbestArray.getJSONObject(0);
                    JSONArray wordsArray = bestResult.getJSONArray("Words");

                    for (int i = 0; i < wordsArray.length(); i++)
                    {
                        JSONObject word = wordsArray.getJSONObject(i);
                        log.info(
                            "Word: \"" + word.getString("Word") + "\" | " +
                            "Offset: " + word.getLong("Offset") / 10000 + "ms | " +
                            "Duration: " + word.getLong("Duration") / 10000 + "ms");
                    }
                }
                */
            }
            else if (e.getResult().getReason() == ResultReason.NoMatch)
            {
                // NoMatch occurs when no speech was recognized.
                NoMatchReason reason = NoMatchDetails.fromResult(e.getResult()).getReason();
                log.info("NO MATCH: Reason=" + reason);
            }
        });

        recognizer.canceled.addEventListener((s, e) ->
        {
            log.info("CANCELED: Reason=" + e.getReason());

            if (e.getReason() == CancellationReason.Error)
            {
                // NOTE: In case of an error, do not use the same recognizer for recognition anymore.
                System.err.println("CANCELED: ErrorCode=" + e.getErrorCode());
                System.err.println("CANCELED: ErrorDetails=\"" + e.getErrorDetails() + "\"");
            }
        });

        recognizer.sessionStarted.addEventListener((s, e) ->
        {
            log.info("Session started.");
        });

        recognizer.sessionStopped.addEventListener((s, e) ->
        {
            log.info("Session stopped.");
            recognitionEnd.release();
        });

        recognizer.startContinuousRecognitionAsync().get();
    }
}
