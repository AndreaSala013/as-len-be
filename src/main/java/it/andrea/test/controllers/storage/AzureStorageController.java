package it.andrea.test.controllers.storage;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import it.andrea.test.model.InputWav;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.*;
import java.util.Base64;

@Slf4j
@RestController()
@RequestMapping("/storage")
@Profile("local")
public class AzureStorageController {

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping(value = "")
    public ResponseEntity<?> beResp() throws IOException {
        String connectStr = "DefaultEndpointsProtocol=https;AccountName=lenovostoragetest;AccountKey=qhm9Bv40j8isToHtaDnOXQaV0d/SUu4C94aYcl2BXEd2jnvZpdcAqY2Qw5KfVYhkrXatOXHBkOMD+AStmSJvog==;EndpointSuffix=core.windows.net";
        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder().connectionString(connectStr).buildClient();
        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient("files");
        BlobClient blobClient = containerClient.getBlobClient("test.txt");
        InputStream is=new FileInputStream("C:\\Users\\asala1\\OneDrive - BUSINESS INTEGRATION PARTNERS SPA\\Desktop\\test.txt");
        blobClient.upload(is);

        return ResponseEntity.ok(true);
    }

}
