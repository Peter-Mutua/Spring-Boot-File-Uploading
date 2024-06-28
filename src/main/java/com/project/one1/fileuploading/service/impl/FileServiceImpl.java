package com.project.one1.fileuploading.service.impl;

import com.project.one1.fileuploading.controller.FileUploaderController;
import com.project.one1.fileuploading.entity.CompanyFiles;
import com.project.one1.fileuploading.repository.FileRepository;
import com.project.one1.fileuploading.service.FileService;
import com.project.one1.fileuploading.utils.ResponseHandler;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
@AllArgsConstructor
public class FileServiceImpl implements FileService {

    private FileRepository fileRepository;

    private final Path root = Paths.get("uploads");

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(root);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize folder for upload!", e);
        }
    }

    @Override
    public ResponseEntity<Object> save(MultipartFile file) {
        log.info("Save file Method called....");

        String fileName = file.getOriginalFilename();
        try {

            Optional<CompanyFiles> findFile = fileRepository.findByFileNameAndSoftDelete(fileName, false);

            if (findFile.isPresent()) {
                return ResponseHandler.generateResponse("A file with the name " + findFile.get().getFileName() + " already exists!", HttpStatus.FOUND,  null);
            }

            assert fileName != null;
            Path filePath = this.root.resolve(fileName);

            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            CompanyFiles model = new CompanyFiles();
//            model.setCompanyId(companyId);
            model.setFileLocation(filePath.toString());
            model.setFileName(file.getOriginalFilename());
            model.setMetaData(file.getContentType());
//            model.setCreatedBy(createdBy);

            fileRepository.save(model);

            return ResponseHandler.generateResponse("File " +file.getOriginalFilename()+ " uploaded successfully!", HttpStatus.OK, model);

        } catch (Exception e) {
            if (e instanceof FileAlreadyExistsException) {
                throw new RuntimeException("A file of that name already exists.");
            }

            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public ResponseEntity<Object> getAllFiles() {
        try {
            List<CompanyFiles> files = fileRepository.findAllBySoftDelete(false);
            if (files == null || files.isEmpty()) {
                return ResponseHandler.generateResponse("No files found!", HttpStatus.NOT_FOUND, null);
            } else {
                List<CompanyFiles> fileInfos = files.stream().map(path -> {
                    long id = Long.parseLong(String.valueOf(Integer.parseInt(String.valueOf(path.getId()))));
                    String fileName = path.getFileName();
//                    String fileLocation = path.getFileLocation();
                    String metaData = path.getMetaData();
//                    Integer id = path.getCompanyId();
//                    String createBy = path.getCreateBy();
                    LocalDateTime createdOn = path.getCreatedOn();
                    String fileLocation = MvcUriComponentsBuilder
                            .fromMethodName(FileUploaderController.class, "getFile", path.getFileName()).build().toString();

                    return new CompanyFiles(id, fileName, metaData, fileLocation, createdOn);
                }).collect(Collectors.toList());
//                return ResponseEntity.status(HttpStatus.OK).body(fileInfos);
                return ResponseHandler.generateResponse("All files fetched successfully!", HttpStatus.OK, fileInfos);
            }
        } catch (Exception e) {
            return ResponseHandler.generateResponse("An error occurred while fetching all files!", HttpStatus.BAD_REQUEST, null);
        }
    }

    @Override
    public ResponseEntity<Resource> load(String filename) {
        try {
            Optional<CompanyFiles> filE = fileRepository.findByFileNameAndSoftDelete(filename, false);

            if (filE.isEmpty()) {
                String mssg = "File not found!";
                return ResponseEntity.noContent().build();
            }

            String fileName = String.valueOf(filE.get().getFileName());

            Path file = root.resolve(fileName);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok().body(resource);
            } else {
                throw new RuntimeException("Could not read the file!");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }

//    @GetMapping("/files/{filename:.+}")
//    @ResponseBody
//    public ResponseEntity<Resource> getFile(@PathVariable String filename) {
//        Resource file = storageService.load(filename);
//        return ResponseEntity.ok()
//                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"").body(file);
//    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(root.toFile());
    }

    @Override
    public Stream<Path> loadAll() {
        try {
            return Files.walk(this.root, 1).filter(path -> !path.equals(this.root)).map(this.root::relativize);
        } catch (IOException e) {
            throw new RuntimeException("Could not load the files!");
        }
    }
}
