package com.project.one1.fileuploading.service;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.stream.Stream;

public interface FileService {
    public void init();

    public ResponseEntity<Object> save(MultipartFile[] files);

    public ResponseEntity<Object> getAllFiles();

    public ResponseEntity<Resource> load(String filename);

    public void deleteAll();

    public Stream<Path> loadAll();
}
