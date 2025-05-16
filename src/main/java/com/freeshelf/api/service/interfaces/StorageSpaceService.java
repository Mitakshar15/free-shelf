package com.freeshelf.api.service.interfaces;


import com.freeshelf.api.data.domain.space.StorageSpace;
import com.freeshelf.api.data.domain.user.User;
import org.producr.api.dtos.CreateStorageSpaceRequest;
import org.producr.api.dtos.FindNearestStorageSpaceRequest;
import org.producr.api.dtos.StorageSpaceResponse;
import org.producr.api.dtos.UpdateAvailabilityPeriodRequest;

import java.util.Set;

public interface StorageSpaceService {

  StorageSpace handleCreateStorageSpace(User user,
      CreateStorageSpaceRequest createStorageSpaceRequest);

  Set<StorageSpace> handleGetStorageSpaces(User user);

  Set<StorageSpace> handleFindNearestStorageSpace(User user,
      FindNearestStorageSpaceRequest findNearestStorageSpaceRequest);

  void handleUpdateAvailabilityPeriod(User user,
      UpdateAvailabilityPeriodRequest updateAvailabilityPeriodRequest, Long spaceId);

  void handlePublishStorageSpace(Long spaceId);
}
