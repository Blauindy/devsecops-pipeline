package pdfsplitter;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SplitPdfDto {
    @JsonProperty("pdf")
    private PdfSplitMergeDto pdfSplitMergeDto;
    @JsonProperty("splitPages")
    private String splitPages;

    public PdfSplitMergeDto getPdfSplitMergeDto() {
        return pdfSplitMergeDto;
    }

    public void setPdfEntity(PdfSplitMergeDto pdfSplitMergeDto) {
        this.pdfSplitMergeDto = pdfSplitMergeDto;
    }

    public String getSplitPages() {
        return splitPages;
    }

    public void setSplitPages(String splitPages) {
        this.splitPages = splitPages;
    }
}
