package com.freeshelf.api.service.impl;

import com.freeshelf.api.data.domain.space.StorageSpace;
import com.freeshelf.api.data.domain.user.Address;
import com.freeshelf.api.data.domain.user.User;
import com.freeshelf.api.data.repository.AddressRepository;
import com.freeshelf.api.data.repository.StorageSpaceRepository;
import com.freeshelf.api.mapper.StorageSpaceMapper;
import com.freeshelf.api.service.interfaces.StorageSpaceService;
import com.freeshelf.api.utils.enums.SpaceStatus;
import lombok.RequiredArgsConstructor;
import org.producr.api.dtos.CreateStorageSpaceRequest;
import org.producr.api.dtos.FindNearestStorageSpaceRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class StorageServiceImpl implements StorageSpaceService {

  private final StorageSpaceMapper mapper;
  private final AddressRepository addressRepository;
  private final StorageSpaceRepository storageSpaceRepository;


  @Override
  @PreAuthorize("hasRole('ROLE_HOST')")
  public StorageSpace handleCreateStorageSpace(User user,
      CreateStorageSpaceRequest createStorageSpaceRequest) {
    // TODO: Handle The Storage Space Image Upload Functionality to Securely Store and Access the
    // Images
    StorageSpace space = mapper.toStorageSpaceEntity(createStorageSpaceRequest);
    Address address = addressRepository.findById(createStorageSpaceRequest.getAddressId())
        .orElseThrow(() -> new RuntimeException("Address not found"));
    space.setAddress(address);
    space.setHost(user);
    space.setStatus(SpaceStatus.DRAFT);
    space.getAvailabilityPeriod().setSpace(space);
    return storageSpaceRepository.save(space);
  }

  @Override
  @PreAuthorize("isAuthenticated()")
  public Set<StorageSpace> handleGetStorageSpaces(User user) {
    return storageSpaceRepository.findByHost(user).orElse(Set.of());
  }

  @Override
  // @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_RENTER','ROLE_ANONYMOUS')")
  public Set<StorageSpace> handleFindNearestStorageSpace(User user,
      FindNearestStorageSpaceRequest findNearestStorageSpaceRequest) {
    // TODO: implement Actual logic for finding nearest Location for given latitude and longitude
    return storageSpaceRepository.findNearbyStorageSpaces(
        findNearestStorageSpaceRequest.getLatitude(), findNearestStorageSpaceRequest.getLongitude(),
        findNearestStorageSpaceRequest.getRadius(), user).orElse(Set.of());
  }
}
