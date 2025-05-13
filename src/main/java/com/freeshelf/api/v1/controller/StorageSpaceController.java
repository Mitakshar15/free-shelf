package com.freeshelf.api.v1.controller;

import com.freeshelf.api.builder.ApiResponseBuilder;
import com.freeshelf.api.data.domain.space.StorageSpace;
import com.freeshelf.api.data.domain.user.User;
import com.freeshelf.api.mapper.StorageSpaceMapper;
import com.freeshelf.api.service.interfaces.StorageSpaceService;
import com.freeshelf.api.service.interfaces.UserService;
import com.freeshelf.api.utils.Constants;
import lombok.RequiredArgsConstructor;
import org.aspectj.apache.bcel.classfile.Constant;
import org.producr.api.StorageSpaceControllerV1Api;
import org.producr.api.dtos.CreateStorageSpaceRequest;
import org.producr.api.dtos.FreeShelfApiBaseApiResponse;
import org.producr.api.dtos.StorageSpaceResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class StorageSpaceController implements StorageSpaceControllerV1Api {

  private final StorageSpaceMapper mapper;
  private final ApiResponseBuilder apiResponseBuilder;
  private final UserService userService;
  private final StorageSpaceService storageSpaceService;


  @Override
  public ResponseEntity<StorageSpaceResponse> createStorageSpace(String authorization,
      CreateStorageSpaceRequest createStorageSpaceRequest) throws Exception {
    StorageSpaceResponse storageSpaceResponse = mapper.toStorageSpaceResponse(
        apiResponseBuilder.buildSuccessApiResponse(Constants.CREATE_STORAGE_SPACE_SUCCESS_MESSAGE));
    User user = userService.handleGetUserProfile(authorization);
    storageSpaceResponse.data(List.of(mapper.toStorageSpaceDto(
        storageSpaceService.handleCreateStorageSpace(user, createStorageSpaceRequest))));
    return new ResponseEntity<>(storageSpaceResponse, HttpStatus.OK);
  }

  @Override
  public ResponseEntity<StorageSpaceResponse> getStorageSpaces(String authorization)
      throws Exception {
    StorageSpaceResponse response = mapper.toStorageSpaceResponse(apiResponseBuilder.buildSuccessApiResponse(Constants.GET_STORAGE_SPACE_SUCCESS_MESSAGE));
    User user = userService.handleGetUserProfile(authorization);
    response.data(mapper.toStorageSpaceSet(storageSpaceService.handleGetStorageSpaces(user)));
    return new ResponseEntity<>(response, HttpStatus.OK);
  }
}
