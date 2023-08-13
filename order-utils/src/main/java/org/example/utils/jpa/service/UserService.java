package org.example.utils.jpa.service;

import org.example.utils.jpa.entity.OrderEntity;
import org.example.utils.jpa.entity.UserEntity;
import org.example.utils.jpa.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Users 테이블의 모든 레코드를 가져오는 메서드
    public List<UserEntity> getAllUsers() {
        return userRepository.findAll();
    }

}
