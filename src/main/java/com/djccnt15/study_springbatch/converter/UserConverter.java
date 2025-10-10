package com.djccnt15.study_springbatch.converter;

import com.djccnt15.study_springbatch.common.Converter;
import com.djccnt15.study_springbatch.model.UserEntity;
import com.djccnt15.study_springbatch.model.UserNewEntity;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Converter
public class UserConverter {
    
    public UserNewEntity toNewUser(
        UserEntity userEntity
    ) {
        return UserNewEntity.builder()
            // .id(user.getId())
            .name(userEntity.getName())
            .age(userEntity.getAge())
            .region(userEntity.getRegion())
            .phoneNumber(userEntity.getPhoneNumber())
            .build();
    }
}
