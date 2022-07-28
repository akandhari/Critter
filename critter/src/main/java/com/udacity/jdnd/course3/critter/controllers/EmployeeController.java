package com.udacity.jdnd.course3.critter.controllers;

import com.udacity.jdnd.course3.critter.dtos.EmployeeDTO;
import com.udacity.jdnd.course3.critter.dtos.EmployeeRequestDTO;
import com.udacity.jdnd.course3.critter.dtos.converters.EmployeeDTOConverter;
import com.udacity.jdnd.course3.critter.entities.users.Employee;
import com.udacity.jdnd.course3.critter.services.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/employee")
public class EmployeeController {

    private final Logger logger = LoggerFactory.getLogger(EmployeeController.class);


    private EmployeeService _employeeService;


    private EmployeeDTOConverter _employeeDtoConverter;

    public EmployeeController(EmployeeService employeeService, EmployeeDTOConverter employeeDTOConverter) {
     _employeeService = employeeService;
       _employeeDtoConverter = employeeDTOConverter;
    }

    @GetMapping
    public List<EmployeeDTO> getAllEmployees() {
        List<Employee> ListEmployee = _employeeService.getAll();

        List<EmployeeDTO> listOfEmployeesDTO;
        listOfEmployeesDTO = ListEmployee.stream()
                .map(_employeeDtoConverter::employeeToDTO)
                .collect(toList());

        return listOfEmployeesDTO;
    }

    @GetMapping("/{empId}")
    public EmployeeDTO getEmployee(@PathVariable long empId) {
        Employee employee = _employeeService.getEmpById(empId);

        EmployeeDTO employeeDTO = _employeeDtoConverter.employeeToDTO(employee);

        return employeeDTO;
    }

    @GetMapping("/availability")
    public List<EmployeeDTO> findEmployeesForService(@RequestBody EmployeeRequestDTO employeeDTO) {
        List<Employee> listOfAvailableEmployees = _employeeService.getAvailableEmployeesForService(employeeDTO.getDate(), employeeDTO.getSkills());

        List<EmployeeDTO> listOfAvailableEmployeesDTO = listOfAvailableEmployees
                .stream()
                .map(_employeeDtoConverter::employeeToDTO)
                .collect(toList());

        return listOfAvailableEmployeesDTO;
    }

    @PostMapping
    public EmployeeDTO saveEmployee(@RequestBody EmployeeDTO employeeDTO) {
        Employee employee = _employeeDtoConverter.dtoToEmployee(employeeDTO);
        Employee savedEmployee = _employeeService.save(employee);

        EmployeeDTO savedEmployeeDTO = _employeeDtoConverter.employeeToDTO(savedEmployee);

        return savedEmployeeDTO;

    }

    @PatchMapping("/{employeeID}")
    public void setAvailability(@RequestBody Set<DayOfWeek> daysAvailable, @PathVariable long employeeID) {
        Employee employee = _employeeService.getEmpById(employeeID);

        employee.setDaysAvailable(daysAvailable);

        _employeeService.save(employee);
    }


    @DeleteMapping("/{employeeID}")
    public void deleteEmployeeByID(@PathVariable Long employeeID) {

        _employeeService.delete(employeeID);
    }

    //Extra endpoints
    @GetMapping("/search/{contains}")
    public List<EmployeeDTO> findEmployeesByPartialName(@PathVariable String contains) {
        List<Employee> employeesByPartialName = _employeeService.getEmployeesByPartialName(contains);

        logger.info("Employees With Name Contains: " + employeesByPartialName);

        List<EmployeeDTO> employeesByPartialName_DTO = employeesByPartialName
                .stream()
                .map(_employeeDtoConverter::employeeToDTO)
                .collect(toList());

        return employeesByPartialName_DTO;
    }
}
