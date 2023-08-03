package com.example.MyBookShopApp.services;

import com.example.MyBookShopApp.aspect.annotations.ServiceProcessTrackable;
import com.example.MyBookShopApp.data.entity.book.BookEntity;
import com.example.MyBookShopApp.data.entity.book.file.BookFileEntity;
import com.example.MyBookShopApp.repositories.BookFileRepository;
import com.example.MyBookShopApp.repositories.BookRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
public class BookFileService {

    private final BookRepository bookRepository;
    private final BookFileRepository bookFileRepository;

    @Value("${path.download}")
    private String downloadPath;


    @Value("${path.upload}")
    private String uploadPath;

    @ServiceProcessTrackable
    public void saveNewBookImage(MultipartFile file, String slug) throws Exception {
        String resourceURI = null;
        if (!file.isEmpty()) {
            if (!new File(uploadPath).exists()) {
                Files.createDirectories(Paths.get(uploadPath));
            }
            String fileName = slug + "." + FilenameUtils.getExtension(file.getOriginalFilename());
            Path path = Paths.get(uploadPath, fileName);
            resourceURI = "/book-covers/" + fileName;
            file.transferTo(path);
        }
        saveBookImage(slug, resourceURI);
    }

    @ServiceProcessTrackable
    public void saveBookImage(String slug, String savePath) {
        BookEntity book = bookRepository.findBySlug(slug).orElseThrow();
        book.setImage(savePath);
        bookRepository.save(book);
    }

    public Path getBookFilePath(String hash) {
        return Paths.get(getBookFileByHash(hash).getPath());
    }

    public MediaType getBookFileMime(String hash) {
        String mimeType = URLConnection.guessContentTypeFromName(getBookFilePath(hash).getFileName().toString());
        if (mimeType != null) {
            return MediaType.parseMediaType(mimeType);
        } else {
            return MediaType.APPLICATION_OCTET_STREAM;
        }
    }

    @ServiceProcessTrackable
    public byte[] getBookFileByteArray(String hash) throws IOException {
        Path path = Paths.get(downloadPath, getBookFileByHash(hash).getPath());
        return Files.readAllBytes(path);
    }

    private BookFileEntity getBookFileByHash(String hash) {
        return bookFileRepository.findByHash(hash);

    }
}
