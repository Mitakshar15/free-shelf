package com.freeshelf.api.v1.controller;

import com.freeshelf.api.builder.ApiResponseBuilder;
import com.freeshelf.api.data.domain.user.User;
import com.freeshelf.api.mapper.StorageSpaceMapper;
import com.freeshelf.api.service.interfaces.ImageService;
import com.freeshelf.api.service.interfaces.StorageSpaceService;
import com.freeshelf.api.service.interfaces.UserService;
import com.freeshelf.api.utils.Constants;
import lombok.RequiredArgsConstructor;
import org.producr.api.StorageSpaceControllerV1Api;
import org.producr.api.dtos.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
public class StorageSpaceController implements StorageSpaceControllerV1Api {

  private final StorageSpaceMapper mapper;
  private final ApiResponseBuilder apiResponseBuilder;
  private final UserService userService;
  private final StorageSpaceService storageSpaceService;
  private final ImageService imageService;


  @Override
  public ResponseEntity<StorageSpaceResponse> createStorageSpace(String authorization,
      CreateStorageSpaceRequest createStorageSpaceRequest) throws Exception {
    StorageSpaceResponse storageSpaceResponse = mapper.toStorageSpaceResponse(
        apiResponseBuilder.buildSuccessApiResponse(Constants.CREATE_STORAGE_SPACE_SUCCESS_MESSAGE));
    User user = userService.handleGetUserProfile(authorization);
    storageSpaceResponse.data(Set.of(mapper.toStorageSpaceDto(
        storageSpaceService.handleCreateStorageSpace(user, createStorageSpaceRequest))));
    return new ResponseEntity<>(storageSpaceResponse, HttpStatus.OK);
  }

  @Override
  public ResponseEntity<StorageSpaceResponse> getStorageSpaces(String authorization)
      throws Exception {
    StorageSpaceResponse response = mapper.toStorageSpaceResponse(
        apiResponseBuilder.buildSuccessApiResponse(Constants.GET_STORAGE_SPACE_SUCCESS_MESSAGE));
    User user = userService.handleGetUserProfile(authorization);
    response.data(mapper.toStorageSpaceSet(storageSpaceService.handleGetStorageSpaces(user)));
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @Override
  public ResponseEntity<SpaceImageResponse> setImageAsPrimary(Long spaceId, Long imageId)
      throws Exception {
    SpaceImageResponse response = mapper.toSpaceImageResponse(
        apiResponseBuilder.buildSuccessApiResponse(Constants.SET_IMAGE_AS_PRIMARY_SUCCESS_MESSAGE));
    response
        .data(mapper.toSpaceImageDto(List.of(imageService.setImageAsPrimary(spaceId, imageId))));
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @Override
  public ResponseEntity<SpaceImageResponse> addStorageSpaceImages(String authorization,
      Long storageSpaceId, @RequestParam("images") List<MultipartFile> images,
      @RequestParam(value = "captions", required = false) List<String> captions) throws Exception {
    SpaceImageResponse response = mapper.toSpaceImageResponse(apiResponseBuilder
        .buildSuccessApiResponse(Constants.ADD_STORAGE_SPACE_IMAGES_SUCCESS_MESSAGE));
    User user = userService.handleGetUserProfile(authorization);
    response.data(
        mapper.toSpaceImageDto(imageService.uploadImages(user, storageSpaceId, images, captions)));
    return new ResponseEntity<>(response, HttpStatus.CREATED);
  }

  @Override
  public ResponseEntity<StorageSpaceResponse> findNearestStorageSpace(String authorization,
      @RequestBody FindNearestStorageSpaceRequest findNearestStorageSpaceRequest) throws Exception {
    StorageSpaceResponse response = mapper.toStorageSpaceResponse(apiResponseBuilder
        .buildSuccessApiResponse(Constants.FIND_NEAREST_STORAGE_SPACE_SUCCESS_MESSAGE));
    User user = userService.handleGetUserProfile(authorization);
    response.data(mapper.toStorageSpaceSet(
        storageSpaceService.handleFindNearestStorageSpace(user, findNearestStorageSpaceRequest)));
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @Override
  public ResponseEntity<FreeShelfApiBaseApiResponse> updateAvailabilityPeriod(String authorization,
      UpdateAvailabilityPeriodRequest updateAvailabilityPeriodRequest, Long spaceId)
      throws Exception {
    FreeShelfApiBaseApiResponse response = mapper.toBaseApiResponse(apiResponseBuilder
        .buildSuccessApiResponse(Constants.UPDATE_AVAILABILITY_PERIOD_SUCCESS_MESSAGE));
    User user = userService.handleGetUserProfile(authorization);
    storageSpaceService.handleUpdateAvailabilityPeriod(user, updateAvailabilityPeriodRequest,
        spaceId);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @Override
  public ResponseEntity<FreeShelfApiBaseApiResponse> publishStorageSpace(String authorization,
      Long spaceId) throws Exception {
    FreeShelfApiBaseApiResponse response = mapper.toBaseApiResponse(apiResponseBuilder
        .buildSuccessApiResponse(Constants.STORAGE_SPACE_PUBLISHED_SUCCESS_MESSAGE));
    storageSpaceService.handlePublishStorageSpace(spaceId);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }
}
