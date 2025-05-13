package com.freeshelf.api.service.interfaces;


import com.freeshelf.api.data.domain.space.StorageSpace;
import com.freeshelf.api.data.domain.user.User;
import org.producr.api.dtos.CreateStorageSpaceRequest;
import org.producr.api.dtos.StorageSpaceResponse;

import java.util.Set;

public interface StorageSpaceService {

  StorageSpace handleCreateStorageSpace(User user,
      CreateStorageSpaceRequest createStorageSpaceRequest);

  Set<StorageSpace> handleGetStorageSpaces(User user);
}
