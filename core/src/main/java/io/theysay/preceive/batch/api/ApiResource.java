package io.theysay.preceive.batch.api;

public enum ApiResource {
    TOPICS("/v1/resources/topics/keywords", "Topic Keywords", "id", "classLabel", "text", "weight"),
    NAMED_ENTITY_ASSERTIONS("/v1/resources/namedentity/assertions", "Named Entity Assertions", "id", "classLabel", "text");


    public final String path;
    public final String label;
    public final String fields[];

    ApiResource(String path, String label, String... fields) {
        this.path = path;
        this.label = label;
        this.fields = fields;
    }

    public String tsvHeader() {
        StringBuilder b = new StringBuilder();
        for (int i = 0 ; i < fields.length ; i++) {
            if (i != 0) b.append("\t");
            b.append(fields[i]);
        }
        return b.toString();
    }
}
