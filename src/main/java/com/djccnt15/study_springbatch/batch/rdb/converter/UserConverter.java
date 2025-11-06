package com.djccnt15.study_springbatch.batch.rdb.converter;

import com.djccnt15.study_springbatch.annotation.Converter;
import com.djccnt15.study_springbatch.db.model.UserEntity;
import com.djccnt15.study_springbatch.db.model.UserNewEntity;
import lombok.RequiredArgsConstructor;

@Converter
@RequiredArgsConstructor
public class UserConverter {
    
    public UserNewEntity toNewUser(UserEntity userEntity) {
        return UserNewEntity.builder()
            // .id(user.getId())
            .name(userEntity.getName())
            .age(userEntity.getAge())
            .region(userEntity.getRegion())
            .phoneNumber(userEntity.getPhoneNumber())
            .build();
    }
}
