package com.hotel.flint.member.service;

import com.hotel.flint.member.domain.Member;
import com.hotel.flint.member.dto.MemberDetResDto;
import com.hotel.flint.member.dto.MemberModResDto;
import com.hotel.flint.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;

@Service
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;

    @Autowired
    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public MemberDetResDto memberDetail(Long id){
        Member member = findByUserId(id);

        return member.detUserEntity();
    }

    public void memberDelete(Long id){
        Member member = memberRepository.findById(id).orElseThrow(()-> new EntityNotFoundException("해당 id가 존재하지 않습니다."));
        member.deleteUser();
        memberRepository.save(member);
    }

    public void memberModify(MemberModResDto dto){
        Member member = this.findByUserId(dto.getId());
        member.modifyUser(dto.getPassword());
        memberRepository.save(member);
    }
    public Member findByUserId(Long id) {
        Member member = memberRepository.findById(id).orElseThrow(()->new EntityNotFoundException("해당 id가 존재하지 않습니다."));
        return member;
    }
}
