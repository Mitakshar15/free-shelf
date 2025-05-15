package com.freeshelf.api.mapper;


import com.freeshelf.api.data.domain.user.Address;
import com.freeshelf.api.data.domain.user.User;
import com.freeshelf.api.dto.BaseApiResponse;
import jakarta.validation.Valid;
import org.mapstruct.Mapper;
import org.producr.api.dtos.*;

import java.util.Set;

@Mapper(componentModel = "spring")
public interface UserMgmtMapper {

  User toUserEntity(SignUpRequest signUpRequest);

  AuthResponse toAuthResponse(BaseApiResponse apiResponse);

  FreeShelfApiBaseApiResponse toUserMgmtBaseApiResponse(BaseApiResponse apiResponse);

  UserProfileResponse toUserProfileResponse(BaseApiResponse baseApiResponse);

  UserDto toUserDto(User user);

  Address toAddressEntity(AddNewAddressRequest addNewAddressRequest);


  Address toAddressEntity(EditAddressRequest editAddressRequest);

  AddressResponse toAddressResponse(BaseApiResponse baseApiResponse);

  Set<@Valid AddressDto> toAddressSet(Set<@Valid Address> addresses);
}
