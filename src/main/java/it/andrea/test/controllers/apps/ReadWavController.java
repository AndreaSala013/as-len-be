package it.andrea.test.controllers.apps;

import it.andrea.test.model.InputWav;
import it.andrea.test.services.WavFileUtility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.*;
import java.util.Base64;

@Slf4j
@RestController()
@RequestMapping("/reader")
@Profile("local")
public class ReadWavController {
    @Autowired
    private RestTemplate restTemplate;


    @GetMapping(value = "")
    public ResponseEntity<?> beResp(@RequestParam String fileName) {
        return ResponseEntity.ok(sendWavBe(fileName));
    }

    @GetMapping(value = "getSpeech")
    public ResponseEntity<?> getSpeech(@RequestParam String text) {
        InputWav resp = null;
        try {
            String url = "https://as-lenovo-be.azurewebsites.net/be/textToSpeech?inputText=" + text;
            //String url = "http://localhost:9090/be/speechToText";
            log.info("Calling BE with url {}",url);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponents builder = UriComponentsBuilder.fromUriString(url).buildAndExpand();

            ResponseEntity<InputWav> msResp = restTemplate
                    .exchange(builder.toUriString(),
                            HttpMethod.GET,
                            entity, InputWav.class);
            if(msResp.getStatusCode().equals(HttpStatus.OK)){
                resp = msResp.getBody();
                WavFileUtility.CreateWavFile(resp.getByteArr());
            }

            log.info("RESP: {}", resp);

        } catch (Exception ex) {
            log.error(ex.toString());
        }
        return ResponseEntity.ok(true);
    }


    private String sendWavBe(String fileName) {
        String resp = null;
        try {
            //String url = "https://as-lenovo-be.azurewebsites.net/be/speechToText";
            String url = "http://localhost:9090/be/speechToText";
            log.info("Calling BE with url {}",url);

            InputStream is=new FileInputStream("C:\\Users\\asala1\\OneDrive - BUSINESS INTEGRATION PARTNERS SPA\\Documents\\Audacity\\"+fileName);
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();

            int nRead;
            byte[] data = new byte[16384];

            while ((nRead = is.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            byte[] bytes = buffer.toByteArray();

            InputWav inputWav = new InputWav("",bytes);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<?> entity = new HttpEntity<>(inputWav, headers);
            UriComponents builder = UriComponentsBuilder.fromUriString(url).buildAndExpand();

            ResponseEntity<String> msResp = restTemplate
                    .exchange(builder.toUriString(),
                            HttpMethod.POST,
                            entity, String.class);
            resp = msResp.getBody();
            log.info("RESP: {}", resp);
            return resp;
        } catch (Exception ex) {
            log.error(ex.toString());
            resp = ex.toString();
        }
        return resp;
    }

}
