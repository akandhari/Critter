package com.udacity.jdnd.course3.critter.controllers;

import com.udacity.jdnd.course3.critter.dtos.converters.ScheduleDTOConverter;
import com.udacity.jdnd.course3.critter.dtos.ScheduleDTO;
import com.udacity.jdnd.course3.critter.entities.Pet;
import com.udacity.jdnd.course3.critter.entities.Schedule;
import com.udacity.jdnd.course3.critter.entities.users.Customer;
import com.udacity.jdnd.course3.critter.entities.users.Employee;
import com.udacity.jdnd.course3.critter.services.CustomerService;
import com.udacity.jdnd.course3.critter.services.EmployeeService;
import com.udacity.jdnd.course3.critter.services.PetService;
import com.udacity.jdnd.course3.critter.services.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Handles web requests related to Schedules.
 */
@RestController
@RequestMapping("/schedule")
public class ScheduleController {

    @Autowired
    private ScheduleDTOConverter _scheduleDTOConverter;
    @Autowired
    private EmployeeService _employeeService;
    @Autowired
    private PetService _petService;
    @Autowired
    private ScheduleService _scheduleService;
    @Autowired
    private CustomerService _customerService;

    @GetMapping
    public List<ScheduleDTO> getAllSchedule() {
        return _scheduleService.getAll()
                .stream()
                .map(_scheduleDTOConverter::scheduleToDto)
                .collect(toList());
    }

    @GetMapping("/{scheduleId}")
    public ScheduleDTO getScheduleByID(@PathVariable Long scheduleId) {
        Schedule schedule = _scheduleService.getByID(scheduleId);

        ScheduleDTO scheduleDTO = _scheduleDTOConverter.scheduleToDto(schedule);

        return scheduleDTO;
    }

    @GetMapping("/pet/{petId}")
    public List<ScheduleDTO> getSchedulesForPet(@PathVariable long petId) {
        Pet pet = _petService.getPetById(petId);
        return _scheduleService.getScheduleByPet(pet)
                .stream()
                .map(_scheduleDTOConverter::scheduleToDto)
                .collect(toList());
    }

    @GetMapping("/employee/{empId}")
    public List<ScheduleDTO> getSchedulesForEmployee(@PathVariable long empId) {
        Employee employee = _employeeService.getEmpById(empId);
        return _scheduleService.getScheduleByEmp(employee)
                .stream()
                .map(_scheduleDTOConverter::scheduleToDto)
                .collect(toList());
    }

    @GetMapping("/customer/{custId}")
    public List<ScheduleDTO> getSchedulesForCustomer(@PathVariable long custId) {
        Customer customer = _customerService.getById(custId);
        return _scheduleService.getScheduleByCustomer(customer)
                .stream()
                .map(_scheduleDTOConverter::scheduleToDto)
                .collect(toList());
    }

    @PostMapping
    public ScheduleDTO createSchedule(@RequestBody ScheduleDTO scheduleDTO) {
        Schedule schedule = _scheduleDTOConverter.dtoToSchedule(scheduleDTO);

        return _scheduleDTOConverter.scheduleToDto(_scheduleService.saveSchedule(schedule));
    }
}
