package pdfmerger;

import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class PDFMergeService {
    private final Path rootLocation = Paths.get("./tmp");
    private final Path filesToMergeLocation = Paths.get(rootLocation + "/filestomerge");
    private final Path mergedLocation = Paths.get(rootLocation + "/merged");
    private final PDFMergerUtility mergerUtility;
    private final List<String> mergedTags;

    @Autowired
    public PDFMergeService() {
        mergerUtility = new PDFMergerUtility();
        mergedTags = new ArrayList<>();
    }

    public void addPDF(byte[] bytes, int id) {
        try {
            Files.createDirectories(filesToMergeLocation);
            String location = filesToMergeLocation + "/" + id + "filetomerge.pdf";
            FileOutputStream fos = new FileOutputStream(location);
            fos.write(bytes);
            fos.close();
            mergerUtility.addSource(location);
        } catch (IOException e) {
            logAndThrowException(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong trying to add the pdf!", e);
        }
    }

    public void addTags(String pdfTags) {
        String[] tags = pdfTags.split(";");
        for (String tag : tags) {
            if (!this.mergedTags.contains(tag)) {
                this.mergedTags.add(tag);
            }
        }
    }

    public void mergeFiles() {
        try {
            Files.createDirectories(mergedLocation);
            mergerUtility.setDestinationFileName(mergedLocation + "/" + "mergedPDF.pdf");
            mergerUtility.mergeDocuments(null);
        } catch (IOException e) {
            logAndThrowException(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong trying to merge!", e);
        } finally {
            try {
                cleanDirectories(filesToMergeLocation);
            } catch (IOException e) {
                System.err.println("Unable to delete all files: " + e);
            }
        }
    }

    private String getMergedTags() {
        StringBuilder returnValue = new StringBuilder();
        for (String tag : this.mergedTags) {
            returnValue.append(tag).append(";");
        }
        this.mergedTags.clear();
        return returnValue.substring(0, returnValue.length() - 1);
    }

    public PdfSplitMergeDto getMergedFiles() {
        PdfSplitMergeDto pdfSplitMergeDTO = new PdfSplitMergeDto();
        Path mergedFilePath = Paths.get(mergedLocation + "/" + "mergedPDF.pdf");
        try {
            byte[] pdfInBytes = Files.readAllBytes(mergedFilePath);
            pdfSplitMergeDTO.setData(pdfInBytes);
            pdfSplitMergeDTO.setTags(getMergedTags());
            pdfSplitMergeDTO.setTitle("mergedFile");
            return pdfSplitMergeDTO;
        } catch (IOException e) {
            logAndThrowException(HttpStatus.INTERNAL_SERVER_ERROR, "Error serving merged PDF, sorry !", e);
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
}
