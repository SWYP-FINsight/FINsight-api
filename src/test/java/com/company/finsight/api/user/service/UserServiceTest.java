package com.company.finsight.api.user.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.company.finsight.api.user.entity.User;
import com.company.finsight.api.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void getUser() {
        //given
        User user = User.create("testuser", "password123!");
        ReflectionTestUtils.setField(user, "id", 1L);

        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));

        //when
        User foundUser = userService.getUser(user.getId());

        //then
        assertThat(foundUser.getId()).isEqualTo(user.getId());
        assertThat(foundUser.getUsername()).isEqualTo(user.getUsername());
    }
}