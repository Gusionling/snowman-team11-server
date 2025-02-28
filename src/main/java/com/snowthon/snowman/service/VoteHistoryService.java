package com.snowthon.snowman.service;

import com.snowthon.snowman.domain.*;
import com.snowthon.snowman.dto.request.VoteRequestDto;
import com.snowthon.snowman.dto.type.ErrorCode;
import com.snowthon.snowman.dto.type.wear.EHeadWear;
import com.snowthon.snowman.dto.type.wear.ENeckWear;
import com.snowthon.snowman.dto.type.wear.EOuterWear;
import com.snowthon.snowman.dto.type.wear.ETopWear;
import com.snowthon.snowman.exception.CommonException;
import com.snowthon.snowman.repository.RegionRepository;
import com.snowthon.snowman.repository.UserRegionVoteRepository;
import com.snowthon.snowman.repository.UserRepository;
import com.snowthon.snowman.repository.VoteHistoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class VoteHistoryService {

    private final VoteHistoryRepository voteHistoryRepository;
    private final UserRegionVoteRepository userRegionVoteRepository;
    private final RegionRepository regionRepository;
    private final UserRepository userRepository;

    //3-2. 투표 하기
    @Transactional
    public void createVote(Long regionId, VoteRequestDto voteInfoDto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));

        Region region = regionRepository.findById(regionId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_REGION));
/*
        if(userRegionVoteRepository.existsByUserIdAndRegionId(userId, regionId)){
           throw new CommonException(ErrorCode.ALREADY_VOTED);
        }
        
 */
        Branch mainBranch = region.getMainBranch();

        mainBranch.updateVote(
                ETopWear.valueOf(voteInfoDto.topWear()),
                EOuterWear.valueOf(voteInfoDto.outerWear()),
                EHeadWear.valueOf(voteInfoDto.headWear()),
                ENeckWear.valueOf(voteInfoDto.neckWear())
        );

        VoteHistory voteHistory = VoteHistory.createFrom(
                user, region, mainBranch,
                ETopWear.valueOf(voteInfoDto.topWear()),
                EOuterWear.valueOf(voteInfoDto.outerWear()),
                EHeadWear.valueOf(voteInfoDto.headWear()),
                ENeckWear.valueOf(voteInfoDto.neckWear())
        );

        voteHistoryRepository.save(voteHistory);
        userRegionVoteRepository.save(UserRegionVote.create(user, region));
    }

    //3-1. 투표 여부 확인
    public boolean checkUserVoting(Long userId, Long regionId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));

        Region region = regionRepository.findById(regionId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_REGION));

        return userRegionVoteRepository.existsByUserAndRegion(user, region);
    }


}
