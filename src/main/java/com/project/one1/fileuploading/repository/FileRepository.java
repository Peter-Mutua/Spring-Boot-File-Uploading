package com.project.one1.fileuploading.repository;

import com.project.one1.fileuploading.entity.CompanyFiles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileRepository extends JpaRepository<CompanyFiles, Long> {
    Optional<CompanyFiles> findByFileNameAndSoftDelete(String fileName, boolean softDelete);

    List<CompanyFiles> findAllBySoftDelete(boolean softDelete);
}
