package com.simongig.recipesapp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.sun.net.httpserver.HttpServer;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

/**
 * Pins the R2-specific setup of the S3Client bean by looking at the request it really sends
 * (to a local fake server): path-style addressing, region "auto", and no CRC32 checksum
 * headers/trailers, which Cloudflare R2 rejects but the SDK adds by default since 2.30.
 */
class S3ClientConfigTest {

    @Test
    void sendsPathStyleRequestsWithoutChecksumHeadersToTheConfiguredEndpoint() throws Exception {
        AtomicReference<String> requestLine = new AtomicReference<>();
        Map<String, String> headers = new TreeMap<>();

        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/", exchange -> {
            requestLine.set(exchange.getRequestMethod() + " " + exchange.getRequestURI().getPath());
            exchange.getRequestHeaders().forEach((name, values) -> headers.put(name.toLowerCase(), String.join(",", values)));
            exchange.getRequestBody().readAllBytes();
            exchange.sendResponseHeaders(200, -1);
            exchange.close();
        });
        server.start();

        try {
            SpringConfiguration config = new SpringConfiguration();
            ReflectionTestUtils.setField(config, "r2Endpoint", "http://127.0.0.1:" + server.getAddress().getPort());
            ReflectionTestUtils.setField(config, "r2AccessKey", "test-access-key");
            ReflectionTestUtils.setField(config, "r2SecretKey", "test-secret-key");

            try (S3Client client = config.s3Client()) {
                client.putObject(
                        PutObjectRequest.builder().bucket("recipe-images").key("recipes/a.png").contentType("image/png").build(),
                        RequestBody.fromBytes(new byte[] {1, 2, 3}));
            }
        } finally {
            server.stop(0);
        }

        assertEquals("PUT /recipe-images/recipes/a.png", requestLine.get());
        assertTrue(headers.get("authorization").contains("/auto/s3/aws4_request"), headers.get("authorization"));
        // (Not asserting on content-encoding: over plain http the SDK always signs the payload as
        // aws-chunked, unrelated to checksums. R2 itself is https-only.)
        headers.keySet().forEach(name -> {
            assertTrue(!name.startsWith("x-amz-checksum") && !name.equals("x-amz-sdk-checksum-algorithm")
                    && !name.equals("x-amz-trailer"), "unexpected checksum header: " + name);
        });
    }
}
