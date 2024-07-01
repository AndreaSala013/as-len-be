package it.andrea.test.controllers;

import it.andrea.test.model.InputWav;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
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
        sendWavBe(fileName);
        return ResponseEntity.ok("true");
    }

    private String sendWavBe(String fileName) {
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
            String byteStr = Base64.getEncoder().encodeToString(bytes);

            InputWav inputWav = new InputWav(byteStr);

            HttpHeaders headers = new HttpHeaders();
            HttpEntity<?> entity = new HttpEntity<>(inputWav, headers);
            UriComponents builder = UriComponentsBuilder.fromUriString(url).buildAndExpand();

            ResponseEntity<String> msResp = restTemplate
                    .exchange(builder.toUriString(),
                            HttpMethod.POST,
                            entity, String.class);
            return msResp.getBody();
        } catch (Exception ex) {
            log.error(ex.toString());
        }
        return null;
    }

}
