package pdfmerger;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class SelectedPdfDto {
    @JsonProperty("pdfs")
    private List<PdfSplitMergeDto> pdfs;

    public List<PdfSplitMergeDto> getPdfs() {
        return pdfs;
    }

    public void setPdfs(List<PdfSplitMergeDto> pdfs) {
        this.pdfs = pdfs;
    }
}
