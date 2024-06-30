package com.project.one1.fileuploading.controller;

import com.project.one1.fileuploading.service.FileService;
import com.project.one1.fileuploading.utils.ResponseHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@CrossOrigin("*")
@RestController
@RequestMapping("/api/v1")
public class FileUploaderController {

    @Autowired
    private FileService fileService;

    @PostMapping("/upload")
    public ResponseEntity<Object> uploadFile(@RequestParam("file") MultipartFile[] files) {
        try {
            log.info("Uploading file with size: {} bytes.....", files.length);
            return fileService.save(files);
        } catch (Exception e) {
            return ResponseHandler.generateResponse("An error occurred while uploading the file" + e.getMessage(), HttpStatus.BAD_REQUEST, null);
        }
    }

    @GetMapping("/allFiles")
    public ResponseEntity<Object> getAllFiles() {
        try {
            return fileService.getAllFiles();
        } catch (Exception e) {
            return ResponseHandler.generateResponse("An error occurred while fetching the files" + e.getMessage(), HttpStatus.BAD_REQUEST, null);
        }
    }

    @GetMapping("/download/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> getFile(@PathVariable String filename) {
        try {
            return fileService.load(filename);
        } catch (Exception e) {
//            return ResponseHandler.generateResponse("An error occurred while retrieving the file " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, null);
            return ResponseEntity.badRequest()
                    .build();
        }
    }
}
