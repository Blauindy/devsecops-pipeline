package pdfmerger;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PdfSplitMergeDto {
    @JsonProperty("id")
    private Long id;
    @JsonProperty("title")
    private String title;
    @JsonProperty("tags")
    private String tags;
    @JsonProperty("data")
    private byte[] data;

    public PdfSplitMergeDto(String title, String tags, byte[] data) {
        this.title = title;
        this.tags = tags;
        this.data = data;
    }

    public PdfSplitMergeDto() {

    }


    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getTags() {
        return tags;
    }

    public byte[] getData() {
        return data;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
