import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pdfsplitter.PDFSplitService;
import pdfsplitter.PdfSplitMergeDto;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;

public class TestApplication {
    private final Path documentsLocation = Paths.get("./tmp");
    PDFSplitService pdfSplitService;

    @BeforeEach
    public void init() throws IOException {
        pdfSplitService = new PDFSplitService();
        PDDocument pdfDoc = new PDDocument();
        for (int i = 0; i < 10; i++) {
            pdfDoc.addPage(new PDPage());
        }
        Files.createDirectories(documentsLocation);
        pdfDoc.save(documentsLocation + "/fileToSplit.pdf");
        pdfDoc.close();
    }

    @Test
    public void testSplitter() {
        try {
            pdfSplitService.setPDF(Files.readAllBytes(Paths.get(documentsLocation + "/fileToSplit.pdf")));
            pdfSplitService.setPagesToSplitAt("1,4,6");
            pdfSplitService.split();
            List<Path> allDocumentLocations = pdfSplitService.getAllDocumentLocations();
            Assertions.assertEquals(4, Loader.loadPDF(new File(allDocumentLocations.get(0).toString())).getNumberOfPages());
            Assertions.assertEquals(2, Loader.loadPDF(new File(allDocumentLocations.get(1).toString())).getNumberOfPages());
            Assertions.assertEquals(3, Loader.loadPDF(new File(allDocumentLocations.get(2).toString())).getNumberOfPages());
            Assertions.assertEquals(1, Loader.loadPDF(new File(allDocumentLocations.get(3).toString())).getNumberOfPages());
            PdfSplitMergeDto[] pdfSplitMergeDto = pdfSplitService.getSplittedFiles();
            Assertions.assertEquals(4, pdfSplitMergeDto.length);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @AfterEach
    public void cleanDirectories() throws IOException {
        if (Files.exists(documentsLocation)) {
            Files
                    .walk(Paths.get("./tmp"))
                    .sorted(Comparator.reverseOrder())
                    .forEach(path -> {
                        try {
                            System.out.println("Deleting: " + path);
                            Files.delete(path);
                        } catch (IOException ignored) {
                        }
                    });
        }
    }
}
