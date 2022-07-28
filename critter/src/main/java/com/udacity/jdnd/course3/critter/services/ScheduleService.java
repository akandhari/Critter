package com.udacity.jdnd.course3.critter.services;

import com.udacity.jdnd.course3.critter.entities.Pet;
import com.udacity.jdnd.course3.critter.entities.Schedule;
import com.udacity.jdnd.course3.critter.entities.users.Customer;
import com.udacity.jdnd.course3.critter.entities.users.Employee;
import com.udacity.jdnd.course3.critter.repositories.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class ScheduleService {

    @Autowired
    private ScheduleRepository _scheduleRepository;

    public List<Schedule> getAll(){
        return _scheduleRepository.findAll();
    }

    public Schedule getByID(Long scheduleId){
        return _scheduleRepository.getById(scheduleId);
    }

    public List<Schedule> getScheduleByPet(Pet pet){
        return _scheduleRepository.getScheduleByPets(pet);
    }

    public List<Schedule> getScheduleByEmp(Employee employee){
        return _scheduleRepository.getScheduleByEmployees(employee);
    }

    public List<Schedule> getScheduleByCustomer(Customer customer){
        List<Pet> pets = customer.getPets();
        List<Schedule> schedules = new ArrayList<>();
        pets.stream().forEach(pet -> schedules.addAll(_scheduleRepository.getScheduleByPets(pet)));
        return schedules;
    }

    public Schedule saveSchedule(Schedule schedule){
        Schedule newSchedule = _scheduleRepository.save(schedule);

        return newSchedule;
    }
}
