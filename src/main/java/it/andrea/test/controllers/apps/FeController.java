package it.andrea.test.controllers.apps;

import it.andrea.test.model.InputWav;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@RestController()
@RequestMapping("/fe")
public class FeController {

    @Autowired
    private RestTemplate restTemplate;
    @Value("${be.url}")
    private String beUrl;

    @GetMapping(value = "/health")
    public ResponseEntity<?> beResp() {
        log.info("health");
        return ResponseEntity.ok("FE is up and running");
    }

    @GetMapping(value = "/callBe")
    public ResponseEntity<?> callBe() {
        log.info("call-be");
        String respBe = healthBE();
        String res = "BE resp: " + respBe;
        log.info(res);
        return ResponseEntity.ok(res);
    }

    @PostMapping(value = "/speechToText")
    public ResponseEntity<?> speechToText(@RequestBody InputWav inputWav) {
        log.info("speechToText");
        String respBe = sendWavBe(inputWav);
        String res = "BE resp: " + respBe;
        log.info(res);
        return ResponseEntity.ok(res);
    }

    private String sendWavBe(InputWav inputWav) {
        try {
            String url = beUrl + "/speechToText";
            log.info("Calling BE with url {}",url);
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

    private String healthBE() {
        try {
            String url = beUrl + "/health";
            log.info("Calling BE with url {}",url);
            HttpHeaders headers = new HttpHeaders();
            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponents builder = UriComponentsBuilder.fromUriString(url).buildAndExpand();
            ResponseEntity<String> msResp = restTemplate.exchange(builder.toUriString(), HttpMethod.GET,
                    entity, String.class);
            return msResp.getBody();
        } catch (Exception ex) {
            log.error(ex.toString());
        }
        return null;
    }

}
