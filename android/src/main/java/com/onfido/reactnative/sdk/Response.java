package com.onfido.reactnative.sdk;

import javax.annotation.Nullable;

/**
 * The response object represents the results of a successful execution of the Onfido SDK.
 */
class Response {
    public class Identifiable {
        public Identifiable(String id) {
            this.id = id;
        }
        public String id = "default";
    }
    public class Document {
        public Identifiable front;
        public Identifiable back;
        public Identifiable nfcMediaId;
        public Identifiable typeSelected;
        @Nullable public Identifiable countrySelected;

    }
    public class Face extends Response.Identifiable {
        public Face(String id, String variant) {
            super(id);
            this.variant = variant;
        }
        public String variant;
    }
    public static class ProofOfAddress {
        public String type;
        public ProofOfAddressSide front;
        @Nullable public ProofOfAddressSide back;

        public ProofOfAddress(String type, ProofOfAddressSide front, @Nullable ProofOfAddressSide back) {
            this.type = type;
            this.front = front;
            this.back = back;
        }
        public static class ProofOfAddressSide {
            public String id;
            @Nullable public String type;

            public ProofOfAddressSide(String id, @Nullable String type) {
                this.id = id;
                this.type = type;
            }
        }
    }

    public Document document;
    public Face face;
    public ProofOfAddress proofOfAddress;

    public Response(String frontId, String backId, String faceId, String faceVariant, String nfcMediaUUID, ProofOfAddress proofOfAddress) {
        initDocument(frontId, backId, nfcMediaUUID);
        initFace(faceId, faceVariant);
        this.proofOfAddress = proofOfAddress;
    }

    private void initDocument(String frontId, String backId, String nfcMediaUUID) {
        if (frontId != null || backId != null || nfcMediaUUID != null) {
            document = new Document();
            if (frontId != null) {
                document.front = new Identifiable(frontId);
            }
            if (backId != null) {
                document.back = new Identifiable(backId);
            }
            if (nfcMediaUUID != null) {
                document.nfcMediaId = new Identifiable(nfcMediaUUID);
            }
        }
    }

    private void initFace(String faceId, String faceVariant) {
        if (faceId != null || faceVariant != null) {
            face = new Face(faceId, faceVariant);
        }
    }
}
