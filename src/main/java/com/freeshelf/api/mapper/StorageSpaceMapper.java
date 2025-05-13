package com.freeshelf.api.mapper;

import com.freeshelf.api.data.domain.space.SpaceImage;
import com.freeshelf.api.data.domain.space.StorageSpace;
import com.freeshelf.api.dto.BaseApiResponse;
import jakarta.validation.Valid;
import org.mapstruct.Mapper;
import org.producr.api.dtos.*;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface StorageSpaceMapper {
  FreeShelfApiBaseApiResponse toBaseApiResponse(BaseApiResponse baseApiResponse);

  StorageSpace toStorageSpaceEntity(CreateStorageSpaceRequest createStorageSpaceRequest);

  Set<SpaceImage> toSpaceImageEntity(@Valid Set<@Valid SpaceImageDto> images);

  StorageSpaceResponse toStorageSpaceResponse(BaseApiResponse baseApiResponse);

  StorageSpaceDto toStorageSpaceDto(StorageSpace storageSpace);

  List<@Valid StorageSpaceDto> toStorageSpaceSet(Set<StorageSpace> storageSpaces);

  SpaceImageResponse toSpaceImageResponse(BaseApiResponse baseApiResponse);

  @Valid
  List<SpaceImageDto> toSpaceImageDto(List<SpaceImage> spaceImage);
}

