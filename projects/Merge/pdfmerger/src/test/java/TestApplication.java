import org.apache.pdfbox.Loader;
import org.junit.jupiter.api.*;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import pdfmerger.PDFMergeService;
import pdfmerger.PdfSplitMergeDto;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

public class TestApplication {
    private PDFMergeService pdfMergeService;
    private final Path documentsLocation = Paths.get("./tmp");
    private String tagsForFirstTestFile;
    private String tagsForSecondTestFile;

    @BeforeEach
    public void init() throws IOException {
        pdfMergeService = new PDFMergeService();
        PDDocument pdfdoc = new PDDocument();
        pdfdoc.addPage(new PDPage());
        Files.createDirectories(documentsLocation);
        pdfdoc.save(documentsLocation + "/firsttestfile.pdf");
        pdfdoc.save(documentsLocation + "/secondtestfile.pdf");
        tagsForFirstTestFile = "DifferentTagFirstFile;SameTag";
        tagsForSecondTestFile = "DifferentTagSecondFile;SameTag";
        pdfdoc.close();
    }

    @Test
    public void testMerger() {
        try {
            pdfMergeService.addPDF(Files.readAllBytes(Paths.get(documentsLocation + "/firsttestfile.pdf")), 1);
            pdfMergeService.addPDF(Files.readAllBytes(Paths.get(documentsLocation + "/secondtestfile.pdf")), 2);
            pdfMergeService.addTags(tagsForFirstTestFile);
            pdfMergeService.addTags(tagsForSecondTestFile);
            pdfMergeService.mergeFiles();
            PDDocument pdfDocument = Loader.loadPDF(new File(documentsLocation + "/merged/mergedPDF.pdf"));
            System.out.println(pdfDocument.getNumberOfPages());
            Assertions.assertEquals(2, pdfDocument.getNumberOfPages());
            pdfDocument.close();
            PdfSplitMergeDto pdfSplitMergeDTO = pdfMergeService.getMergedFiles();
            Assertions.assertEquals("DifferentTagFirstFile;SameTag;DifferentTagSecondFile", pdfSplitMergeDTO.getTags());
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
