package com.freeshelf.api.service.interfaces;

import com.freeshelf.api.data.domain.space.SpaceImage;
import com.freeshelf.api.data.domain.user.User;
import org.apache.coyote.BadRequestException;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ImageService {

  List<SpaceImage> uploadImages(User user, Long spaceId, List<MultipartFile> files,
      List<String> captions) throws BadRequestException;

  SpaceImage setImageAsPrimary(Long spaceId, Long imageId);
}
