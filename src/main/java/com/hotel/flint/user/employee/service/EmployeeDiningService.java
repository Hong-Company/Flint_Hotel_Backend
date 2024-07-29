package com.hotel.flint.user.employee.service;

import com.hotel.flint.common.enumdir.Option;
import com.hotel.flint.dining.domain.Dining;
import com.hotel.flint.dining.domain.Menu;
import com.hotel.flint.dining.dto.MenuSaveDto;
import com.hotel.flint.dining.repository.DiningRepository;
import com.hotel.flint.dining.repository.MenuRepository;
import com.hotel.flint.user.employee.domain.Employee;
import com.hotel.flint.user.employee.repository.EmployeeRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

@Service
@Transactional
public class EmployeeDiningService {
    private final DiningRepository diningRepository;
    private final MenuRepository menuRepository;
    private final EmployeeRepository employeeRepository;

    public EmployeeDiningService(DiningRepository diningRepository, MenuRepository menuRepository, EmployeeRepository employeeRepository){
        this.diningRepository = diningRepository;
        this.menuRepository = menuRepository;
        this.employeeRepository = employeeRepository;
    }

    private Employee getAuthenticatedEmployee() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            System.out.println(userDetails);
            String email = userDetails.getUsername();
            return employeeRepository.findByEmailAndDelYN(email, Option.N)
                    .orElseThrow(() -> new SecurityException("인증되지 않은 사용자입니다."));
        } else {
            throw new SecurityException("인증되지 않은 사용자입니다.");
        }
    }

    public void addDiningMenu(MenuSaveDto menuSaveDto){
        Employee authenticatedEmployee = getAuthenticatedEmployee();

        Dining dining = diningRepository.findById(menuSaveDto.getDiningId())
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 Dining ID"));
        if(authenticatedEmployee.getDepartment().toString() != dining.getDiningName().toString()){
            throw new IllegalArgumentException("접근 권한이 없습니다.");
        }
        Menu menu = menuSaveDto.toEntity(dining);
        menuRepository.save(menu);
    }

    public void modDiningMenu(Long menuId, int newCost){
        Employee authenticatedEmployee = getAuthenticatedEmployee();

        Menu menu = menuRepository.findById(menuId).orElseThrow(
                () -> new EntityNotFoundException("존재하지 않는 메뉴"));
        Dining dining = diningRepository.findById(menu.getDining().getId()).
                orElseThrow(() -> new EntityNotFoundException("존재하지 않는 Dining ID"));
        if(authenticatedEmployee.getDepartment().toString() != dining.getDiningName().toString()){
            throw new IllegalArgumentException("접근 권한이 없습니다.");
        }

        menu.menuUpdate(newCost);
        menuRepository.save(menu);
    }

    public void delDiningMenu(Long menuId){
        Employee authenticatedEmployee = getAuthenticatedEmployee();

        Menu menu = menuRepository.findById(menuId).orElseThrow(
                () -> new EntityNotFoundException("삭제하려는 메뉴가 존재하지 않습니다."));
        Dining dining = diningRepository.findById(menu.getDining().getId()).
                orElseThrow(() -> new EntityNotFoundException("존재하지 않는 Dining ID"));
        if(authenticatedEmployee.getDepartment().toString() != dining.getDiningName().toString()){
            throw new IllegalArgumentException("접근 권한이 없습니다.");
        }
        menuRepository.deleteById(menuId);
    }
}
