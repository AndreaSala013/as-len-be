package it.andrea.test.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Locale;

@Slf4j
@RestController()
@RequestMapping("/webApp")
@Profile("local")
public class TestController {

    @Autowired
    private RestTemplate restTemplate;
    @Value("${be.url}")
    private String beUrl;

    @GetMapping(value = "/fe")
    public ResponseEntity<?> feResp() {
        String feStr = "FE is responding";
        log.info(feStr);
        String beResp = callBE();
        return ResponseEntity.ok(feStr + beResp);
    }

    @GetMapping(value = "/be")
    public ResponseEntity<?> beResp() {
        log.info("BE is responding");
        return ResponseEntity.ok("BE is responding");
    }

    private String callBE() {
        try {
            log.info("Calling BE with url {}",beUrl);
            HttpHeaders headers = new HttpHeaders();
            HttpEntity<?> entity = new HttpEntity<>(headers);
            UriComponents builder = UriComponentsBuilder.fromUriString(beUrl).buildAndExpand();

            ResponseEntity<String> msResp = restTemplate.exchange(builder.toUriString(), HttpMethod.GET,
                    entity, String.class);
            return msResp.getBody();
        } catch (Exception ex) {
            log.error(ex.toString());
        }
        return null;
    }
}
