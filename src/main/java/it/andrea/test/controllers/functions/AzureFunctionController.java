package it.andrea.test.controllers.functions;

import it.andrea.test.model.InputWav;
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
@RequestMapping("/functions")
@Profile("local")
public class AzureFunctionController {

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping(value = "")
    public ResponseEntity<?> beResp() throws IOException {
        InputStream is=new FileInputStream("C:\\Users\\asala1\\OneDrive - BUSINESS INTEGRATION PARTNERS SPA\\Desktop\\test.txt");
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        int nRead;
        byte[] data = new byte[16384];

        while ((nRead = is.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        byte[] bytes = buffer.toByteArray();
        String byteStr = Base64.getEncoder().encodeToString(bytes);
        InputWav inputWav = new InputWav("ANDREA_2.txt",byteStr);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<?> entity = new HttpEntity<>(inputWav, headers);
        UriComponents builder = UriComponentsBuilder.fromUriString("https://func-len-test-2.azurewebsites.net/api/HttpExample").buildAndExpand();

        ResponseEntity<String> msResp = restTemplate
                .exchange(builder.toUriString(),
                        HttpMethod.POST,
                        entity, String.class);
        log.info(msResp.getBody());
        return ResponseEntity.ok(msResp.getBody());
    }

}
