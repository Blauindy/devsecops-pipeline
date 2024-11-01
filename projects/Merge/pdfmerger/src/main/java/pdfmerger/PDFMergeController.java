package pdfmerger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
public class PDFMergeController {
    private final PDFMergeService pdfMergeService;

    @Autowired
    public PDFMergeController(PDFMergeService service) {
        this.pdfMergeService = service;
    }

    @PostMapping(value = "/api/pdfmerger", consumes = "application/json", produces = "application/json")
    public @ResponseBody() ResponseEntity<PdfSplitMergeDto> fileUpload(@RequestBody SelectedPdfDto selectedPdfDto) throws ResponseStatusException {
        try {
            List<PdfSplitMergeDto> pdf2merge = selectedPdfDto.getPdfs();
            for (int i = 0; i < pdf2merge.size(); i++) {
                pdfMergeService.addPDF(pdf2merge.get(i).getData(), i);
                pdfMergeService.addTags(pdf2merge.get(i).getTags());
            }
        } catch (ResponseStatusException rse) {
            System.err.println(rse.getMessage());
            throw rse;
        }
        pdfMergeService.mergeFiles();
        return ResponseEntity.ok(pdfMergeService.getMergedFiles());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .headers()
            .contentSecurityPolicy("default-src 'self'; script-src 'self' https://trusted.cdn.com;");
    }
}
