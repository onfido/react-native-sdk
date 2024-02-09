package com.onfido.reactnative.sdk;

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
    }
    public class Face extends Response.Identifiable {
        public Face(String id, String variant) {
            super(id);
            this.variant = variant;
        }
        public String variant; 
    }

    public Document document;
    public Face face;

    public Response(String frontId, String backId, String faceId, String faceVariant, String nfcMediaUUID) {
        initDocument(frontId, backId, nfcMediaUUID);
        initFace(faceId, faceVariant);
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
