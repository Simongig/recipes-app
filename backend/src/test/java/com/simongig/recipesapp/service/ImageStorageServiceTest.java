package com.simongig.recipesapp.service;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.server.ResponseStatusException;

import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

@Slf4j 
@ExtendWith(MockitoExtension.class)
class ImageStorageServiceTest {

    private static final String BUCKET = "kochbuch-dev";
    // Only ever used as a string prefix for the returned URLs — the service never calls this host,
    // so using the real one here makes the test no more "real", it just has to match the assertions.
    private static final String PUBLIC_URL = "https://pub-8f9716b0ed6d42b28f32b154cc143fb9.r2.dev";

    @Mock
    private S3Client s3Client;

    private ImageStorageService service;

    @BeforeEach
    void setUp() {
        // trailing slash on purpose: the service must not produce "//" in the returned URLs
        service = new ImageStorageService(s3Client, BUCKET, PUBLIC_URL + "/");
    }

    private static MockMultipartFile image(String contentType) {
        return new MockMultipartFile("images", "photo", contentType, new byte[] {1, 2, 3});
    }

    @Test
    void uploadsToTheBucketAndReturnsPublicUrls() throws IOException {
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(PutObjectResponse.builder().build());

        List<String> urls = service.upload(List.of(image("image/png"), image("image/jpeg")));

        log.info("Returned URLs: {}", urls);

        ArgumentCaptor<PutObjectRequest> requests = ArgumentCaptor.forClass(PutObjectRequest.class);
        verify(s3Client, times(2)).putObject(requests.capture(), any(RequestBody.class));

        assertEquals(2, urls.size());
        assertTrue(urls.get(0).matches(Pattern.quote(PUBLIC_URL) + "/recipes/[0-9a-f-]{36}\\.png"), urls.get(0));
        assertTrue(urls.get(1).matches(Pattern.quote(PUBLIC_URL) + "/recipes/[0-9a-f-]{36}\\.jpg"), urls.get(1));
        assertEquals(BUCKET, requests.getAllValues().get(0).bucket());
        assertEquals("image/png", requests.getAllValues().get(0).contentType());
        assertEquals("image/jpeg", requests.getAllValues().get(1).contentType());
    }

    @Test
    void skipsEmptyParts() throws IOException {
        MockMultipartFile empty = new MockMultipartFile("images", "", "application/octet-stream", new byte[0]);

        assertTrue(service.upload(List.of(empty)).isEmpty());

        verifyNoInteractions(s3Client);
    }

    @Test
    void rejectsUnsupportedTypesBeforeUploadingAnything() {
        ResponseStatusException e = assertThrows(ResponseStatusException.class,
                () -> service.upload(List.of(image("image/png"), image("text/html"))));

        assertEquals(415, e.getStatusCode().value());
        verifyNoInteractions(s3Client); // the valid first image must not have been uploaded either
    }

    @Test
    void removesAlreadyUploadedImagesWhenALaterUploadFails() {
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(PutObjectResponse.builder().build())
                .thenThrow(new RuntimeException("R2 unavailable"));

        assertThrows(RuntimeException.class,
                () -> service.upload(List.of(image("image/png"), image("image/png"))));

        ArgumentCaptor<PutObjectRequest> puts = ArgumentCaptor.forClass(PutObjectRequest.class);
        verify(s3Client, times(2)).putObject(puts.capture(), any(RequestBody.class));
        ArgumentCaptor<DeleteObjectRequest> deletes = ArgumentCaptor.forClass(DeleteObjectRequest.class);
        verify(s3Client, times(1)).deleteObject(deletes.capture());

        // the object that was stored successfully (first put) is the one that gets cleaned up
        assertEquals(puts.getAllValues().get(0).key(), deletes.getValue().key());
        assertEquals(BUCKET, deletes.getValue().bucket());
    }
}
