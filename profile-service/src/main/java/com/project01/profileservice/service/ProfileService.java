package com.project01.profileservice.service;

import com.project01.profileservice.dto.response.ProfileDTO;
import com.project01.profileservice.dto.response.RegisterDTO;


import java.util.List;

public interface ProfileService {
    void createProfile(RegisterDTO registerDTO);
    List<ProfileDTO> getAllProfiles();
    ProfileDTO getProfileByUserId(String id);
    ProfileDTO updateProfile(String userId,ProfileDTO profileDTO);
}
