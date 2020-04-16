package com.onfido.reactnative.sdk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.Test;

public class ResponseTest {

    @Test
    public void shouldCreateResponse() throws Exception {
        String frontId = "frontId1";
        String backId = "backId2";
        String faceId = "faceId3";
        String faceVariant = "faceVariant4";
        Response testResponse = new Response(frontId, backId, faceId, faceVariant);
        assertEquals(frontId, testResponse.document.front.id);
        assertEquals(backId, testResponse.document.back.id);
        assertEquals(faceId, testResponse.face.id);
        assertEquals(faceVariant, testResponse.face.variant);
    }

    @Test
    public void shouldCreateResponseDocFrontOnly() throws Exception {
        String frontId = "frontId1";
        Response testResponse = new Response(frontId, null, null, null);
        assertEquals(frontId, testResponse.document.front.id);
        assertNull(testResponse.document.back);
        assertNull(testResponse.face);
    }

    @Test
    public void shouldCreateResponseFaceOnly() throws Exception {
        String faceId = "faceId3";
        String faceVariant = "faceVariant4";
        Response testResponse = new Response(null, null, faceId, faceVariant);
        assertNull(testResponse.document);
        assertEquals(faceId, testResponse.face.id);
        assertEquals(faceVariant, testResponse.face.variant);
    }

}