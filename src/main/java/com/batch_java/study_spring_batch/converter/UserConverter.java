package com.batch_java.study_spring_batch.converter;

import com.batch_java.study_spring_batch.common.Converter;
import com.batch_java.study_spring_batch.model.UserNew;
import com.batch_java.study_spring_batch.model.User;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Converter
public class UserConverter {
    
    public UserNew toNewUser(
        User user
    ) {
        return UserNew.builder()
            // .id(user.getId())
            .name(user.getName())
            .age(user.getAge())
            .region(user.getRegion())
            .phoneNumber(user.getPhoneNumber())
            .build();
    }
}
