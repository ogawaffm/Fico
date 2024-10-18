package com.ogawa.fico;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.ogawa.fico.db.persistence.beanwriter.Creator;
import com.ogawa.fico.scan.FileBean;
import com.ogawa.fico.scan.FileBeanFactory;
import com.ogawa.fico.scan.fileformat.office.OfficeFileDocSummaryInfo;
import com.ogawa.fico.scan.fileformat.office.OfficeFileFormat;
import com.ogawa.fico.scan.fileformat.office.OfficeFileSummaryInfo;
import com.ogawa.fico.scan.fileformat.office.OfficeBinFileSummeryReader;
import com.ogawa.fico.service.FileNaming;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Objects;
import java.util.Stack;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import com.ogawa.fico.scan.fileformat.image.MediaMetadataReader;
import org.apache.poi.poifs.filesystem.NotOLE2FileException;

@Slf4j
public class PersistingFileVisitor implements FileVisitor<Path> {

    private final Path path;

    private final Creator<FileBean> dirCreator;
    private final Creator<FileBean> fileCreator;
    private final long scanId;

    private Long dirId = null;

    final private Stack<Long> dirIdStack = new Stack<>();

    @Getter
    private long fileCount = 0;

    @Getter
    private long dirCount = 0;

    @Getter
    private long totalSize = 0;

    public PersistingFileVisitor(long scanId, Path path, Creator<FileBean> dirRowCreator,
        Creator<FileBean> fileRowCreator) {
        this.scanId = scanId;
        this.path = path;
        this.dirCreator = dirRowCreator;
        this.fileCreator = fileRowCreator;
    }

    public void walk() throws IOException {
        Files.walkFileTree(path, this);
    }

    private void logDirChange(String verb, Path dir, Long dirId, Long parentDirId) {
        log.trace("Scan #{} {} {}", scanId, verb, dir);
        if (parentDirId == null) {
            log.trace("Scan #{}: FileId of {} is {} and it is a scan root directory", scanId, dir, dirId);
        } else {
            log.trace("Scan #{}: FileId of {} is {} and parent dir id is {}", scanId, dir, dirId, parentDirId);
        }
    }

    /**
     * Invoked for a directory logBefore entries in the directory are visited.
     *
     * <p> Unless overridden, this method returns {@link FileVisitResult#CONTINUE
     * CONTINUE}.
     */
    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attributes) {

        if (FileNaming.isRecycleBin(dir.toString())) {
            log.debug("Scan #{}: Skipping Recycle Bin directory {}", scanId, dir);
            return FileVisitResult.SKIP_SUBTREE;
        }

        FileBean dirBean = FileBeanFactory.create(dirId, scanId, dir, attributes);

        dirCreator.create(dirBean);

        dirIdStack.push(dirId);

        logDirChange("Entering", dir, dirBean.getFileId(), dirId);

        dirId = dirBean.getFileId();

        dirCount++;

        return FileVisitResult.CONTINUE;

    }

    /**
     * Invoked for a file in a directory.
     *
     * <p> Unless overridden, this method returns {@link FileVisitResult#CONTINUE
     * CONTINUE}.
     */
    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) {

        if (!attributes.isDirectory()) {

            FileBean fileBean = FileBeanFactory.create(dirId, scanId, file, attributes);

            if (false && MediaMetadataReader.isSupportedFileType(file)) {
                File mediaFile = new File(file.toString());
                try {
                    Metadata metadata = ImageMetadataReader.readMetadata(mediaFile);
                    for (Directory directory : metadata.getDirectories()) {
                        System.err.println("Directory: " + directory.getClass().getName());
                        for (Tag tag : directory.getTags()) {
                            System.out.println(tag.getTagTypeHex() + " " + tag);
                        }
                    }
                } catch (ImageProcessingException e) {
                    log.info("Scan #{}: Could not read metadata of {}. {}", scanId, file, e.getMessage());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            if (false && OfficeBinFileSummeryReader.isSupportedFileType(file)
                && !OfficeFileFormat.fromFilename(file.toString()).isXmlBased()
            ) {
                System.err.println("Office file: " + file);
                log.info("Scan #{}: Reading office file summary info of {}", scanId, file);
                try {
                    OfficeFileSummaryInfo officeFileSummary = OfficeBinFileSummeryReader.readSummaryInfo(file);
                    OfficeFileDocSummaryInfo officeFileDocSummary = OfficeBinFileSummeryReader.readDocSummaryInfo(
                        file);
                    if (officeFileDocSummary != null || officeFileSummary != null) {
                        System.err.println(officeFileDocSummary);
                        System.err.println(officeFileSummary);

                    }
                } catch (Exception e) {
                    log.info("Scan #{}: Could not read office file summary info of {}. {}", scanId, file,
                        e.getMessage());
                }
            }

            fileCreator.create(fileBean);

            fileCount++;

            totalSize += fileBean.getSize();

            log.info("Scan #{} added {}", scanId, file);

            log.trace("Scan #{}: FileId of file {} is {} and id of its dir {}",
                scanId, path, fileBean.getFileId(), dirId
            );

        }
        return FileVisitResult.CONTINUE;

    }

    /**
     * Invoked for a file that could not be visited.
     *
     * <p> Unless overridden, this method re-throws the I/O logException that prevented
     * the file from being visited.
     */
    @Override
    public FileVisitResult visitFileFailed(Path file, IOException ioException) {
        Objects.requireNonNull(file);
        log.warn("Scan #{}: Visit of {} failed: {}", scanId, file, ioException.getMessage());
        return FileVisitResult.CONTINUE;
    }

    /**
     * Invoked for a directory logAfter entries in the directory, and all of their descendants, have been visited.
     *
     * <p> Unless overridden, this method returns {@link FileVisitResult#CONTINUE
     * CONTINUE} if the directory iteration completes without an I/O logException; otherwise this method re-throws the
     * I/O logException that caused the iteration of the directory to terminate prematurely.
     */
    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException ioException) {

        dirId = dirIdStack.pop();

        logDirChange("Leaving", dir, dirId, dirIdStack.isEmpty() ? null : dirIdStack.peek());

        return FileVisitResult.CONTINUE;
    }

}
