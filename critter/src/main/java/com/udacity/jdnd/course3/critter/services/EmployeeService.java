package com.udacity.jdnd.course3.critter.services;

import com.udacity.jdnd.course3.critter.entities.users.Employee;
import com.udacity.jdnd.course3.critter.enums.EmployeeSkill;
import com.udacity.jdnd.course3.critter.repositories.EmployeeRepository;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;

@Service
@Transactional
public class EmployeeService {

    private final Logger logger = LoggerFactory.getLogger(EmployeeService.class);

    @Autowired
    private EmployeeRepository _employeeRepository;

    public Employee save(Employee employee){

        Employee newEmployee = _employeeRepository.save(employee);

        return newEmployee;
    }

    public List<Employee> getAll(){
        return _employeeRepository.findAll();
    }

    public Employee getEmpById(Long empId){
        return _employeeRepository.getById(empId);
    }

    public void delete(Long empId){
        Employee employee = _employeeRepository.getById(empId);
        _employeeRepository.delete(employee);
    }

    public List<Employee> getAvailableEmployeesForService(LocalDate date, Set<EmployeeSkill> skills){
        DayOfWeek dayOfWeek = date.getDayOfWeek();

        List<Employee> availableEmployeesByDOW = _employeeRepository.findEmployeeByDaysAvailable(dayOfWeek);

        List<Employee> filteredEmployeesBySkills = availableEmployeesByDOW.stream()
                .filter(e -> e.getSkills().containsAll(skills))
                .collect(toList());

        logger.info("Employees By Skills (Filtered List): " + filteredEmployeesBySkills);

        return filteredEmployeesBySkills;
    }
    public List<Employee> getEmployeesByPartialName(String nameContains) {
        List<Employee> allEmployees = _employeeRepository.findAll();
        List<Employee> filteredEmployees = allEmployees
                .stream()
                .filter(emp -> StringUtils.containsIgnoreCase(emp.getFirstName(),nameContains) || StringUtils.containsIgnoreCase(emp.getLastName(),nameContains))
                .collect(toList());

        logger.info("Filtered Employees: " + filteredEmployees + " By FirstName OR LastName Contains : " + nameContains);

        return filteredEmployees;
    }

}
