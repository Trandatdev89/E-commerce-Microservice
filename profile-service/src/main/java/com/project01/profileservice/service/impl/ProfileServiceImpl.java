package com.project01.profileservice.service.impl;

import com.project01.profileservice.dto.response.ProfileDTO;
import com.project01.profileservice.dto.response.RegisterDTO;
import com.project01.profileservice.entity.ProfileEntity;
import com.project01.profileservice.exception.custom.AppException;
import com.project01.profileservice.exception.custom.ErrorCode;
import com.project01.profileservice.repository.ProfileRepository;
import com.project01.profileservice.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final ProfileRepository profileRepository;
    private final ModelMapper modelMapper;

    @Override
    public void createProfile(RegisterDTO registerDTO) {
          ProfileEntity profileEntity = modelMapper.map(registerDTO, ProfileEntity.class);
          profileRepository.save(profileEntity);
    }

    @Override
    public List<ProfileDTO> getAllProfiles() {
        List<ProfileEntity> profileEntities = profileRepository.findAll();
        List<ProfileDTO> profileDTOS = new ArrayList<>();
        for (ProfileEntity profileEntity : profileEntities) {
            profileDTOS.add(modelMapper.map(profileEntity, ProfileDTO.class));
        }
        return profileDTOS;
    }

    @Override
    public ProfileDTO getProfileByUserId(String id) {
        ProfileDTO profileDTO = modelMapper.map(profileRepository.findByUserid(id).orElseThrow(()-> new AppException(ErrorCode.USER_NOTFOUND)), ProfileDTO.class);
        return profileDTO;
    }

    @Override
    public ProfileDTO updateProfile(String userId, ProfileDTO profileDTO) {
        var profileEntity = profileRepository.findByUserid(userId).orElseThrow(()-> new AppException(ErrorCode.USER_NOTFOUND));
        ProfileEntity updateProfile = modelMapper.map(profileDTO, ProfileEntity.class);
        profileRepository.save(updateProfile);
        return modelMapper.map(updateProfile, ProfileDTO.class);
    }
}
