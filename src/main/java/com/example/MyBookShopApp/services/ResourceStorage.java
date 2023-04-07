package com.example.MyBookShopApp.services;

import com.example.MyBookShopApp.data.struct.book.file.BookFileEntity;
import com.example.MyBookShopApp.errors.CommonErrorException;
import com.example.MyBookShopApp.repositories.BookFileRepository;
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
public class ResourceStorage {

    private final BookService bookService;
    private final BookFileRepository bookFileRepository;

    @Value("${path.download}")
    private String downloadPath;


    @Value("${path.upload}")
    private String uploadPath;
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
        bookService.saveBookImage(slug, resourceURI);
    }

    public BookFileEntity getBookFileByHash(String hash) {
        return bookFileRepository.findByHash(hash);

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

    public byte[] getBookFileByteArray(String hash) throws IOException {
        Path path = Paths.get(downloadPath, getBookFileByHash(hash).getPath());
        return Files.readAllBytes(path);
    }
}
