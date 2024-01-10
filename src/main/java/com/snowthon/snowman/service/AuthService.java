package com.snowthon.snowman.service;

import com.snowthon.snowman.domain.User;
import com.snowthon.snowman.dto.request.UserLoginDto;
import com.snowthon.snowman.dto.response.JwtTokenDto;
import com.snowthon.snowman.dto.type.ERole;
import com.snowthon.snowman.exception.CommonException;
import com.snowthon.snowman.repository.UserRepository;
import com.snowthon.snowman.utility.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Transactional
    public JwtTokenDto login(UserLoginDto userLoginDto) {
        Optional<User> existingUser = userRepository.findBySerialId(userLoginDto.providerId());

        if (existingUser.isPresent()) {
            return jwtUtil.generateTokens(existingUser.get().getSerialId(), ERole.USER);
        } else {
            User newUser = userRepository.save(User.signUp(userLoginDto.providerId()));
            return jwtUtil.generateTokens(newUser.getSerialId(), ERole.USER);
        }
    }
}
