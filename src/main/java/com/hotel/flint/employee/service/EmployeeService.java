package com.hotel.flint.employee.service;

import com.hotel.flint.common.DepartMent;
import com.hotel.flint.diningreservation.domain.DiningReservation;
import com.hotel.flint.diningreservation.dto.DiningReservationInfoResDto;
import com.hotel.flint.diningreservation.repository.DiningReservationRepository;
import com.hotel.flint.employee.domain.Employee;
import com.hotel.flint.employee.dto.*;
import com.hotel.flint.employee.repository.EmployeeRepository;
import com.hotel.flint.member.domain.Member;
import com.hotel.flint.member.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;

@Service
@Transactional
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final MemberService memberService;
    private final DiningReservationRepository diningReservationRepository;

    @Autowired
    public EmployeeService(EmployeeRepository employeeRepository,
                           MemberService memberService,
                           DiningReservationRepository diningReservationRepository){
        this.employeeRepository = employeeRepository;
        this.memberService = memberService;
        this.diningReservationRepository = diningReservationRepository;
    }

    public InfoUserResDto memberInfo(Long id){
        Member member = memberService.findByUserId(id);
        return member.infoUserEntity();
    }
    public DiningReservationInfoResDto memberDiningReservationInfo(Long id){
        Member member = memberService.findByUserId(id);
        DiningReservation diningReservation = diningReservationRepository.findByMemberId(member);
        DiningReservationInfoResDto dto = diningReservation.toEntity();

        return dto;
    }

    public EmployeeDetResDto employeeDetail(Long id){
        Employee employee = employeeRepository.findById(id).orElseThrow(()->new EntityNotFoundException("해당 ID가 존재하지 않습니다."));
        return employee.EmpDetEntity();
    }

    public void employeeModify(EmployeeModResDto dto){
        Employee employee = this.findByEmpId(dto.getId());
        employee.modifyEmp(dto.getPassword());
        employeeRepository.save(employee);
    }

    public Employee findByEmpId(Long id){
        return employeeRepository.findById(id).orElseThrow(()->new EntityNotFoundException("해당 ID가 존재하지 않습니다."));
    }

    public Employee modEmployeeRank(EmployeeRankModResDto dto){
        Employee officeEmployee = employeeRepository.findById(dto.getOfficeId()).orElseThrow(()->new EntityNotFoundException("해당 ID가 존재하지 않습니다."));
        if(!officeEmployee.getDepartment().equals(DepartMent.Office))
            throw new IllegalArgumentException("Office 부서만 수정이 가능합니다.");
        Employee targetEmployee = employeeRepository.findById(dto.getTargetId()).orElseThrow(()->new EntityNotFoundException("해당 ID가 존재하지 않습니다."));

        targetEmployee.modifyRank(dto.getEmployeeRank());
        return employeeRepository.save(targetEmployee);
    }
}
