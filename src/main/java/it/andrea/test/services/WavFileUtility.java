package it.andrea.test.services;

import lombok.extern.slf4j.Slf4j;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
public class WavFileUtility {

    public static void CreateWavFile(byte[] audioBytes){

        try {
            String fileName = "speech_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH_mm_ss")) + ".wav";
            InputStream inputStream = new ByteArrayInputStream(audioBytes);
            //write wav file to file system
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(inputStream);
            AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE , new File(fileName));
            log.info("file {} creato", fileName);
        }
        catch(Exception e) {
            log.error(e.getMessage());
        }

    }

}
