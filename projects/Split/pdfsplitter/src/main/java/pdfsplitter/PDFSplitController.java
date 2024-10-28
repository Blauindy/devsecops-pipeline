package pdfsplitter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class PDFSplitController {
    private final PDFSplitService pdfSplitService;

    @Autowired
    public PDFSplitController(PDFSplitService service) {
        this.pdfSplitService = service;
    }

    @PostMapping(path = "/api/pdfsplitter", consumes = "application/json", produces = "application/json")
    public @ResponseBody() ResponseEntity<PdfSplitMergeDto[]> fileUpload(@RequestBody SplitPdfDto splitPdfDto) throws ResponseStatusException {
        try {
            pdfSplitService.setPDF(splitPdfDto.getPdfSplitMergeDto().getData());
            pdfSplitService.setPagesToSplitAt(splitPdfDto.getSplitPages());

        } catch (ResponseStatusException rse) {
            System.err.println(rse.getMessage());
            throw rse;
        }
        pdfSplitService.split();
        return ResponseEntity.ok(pdfSplitService.getSplittedFiles());
    }
}
