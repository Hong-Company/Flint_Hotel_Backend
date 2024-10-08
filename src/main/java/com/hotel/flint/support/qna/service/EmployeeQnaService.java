package com.hotel.flint.support.qna.service;

import com.hotel.flint.common.enumdir.Option;
import com.hotel.flint.support.qna.controller.InquirySseController;
import com.hotel.flint.support.qna.domain.QnA;
import com.hotel.flint.support.qna.dto.CreateAnswerDto;
import com.hotel.flint.support.qna.dto.EmployeeQnaDetailDto;
import com.hotel.flint.support.qna.dto.EmployeeQnaListDto;
import com.hotel.flint.support.qna.dto.UpdateAnswerDto;
import com.hotel.flint.support.qna.repository.QnaRepository;
import com.hotel.flint.user.employee.domain.Employee;
import com.hotel.flint.user.employee.repository.EmployeeRepository;
import com.hotel.flint.user.member.domain.Member;
import com.hotel.flint.user.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

@Service
@Transactional
public class EmployeeQnaService {

    private final QnaRepository qnaRepository;
    private final EmployeeRepository employeeRepository;
    private final InquirySseController sseController; // SseController 의존성 주입
    private final MemberRepository memberRepository;

    @Autowired
    public EmployeeQnaService(QnaRepository qnaRepository, EmployeeRepository employeeRepository, InquirySseController sseController, MemberRepository memberRepository) {
        this.qnaRepository = qnaRepository;
        this.employeeRepository = employeeRepository;
        this.sseController = sseController; // 주입
        this.memberRepository = memberRepository;
    }

    private Employee getAuthenticatedEmployee() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String email = userDetails.getUsername();
            return employeeRepository.findByEmailAndDelYN(email, Option.N)
                    .orElseThrow(() -> new SecurityException("인증되지 않은 사용자입니다."));
        } else {
            throw new SecurityException("인증되지 않은 사용자입니다.");
        }
    }

    // QnA 답변 작성
    public QnA createAnswer(Long qnaId, CreateAnswerDto dto) {

        // 관리자 권한으로 로그인된 사용자 가져오기
        Employee employee = getAuthenticatedEmployee();
        QnA qna = qnaRepository.findById(qnaId).orElseThrow(() -> new EntityNotFoundException("해당 질문이 없습니다."));
        Member member = memberRepository.findById(qna.getMember().getId())
                .orElseThrow(() -> new EntityNotFoundException("해당하는 Member ID가 없습니다."));

        if (qna.getRespond().equals(Option.N)) {

            qna = dto.toEntity(qna, employee);
//            메서드명 일치
            sseController.publishQnaMessage(qna.getAnswer(), member.getEmail());
            return qnaRepository.save(qna);

        } else {
            throw new IllegalStateException("이미 답변이 존재합니다.");
        }
    }

    // QnA 답변 수정
    public QnA updateAnswer(Long qnaId, UpdateAnswerDto dto) {

        Employee employee = getAuthenticatedEmployee();

        QnA qna = qnaRepository.findById(qnaId).orElseThrow(() -> new EntityNotFoundException("해당 질문이 없습니다."));

        if (qna.getRespond().equals(Option.Y)) {

            qna = dto.toEntity(qna, employee);
            return qnaRepository.save(qna);

        } else {
            throw new IllegalStateException("답변이 존재하지 않습니다.");
        }


    }

    // QnA 답변 삭제
    public QnA deleteAnswer(Long qnaId) {

        QnA qna = qnaRepository.findById(qnaId).orElseThrow(() -> new EntityNotFoundException("해당 질문이 없습니다."));

        if (qna.getRespond().equals(Option.Y)) {

            qna.DeleteQna();
            return qnaRepository.save(qna);

        } else {
            throw new IllegalStateException("답변이 존재하지 않습니다.");
        }

    }

    // QnA 리스트 목록 전체 조회
    public Page<EmployeeQnaListDto> employeeQnaListPage(String email, Pageable pageable) {
        // Specification을 사용하여 이메일 필터링 추가
        Specification<QnA> specification = (root, query, criteriaBuilder) -> {
            if (email != null && !email.isEmpty()) {
                // member 객체에서 email을 가져와서 필터링 조건 추가
                return criteriaBuilder.like(
                        root.join("member").get("email"), "%" + email + "%");
            }
            return null; // 조건이 없으면 모든 결과 반환
        };

        // Specification과 페이징을 사용하여 쿼리 실행
        Page<QnA> qnaPageList = qnaRepository.findAll(specification, pageable);
        return qnaPageList.map(QnA::ListEntity); // QnA 엔티티를 DTO로 변환
    }


    // QnA 및 답변 상세 조회
    public EmployeeQnaDetailDto employeeDetailQnA(Long qnaId){

        QnA qna = qnaRepository.findById(qnaId).orElseThrow(() -> new EntityNotFoundException("해당 질문이 없습니다"));
        EmployeeQnaDetailDto employeeQnaDetailDto = qna.DetailEntity();
        return employeeQnaDetailDto;
    }


}
