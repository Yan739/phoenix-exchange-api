package com.yann.phoenix_exchange_api.service;

import com.yann.phoenix_exchange_api.dto.user.UserCreateDTO;
import com.yann.phoenix_exchange_api.dto.user.UserDTO;
import com.yann.phoenix_exchange_api.dto.user.UserUpdateDTO;
import com.yann.phoenix_exchange_api.entity.user.User;
import com.yann.phoenix_exchange_api.entity.user.UserRole;
import com.yann.phoenix_exchange_api.exception.ResourceNotFoundException;
import com.yann.phoenix_exchange_api.mapper.UserMapper;
import com.yann.phoenix_exchange_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserDTO getById(Long id) {
        User user = findUserById(id);
        return userMapper.toDTO(user);
    }

    public UserDTO getByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
        return userMapper.toDTO(user);
    }

    public Page<UserDTO> getAll(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(userMapper::toDTO);
    }

    public List<UserDTO> getByRole(UserRole role) {
        return userRepository.findByRole(role).stream()
                .map(userMapper::toDTO)
                .toList();
    }

    public UserDTO create(UserCreateDTO createDTO) {
        if (userRepository.existsByUsername(createDTO.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }

        if (userRepository.existsByEmail(createDTO.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        User user = userMapper.toEntity(createDTO);
        user.setPasswordHash(passwordEncoder.encode(createDTO.getPassword()));

        User saved = userRepository.save(user);
        log.info("User created: {}", saved.getUsername());

        return userMapper.toDTO(saved);
    }

    public UserDTO update(Long id, UserUpdateDTO updateDTO) {
        User user = findUserById(id);
        userMapper.updateEntityFromDTO(updateDTO, user);

        User updated = userRepository.save(user);
        log.info("User updated: {}", id);

        return userMapper.toDTO(updated);
    }

    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found: " + id);
        }

        userRepository.deleteById(id);
        log.info("User deleted: {}", id);
    }

    public void activateUser(Long id) {
        User user = findUserById(id);
        user.setIsActive(true);
        userRepository.save(user);
        log.info("User activated: {}", id);
    }

    public void deactivateUser(Long id) {
        User user = findUserById(id);
        user.setIsActive(false);
        userRepository.save(user);
        log.info("User deactivated: {}", id);
    }

    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
    }
}