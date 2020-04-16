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
    }
    public class Face extends Identifiable {
        public Face(String id, String variant) {
            super(id);
            this.variant = variant;
        }
        public String variant; 
    }

    public Document document;
    public Face face;

    public Response(String frontId, String backId, String faceId, String faceVariant) {
        initDocument(frontId, backId);
        initFace(faceId, faceVariant);
    }

    private void initDocument(String frontId, String backId) {
        if (frontId != null || backId != null) {
            document = new Document();
            if (frontId != null) {
                document.front = new Identifiable(frontId);
            }
            if (backId != null) {
                document.back = new Identifiable(backId);
            }
        }
    }

    private void initFace(String faceId, String faceVariant) {
        if (faceId != null || faceVariant != null) {
            face = new Face(faceId, faceVariant);
        }
    }
}