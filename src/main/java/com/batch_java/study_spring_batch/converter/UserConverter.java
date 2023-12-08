package com.batch_java.study_spring_batch.converter;

import com.batch_java.study_spring_batch.common.Converter;
import com.batch_java.study_spring_batch.model.UserNewEntity;
import com.batch_java.study_spring_batch.model.UserEntity;
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
