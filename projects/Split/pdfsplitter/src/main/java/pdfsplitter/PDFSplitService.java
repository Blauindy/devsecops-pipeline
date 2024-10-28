package pdfsplitter;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
public class PDFSplitService {
    private final Stack<Integer> pagesToSplitAt;
    private static final Path rootLocation = Paths.get("./tmp");
    private static final Path fileToSplitLocation = Paths.get(rootLocation + "/filetosplit");
    private static final Path splittedFileLocation = Paths.get(rootLocation + "/splittedfiles");
    private static List<Path> allDocumentLocations;

    @Autowired
    public PDFSplitService() {
        pagesToSplitAt = new Stack<>();
        allDocumentLocations = new ArrayList<>();
    }

    public void setPDF(byte[] bytes) {
        try {
            allDocumentLocations.clear();
            Files.createDirectories(fileToSplitLocation);
            String location = fileToSplitLocation + "/filetosplit.pdf";
            FileOutputStream fos = new FileOutputStream(location);
            fos.write(bytes);
            fos.close();
        } catch (IOException e) {
            logAndThrowException(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong trying to set the pdf!", e);
        }
    }

    public void setPagesToSplitAt(String stringPagesToSplitAt) {
        try {
            int[] pageNumbersToSplitAt = convertStringToIntArray(stringPagesToSplitAt);
            pageNumbersToSplitAt = Arrays.stream(pageNumbersToSplitAt).distinct().toArray();
            PDDocument doc = Loader.loadPDF(new File(fileToSplitLocation + "/filetosplit.pdf"));
            int amountOfPages = doc.getNumberOfPages();
            doc.close();
            Arrays.sort(pageNumbersToSplitAt);
            if (pageNumbersToSplitAt[pageNumbersToSplitAt.length - 1] >= amountOfPages || pageNumbersToSplitAt[0] <= 0) {
                logAndThrowException(HttpStatus.BAD_REQUEST, "One of the page number to split at is not in Range of the pdf-file!", null);
                return;
            }
            for (int i : pageNumbersToSplitAt) {
                this.pagesToSplitAt.push(i);
            }
        } catch (IOException e) {
            logAndThrowException(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong reading the pdf!", e);
        }
    }

    private int[] convertStringToIntArray(String numbers) {
        if (numbers == null || numbers.isEmpty()) {
            logAndThrowException(HttpStatus.BAD_REQUEST, "There are no pages to split at transferred", null);
            return null;
        } else {
            try {
                String[] splittedNumbers = numbers.split(",");
                int[] intNumbers = new int[splittedNumbers.length];
                for (int i = 0; i < splittedNumbers.length; i++) {
                    intNumbers[i] = Integer.parseInt(splittedNumbers[i]);
                }
                return intNumbers;
            } catch (NumberFormatException e) {
                logAndThrowException(HttpStatus.BAD_REQUEST, "There are invalid pages to split at transferred", e);
                return null;
            }
        }
    }


    public void split() {
        try {
            Files.createDirectories(splittedFileLocation);
            PDDocument doc = Loader.loadPDF(new File(fileToSplitLocation + "/filetosplit.pdf"));
            int toPage = doc.getNumberOfPages();
            while (!this.pagesToSplitAt.empty()) {
                int fromPage = this.pagesToSplitAt.pop() + 1;
                splitAndSaveDocument(fromPage, toPage, doc);
                toPage = fromPage - 1;
            }
            splitAndSaveDocument(1, toPage, doc);
            doc.close();
        } catch (IOException e) {
            logAndThrowException(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong trying to merge!", e);
        } finally {
            try {
                cleanDirectories(fileToSplitLocation);
            } catch (IOException e) {
                System.err.println("Unable to delete all files: " + e);
            }
        }
    }

    private static void splitAndSaveDocument(int fromPage, int toPage, PDDocument doc) throws IOException {
        Splitter splitter = new Splitter();
        splitter.setStartPage(fromPage);
        splitter.setEndPage(toPage);
        List<PDDocument> splitPages = splitter.split(doc);
        PDDocument splittedDoc = new PDDocument();
        for (PDDocument page : splitPages) {
            splittedDoc.addPage(page.getPage(0));
        }
        String documentPath;
        if (fromPage != toPage) {
            documentPath = splittedFileLocation + "/" + "pages_" + fromPage + "-" + toPage + ".pdf";
        } else {
            documentPath = splittedFileLocation + "/" + "page_" + fromPage + ".pdf";
        }
        splittedDoc.save(new File(documentPath));
        splittedDoc.close();
        allDocumentLocations.add(Paths.get(documentPath));
    }

    public PdfSplitMergeDto[] getSplittedFiles() {
        try {
            PdfSplitMergeDto[] pdfSplitMergeDtos = new PdfSplitMergeDto[allDocumentLocations.size()];
            int tmp = allDocumentLocations.size();
            for (int i = 1; i <= tmp; i++) {
                byte[] bytes = Files.readAllBytes(allDocumentLocations.get(tmp - i));
                String filename = allDocumentLocations.get(tmp - i).getFileName().toString();
                pdfSplitMergeDtos[i - 1] = new PdfSplitMergeDto(filename.split("\\.")[0], "", bytes);
            }
            return pdfSplitMergeDtos;
        } catch (IOException e) {
            logAndThrowException(HttpStatus.INTERNAL_SERVER_ERROR, "Error serving merged PDF, sorry!", e);
            return null;
        } finally {
            try {
                cleanDirectories(rootLocation);
            } catch (IOException e) {
                System.err.println("Unable to delete all files: " + e);
            }
        }

    }

    private void cleanDirectories(Path location) throws IOException {
        Files
                .walk(location)
                .sorted(Comparator.reverseOrder())
                .forEach(path -> {
                    try {
                        System.out.println("Deleting: " + path);
                        Files.delete(path);
                    } catch (IOException ignored) {
                    }
                });
    }

    private void logAndThrowException(HttpStatus status, String msg, Throwable e) {
        System.err.println(msg);
        throw new ResponseStatusException(status, msg, e);
    }

    public List<Path> getAllDocumentLocations() {
        return allDocumentLocations;
    }
}
