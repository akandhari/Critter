package com.udacity.jdnd.course3.critter.dtos.converters;

import com.udacity.jdnd.course3.critter.dtos.ScheduleDTO;
import com.udacity.jdnd.course3.critter.entities.Pet;
import com.udacity.jdnd.course3.critter.entities.Schedule;
import com.udacity.jdnd.course3.critter.entities.users.Employee;
import com.udacity.jdnd.course3.critter.repositories.EmployeeRepository;
import com.udacity.jdnd.course3.critter.repositories.PetRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Component
public class ScheduleDTOConverter {

    @Autowired
    private EmployeeRepository _employeeRepository;

    @Autowired
    private PetRepository _petRepository;



    public ScheduleDTO scheduleToDto(Schedule schedule){
        ScheduleDTO scheduleDTO = new ScheduleDTO();
        BeanUtils.copyProperties(schedule, scheduleDTO);

        List<Long> listOfEmployeeIDs = schedule.getEmployees()
                .stream()
                .map(Employee::getId)
                .collect(toList());

        List<Long> listOfPetIDs = schedule.getPets()
                .stream()
                .map(Pet::getId)
                .collect(toList());

        scheduleDTO.setEmployeeIds(listOfEmployeeIDs);
        scheduleDTO.setPetIds(listOfPetIDs);

        return scheduleDTO;
    }

    public Schedule dtoToSchedule(ScheduleDTO scheduleDTO){
        Schedule schedule = new Schedule();

        BeanUtils.copyProperties(scheduleDTO, schedule);
        schedule.setEmployees(_employeeRepository.findAllById(scheduleDTO.getEmployeeIds()));
        schedule.setPets(_petRepository.findAllById(scheduleDTO.getPetIds()));

        return schedule;
    }

}
