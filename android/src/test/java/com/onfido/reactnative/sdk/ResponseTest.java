package com.onfido.reactnative.sdk;

import com.onfido.reactnative.sdk.Response.ProofOfAddress;
import com.onfido.reactnative.sdk.Response.ProofOfAddress.ProofOfAddressSide;

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
        String nfcMediaUUID = "docNfcMediaId123";
        ProofOfAddress poa = new ProofOfAddress("type", new ProofOfAddressSide("id1", "type1"), new ProofOfAddressSide("id2", "type2"));
        Response testResponse = new Response(frontId, backId, faceId, faceVariant, nfcMediaUUID, poa);
        assertEquals(frontId, testResponse.document.front.id);
        assertEquals(backId, testResponse.document.back.id);
        assertEquals(faceId, testResponse.face.id);
        assertEquals(faceVariant, testResponse.face.variant);
    }

    @Test
    public void shouldCreateResponseDocFrontOnly() throws Exception {
        String frontId = "frontId1";
        ProofOfAddress poa = new ProofOfAddress("type", new ProofOfAddressSide("id1", "type1"), new ProofOfAddressSide("id2", "type2"));
        Response testResponse = new Response(frontId, null, null, null, null, poa);
        assertEquals(frontId, testResponse.document.front.id);
        assertNull(testResponse.document.back);
        assertNull(testResponse.face);
        assertEquals(poa, testResponse.proofOfAddress);
    }

    @Test
    public void shouldCreateResponseFaceOnly() throws Exception {
        String faceId = "faceId3";
        String faceVariant = "faceVariant4";
        ProofOfAddress poa = new ProofOfAddress("type", new ProofOfAddressSide("id", "type"), null);
        Response testResponse = new Response(null, null, faceId, faceVariant, null, poa);
        assertNull(testResponse.document);
        assertEquals(faceId, testResponse.face.id);
        assertEquals(faceVariant, testResponse.face.variant);
        assertEquals(poa, testResponse.proofOfAddress);
    }

}