package com.freeshelf.api.mapper;


import com.freeshelf.api.data.domain.user.User;
import com.freeshelf.api.dto.BaseApiResponse;
import org.mapstruct.Mapper;
import org.producr.api.dtos.*;

@Mapper(componentModel = "spring")
public interface UserMgmtMapper {

  User toUserEntity(SignUpRequest signUpRequest);

  AuthResponse toAuthResponse(BaseApiResponse apiResponse);

  UserMgmtBaseApiResponse toUserMgmtBaseApiResponse(BaseApiResponse apiResponse);

  UserProfileResponse toUserProfileResponse(BaseApiResponse baseApiResponse);

  UserDto toUserDto(User user);
}
