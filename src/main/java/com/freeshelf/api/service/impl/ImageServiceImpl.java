package com.freeshelf.api.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

  @Value("${cloudinary.api.url}")
  private String cloudinaryUrl;

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
    return savedImages;
  }


  @Transactional
  @Override
  public SpaceImage setImageAsPrimary(Long spaceId, Long imageId) {
    StorageSpace space = storageSpaceRepository.findById(spaceId)
        .orElseThrow(() -> new RuntimeException("Storage space not found with id: " + spaceId));

    SpaceImage newPrimaryImage = spaceImageRepository.findByIdAndSpaceId(imageId, spaceId)
        .orElseThrow(() -> new RuntimeException("Image not found with id: " + imageId));


    spaceImageRepository.resetPrimaryFlagForSpace(spaceId);

    // Set the selected image as primary
    newPrimaryImage.setPrimary(true);
    return spaceImageRepository.save(newPrimaryImage);
  }

  private String saveFile(MultipartFile file, Long spaceId) {
    try {
      Cloudinary cloudinary = new Cloudinary(cloudinaryUrl);
      Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
          ObjectUtils.asMap("folder", "freeshelf/spaces/" + spaceId, "resource_type", "image"));
      return (String) uploadResult.get("secure_url");
    } catch (IOException e) {
      throw new RuntimeException("Failed to upload image to Cloudinary", e);
    }
  }

  private boolean isValidImageType(MultipartFile file) {
    String contentType = file.getContentType();
    return contentType != null && (contentType.equals("image/jpeg")
        || contentType.equals("image/jpg") || contentType.equals("image/png"));
  }
}
