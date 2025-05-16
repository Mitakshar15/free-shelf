package com.freeshelf.api.service.impl;

import com.freeshelf.api.data.domain.space.SpaceImage;
import com.freeshelf.api.data.domain.space.StorageSpace;
import com.freeshelf.api.data.domain.user.User;
import com.freeshelf.api.data.repository.SpaceImageRepository;
import com.freeshelf.api.data.repository.StorageSpaceRepository;
import com.freeshelf.api.service.interfaces.ImageService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {


  @Value("${app.file.upload.dir}")
  private String uploadDir;

  @Value("${app.file.base-url}")
  private String baseUrl;

  private final StorageSpaceRepository storageSpaceRepository;
  private final SpaceImageRepository spaceImageRepository;

  @Transactional
  @Override
  public List<SpaceImage> uploadImages(User user, Long spaceId, List<MultipartFile> files,
      List<String> captions) throws BadRequestException {

    StorageSpace space = storageSpaceRepository.findById(spaceId)
        .orElseThrow(() -> new RuntimeException("Storage space not found with id: " + spaceId));

    if (files == null || files.isEmpty()) {
      throw new BadRequestException("No files provided for upload");
    }

    if (files.size() > 5) {
      throw new BadRequestException("Cannot upload more than 5 images at once");
    }

    boolean hasPrimaryImage = spaceImageRepository.existsBySpaceIdAndPrimaryTrue(spaceId);

    List<SpaceImage> savedImages = new ArrayList<>();

    for (int i = 0; i < files.size(); i++) {
      MultipartFile file = files.get(i);
      String caption = (captions != null && i < captions.size()) ? captions.get(i) : null;

      if (file.isEmpty()) {
        continue;
      }

      if (!isValidImageType(file)) {
        throw new BadRequestException(
            "Invalid file type. Only JPG, JPEG, and PNG files are allowed.");
      }
      String imageUrl = saveFile(file, spaceId);
      SpaceImage spaceImage = new SpaceImage();
      spaceImage.setSpace(space);
      spaceImage.setImageUrl(imageUrl);
      spaceImage.setCaption(caption);

      if (!hasPrimaryImage && i == 0) {
        spaceImage.setPrimary(true);
        hasPrimaryImage = true;
      } else {
        spaceImage.setPrimary(false);
      }

      spaceImageRepository.save(spaceImage);
      savedImages.add(spaceImage);
      space.getImages().add(spaceImage);
    }
    storageSpaceRepository.saveAndFlush(space);

    storageSpaceRepository.flush();

    return savedImages;
  }

  /**
   * Set an image as the primary image for a storage space
   *
   * @param spaceId The ID of the storage space
   * @param imageId The ID of the image to set as primary
   * @return The updated primary image
   */
  @Transactional
  @Override
  public SpaceImage setImageAsPrimary(Long spaceId, Long imageId) {
    StorageSpace space = storageSpaceRepository.findById(spaceId)
        .orElseThrow(() -> new RuntimeException("Storage space not found with id: " + spaceId));

    SpaceImage newPrimaryImage = spaceImageRepository.findByIdAndSpaceId(imageId, spaceId)
        .orElseThrow(() -> new RuntimeException("Image not found with id: " + imageId));

    // Reset all images' primary flag to false
    spaceImageRepository.resetPrimaryFlagForSpace(spaceId);

    // Set the selected image as primary
    newPrimaryImage.setPrimary(true);
    return spaceImageRepository.save(newPrimaryImage);
  }

  /**
   * Save file to server and return its URL
   */
  private String saveFile(MultipartFile file, Long spaceId) {
    try {
      // Create directory structure for organized storage
      String directoryPath = String.format("%s/spaces/%d/images", uploadDir, spaceId);
      Path directory = Paths.get(directoryPath);
      Files.createDirectories(directory);

      // Generate unique filename to prevent collisions
      String originalFilename = file.getOriginalFilename();
      String fileExtension = getFileExtension(originalFilename);
      String uniqueFilename = UUID.randomUUID().toString() + fileExtension;

      // Save the file
      Path filePath = directory.resolve(uniqueFilename);
      Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

      // Return the URL
      // return String.format("%s/%s/%s", baseUrl, directoryPath, uniqueFilename);
      return String.format("%s/%s", directoryPath, uniqueFilename);
    } catch (IOException e) {
      throw new RuntimeException("Failed to store file", e);
    }
  }

  private boolean isValidImageType(MultipartFile file) {
    String contentType = file.getContentType();
    return contentType != null && (contentType.equals("image/jpeg")
        || contentType.equals("image/jpg") || contentType.equals("image/png"));
  }

  private String getFileExtension(String filename) {
    if (filename == null) {
      return ".jpg"; // Default extension
    }
    int dotIndex = filename.lastIndexOf('.');
    if (dotIndex < 0) {
      return ".jpg"; // Default extension if no extension found
    }
    return filename.substring(dotIndex);
  }


}
