package com.udacity.jdnd.course3.critter;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.udacity.jdnd.course3.critter.controllers.CustomerController;
import com.udacity.jdnd.course3.critter.controllers.EmployeeController;
import com.udacity.jdnd.course3.critter.controllers.PetController;
import com.udacity.jdnd.course3.critter.controllers.ScheduleController;
import com.udacity.jdnd.course3.critter.dtos.converters.EmployeeDTOConverter;
import com.udacity.jdnd.course3.critter.dtos.*;
import com.udacity.jdnd.course3.critter.enums.EmployeeSkill;
import com.udacity.jdnd.course3.critter.enums.PetType;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.*;

/**
 * This is a set of functional tests to validate the basic capabilities desired for this application.
 * Students will need to configure the application to run these tests by adding application.properties file
 * to the test/resources directory that specifies the datasource. It can run using an in-memory H2 instance
 * and should not try to re-use the same datasource used by the rest of the app.
 * <p>
 * These tests should all pass once the project is complete.
 */
@Transactional
@SpringBootTest(classes = CritterApplication.class)
public class CritterFunctionalTest {

    @Autowired
    private PetController petController;

    @Autowired
    private ScheduleController scheduleController;

    @Autowired
    private CustomerController customerController;

    @Autowired
    private EmployeeController employeeController;

    @Autowired
    private EmployeeDTOConverter employeeDTOConverter;

    private final Logger logger = LoggerFactory.getLogger(CritterFunctionalTest.class);

    @Test
    public void testCreateCustomer() {
        CustomerDTO customerDTO = createCustomerDTO();
        CustomerDTO newCustomer = customerController.saveCustomer(customerDTO);
        CustomerDTO retrievedCustomer = customerController.getCustomer(newCustomer.getId());

        assertEquals(newCustomer.getFirstName(), customerDTO.getFirstName());
        assertEquals(newCustomer.getLastName(), customerDTO.getLastName());
        assertEquals(newCustomer.getId(), retrievedCustomer.getId());
        assertTrue(retrievedCustomer.getId() > 0);

        logger.info("Customer ID: " + newCustomer.getId());
    }

    @Test
    public void testCreateEmployee() {

        EmployeeDTO employeeDTO = createEmployeeDTO("EmployeeCreate", "Test");
        EmployeeDTO newEmployee = employeeController.saveEmployee(employeeDTO);
        EmployeeDTO retrievedEmployee = employeeController.getEmployee(newEmployee.getId());

        assertEquals(employeeDTO.getSkills(), newEmployee.getSkills());
        assertEquals(employeeDTO.getFirstName(), newEmployee.getFirstName());
        assertEquals(employeeDTO.getLastName(), newEmployee.getLastName());
        assertEquals(newEmployee.getId(), retrievedEmployee.getId());
        assertTrue(retrievedEmployee.getId() > 0);

    }

    //Test that creates a pet and associates it with an owner
    @Test
    public void testAddPetsToCustomer() {
        CustomerDTO customerDTO = createCustomerDTO();
        CustomerDTO newCustomer = customerController.saveCustomer(customerDTO);

        PetDTO petDTO = createPetDTO();
        petDTO.setOwnerId(newCustomer.getId());
        PetDTO newPet = petController.savePet(petDTO);

        //make sure pet contains customer id
        PetDTO retrievedPet = petController.getPet(newPet.getId());
        assertEquals(retrievedPet.getId(), newPet.getId());
        assertEquals(retrievedPet.getOwnerId(), newCustomer.getId());

        //make sure you can retrieve pets by owner
        List<PetDTO> pets = petController.getPetsByOwner(newCustomer.getId());
        assertEquals(newPet.getId(), pets.get(0).getId());
        assertEquals(newPet.getName(), pets.get(0).getName());

        //check to make sure customer now also contains pet
        CustomerDTO retrievedCustomer = customerController.getCustomer(newCustomer.getId());
        assertTrue(retrievedCustomer.getPetIds() != null && retrievedCustomer.getPetIds().size() > 0);
        assertEquals(retrievedCustomer.getPetIds().get(0), retrievedPet.getId());

    }

    @Test
    public void testFindPetsByOwner() {
        CustomerDTO customerDTO = createCustomerDTO();
        CustomerDTO newCustomer = customerController.saveCustomer(customerDTO);

        PetDTO petDTO = createPetDTO();
        petDTO.setOwnerId(newCustomer.getId());
        PetDTO newPet = petController.savePet(petDTO);
        petDTO.setType(PetType.DOG);
        petDTO.setName("DogName");

        List<PetDTO> pets = petController.getPetsByOwner(newCustomer.getId());
        assertEquals(pets.size(), 1);
        assertEquals(pets.get(0).getOwnerId(), newCustomer.getId());
        assertEquals(pets.get(0).getId(), newPet.getId());
    }

    @Test
    public void testFindOwnerByPet() {
        CustomerDTO customerDTO = createCustomerDTO();
        CustomerDTO newCustomer = customerController.saveCustomer(customerDTO);

        PetDTO petDTO = createPetDTO();
        petDTO.setOwnerId(newCustomer.getId());
        PetDTO newPet = petController.savePet(petDTO);

        CustomerDTO owner = customerController.getOwnerByPet(newPet.getId());
        assertEquals(owner.getId(), newCustomer.getId());
        assertEquals(owner.getPetIds().get(0), newPet.getId());
    }

    @Test
    public void testChangeEmployeeAvailability() {
        EmployeeDTO employeeDTO = createEmployeeDTO("EmpAvailability", "ChangeTest");
        EmployeeDTO emp1 = employeeController.saveEmployee(employeeDTO);
        assertNull(emp1.getDaysAvailable());

        Set<DayOfWeek> availability = Sets.newHashSet(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY);
        employeeController.setAvailability(availability, emp1.getId());

        EmployeeDTO emp2 = employeeController.getEmployee(emp1.getId());
        assertEquals(availability, emp2.getDaysAvailable());
    }

    @Test
    public void testFindEmployeesByServiceAndTime() {
        EmployeeDTO emp1 = createEmployeeDTO("Employee1", "Test1FindEmpByServiceAndTime");
        EmployeeDTO emp2 = createEmployeeDTO("Employee2", "Test2FindEmpByServiceAndTime");
        EmployeeDTO emp3 = createEmployeeDTO("Employee3", "Test3FindEmpByServiceAndTime");

        emp1.setDaysAvailable(Sets.newHashSet(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY));
        emp2.setDaysAvailable(Sets.newHashSet(DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY));
        emp3.setDaysAvailable(Sets.newHashSet(DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY));

        emp1.setSkills(Sets.newHashSet(EmployeeSkill.FEEDING, EmployeeSkill.PETTING));
        emp2.setSkills(Sets.newHashSet(EmployeeSkill.PETTING, EmployeeSkill.WALKING));
        emp3.setSkills(Sets.newHashSet(EmployeeSkill.WALKING, EmployeeSkill.SHAVING));

        EmployeeDTO emp1n = employeeController.saveEmployee(emp1);
        EmployeeDTO emp2n = employeeController.saveEmployee(emp2);
        EmployeeDTO emp3n = employeeController.saveEmployee(emp3);

        EmployeeRequestDTO   er1 = createEmployeeRequestDTO(2019,12,25,Sets.newHashSet(EmployeeSkill.PETTING)); //Wednesday

        Set<Long> eIds1 = employeeController.findEmployeesForService(er1).stream().map(EmployeeDTO::getId).collect(Collectors.toSet());
        Set<Long> eIds1expected = Sets.newHashSet(emp1n.getId(), emp2n.getId());
        assertEquals(eIds1, eIds1expected);

        EmployeeRequestDTO   er2 = createEmployeeRequestDTO(2019,12,27,Sets.newHashSet(EmployeeSkill.WALKING, EmployeeSkill.SHAVING)); //Friday
        Set<Long> eIds2 = employeeController.findEmployeesForService(er2).stream().map(EmployeeDTO::getId).collect(Collectors.toSet());
        Set<Long> eIds2expected = Sets.newHashSet(emp3n.getId());
        assertEquals(eIds2, eIds2expected);
    }

    @Test
    public void testSchedulePetsForServiceWithEmployee() {
        EmployeeDTO employeeTemp = createEmployeeDTO("empPet1", "TestSchedulePetWithEmployee");
        employeeTemp.setDaysAvailable(Sets.newHashSet(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY));
        EmployeeDTO employeeDTO = employeeController.saveEmployee(employeeTemp);
        CustomerDTO customerDTO = customerController.saveCustomer(createCustomerDTO());
        PetDTO petTemp = createPetDTO();
        petTemp.setOwnerId(customerDTO.getId());
        PetDTO petDTO = petController.savePet(petTemp);

        LocalDate date = LocalDate.of(2019, 12, 25);
        List<Long> petList = Lists.newArrayList(petDTO.getId());
        List<Long> employeeList = Lists.newArrayList(employeeDTO.getId());
        Set<EmployeeSkill> skillSet = Sets.newHashSet(EmployeeSkill.PETTING);

        ScheduleDTO newSchedule = scheduleController.createSchedule(createScheduleDTO(petList, employeeList, date, skillSet));

        long newScheduleID = newSchedule.getId();
        ScheduleDTO scheduleDTO = scheduleController.getScheduleByID(newScheduleID);

        assertEquals(scheduleDTO.getActivities(), skillSet);
        assertEquals(scheduleDTO.getDate(), date);
        assertEquals(scheduleDTO.getEmployeeIds(), employeeList);
        assertEquals(scheduleDTO.getPetIds(), petList);
    }

    @Test
    public void testFindScheduleByEntities() {
        ScheduleDTO sched1 = populateSchedule(1, 2, LocalDate.of(2019, 12, 25), Sets.newHashSet(EmployeeSkill.FEEDING, EmployeeSkill.WALKING));
        ScheduleDTO sched2 = populateSchedule(3, 1, LocalDate.of(2019, 12, 26), Sets.newHashSet(EmployeeSkill.PETTING));

        //add a third schedule that shares some employees and pets with the other schedules
        ScheduleDTO sched3 = new ScheduleDTO();
        sched3.setEmployeeIds(sched1.getEmployeeIds());
        sched3.setPetIds(sched2.getPetIds());
        sched3.setActivities(Sets.newHashSet(EmployeeSkill.SHAVING, EmployeeSkill.PETTING));
        sched3.setDate(LocalDate.of(2020, 3, 23));
        scheduleController.createSchedule(sched3);

        /*
            We now have 3 schedule entries. The third schedule entry has the same employees as the 1st schedule
            and the same pets/owners as the second schedule. So if we look up schedule entries for the employee from
            schedule 1, we should get both the first and third schedule as our result.
         */

        //Employee 1 in is both schedule 1 and 3
        List<ScheduleDTO> scheds1e = scheduleController.getSchedulesForEmployee(sched1.getEmployeeIds().get(0));

        compareSchedules(sched1, scheds1e.get(0));
        //compareSchedules(sched2, scheds1e.get(0));
        compareSchedules(sched3, scheds1e.get(1));

        //Employee 2 is only in schedule 2
        List<ScheduleDTO> scheds2e = scheduleController.getSchedulesForEmployee(sched2.getEmployeeIds().get(0));
        compareSchedules(sched2, scheds2e.get(0));

        //Pet 1 is only in schedule 1
        List<ScheduleDTO> scheds1p = scheduleController.getSchedulesForPet(sched1.getPetIds().get(0));
        compareSchedules(sched1, scheds1p.get(0));

        //Pet from schedule 2 is in both schedules 2 and 3
        List<ScheduleDTO> scheds2p = scheduleController.getSchedulesForPet(sched2.getPetIds().get(0));
        compareSchedules(sched2, scheds2p.get(0));
        compareSchedules(sched3, scheds2p.get(1));

        //Owner of the first pet will only be in schedule 1
        List<ScheduleDTO> scheds1c = scheduleController.getSchedulesForCustomer(customerController.getOwnerByPet(sched1.getPetIds().get(0)).getId());
        compareSchedules(sched1, scheds1c.get(0));

        //Owner of pet from schedule 2 will be in both schedules 2 and 3
        List<ScheduleDTO> scheds2c = scheduleController.getSchedulesForCustomer(customerController.getOwnerByPet(sched2.getPetIds().get(0)).getId());
        compareSchedules(sched2, scheds2c.get(0));
        compareSchedules(sched3, scheds2c.get(1));

        logger.info("schedsle1: " + scheds1e);
        logger.info("schedsle2: " + scheds2e);
        //logger.info("schedsle3: " + scheds3e);
    }

    @Test
    public void testFilterEmployeesByPartialName() {

        //Sample EmployeeDTO instances
        EmployeeDTO employee1DTO = createEmployeeDTO("Emp1PartTest", "LnameTest");
        EmployeeDTO employee2DTO = createEmployeeDTO("Emp2Test", "LnamePartTest");
        EmployeeDTO employee3DTO = createEmployeeDTO("Emp3Test", "LnameTest");
        EmployeeDTO employee4DTO = createEmployeeDTO("PartEmp4Test", "LnameTestPart");
        EmployeeDTO employee5DTO = createEmployeeDTO("Emp5Test", "LnameTest");
        employee1DTO.setDaysAvailable(Sets.newHashSet(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY));
        employee2DTO.setDaysAvailable(Sets.newHashSet(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY));
        employee3DTO.setDaysAvailable(Sets.newHashSet(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY));
        employee4DTO.setDaysAvailable(Sets.newHashSet(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY));
        employee5DTO.setDaysAvailable(Sets.newHashSet(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY));

        EmployeeDTO emp1n = employeeController.saveEmployee(employee1DTO);
        EmployeeDTO emp2n = employeeController.saveEmployee(employee2DTO);
        EmployeeDTO emp3n = employeeController.saveEmployee(employee3DTO);
        EmployeeDTO emp4n = employeeController.saveEmployee(employee4DTO);
        EmployeeDTO emp5n = employeeController.saveEmployee(employee5DTO);

        List<EmployeeDTO> employeeListByPartialName = employeeController.findEmployeesByPartialName("part");

        assertTrue(containsEmployeeWithID(employeeListByPartialName, emp1n.getId()));
        assertTrue(containsEmployeeWithID(employeeListByPartialName, emp2n.getId()));
        assertFalse(containsEmployeeWithID(employeeListByPartialName, emp3n.getId()));
        assertTrue(containsEmployeeWithID(employeeListByPartialName, emp4n.getId()));
        assertFalse(containsEmployeeWithID(employeeListByPartialName, emp5n.getId()));
    }

//    HELPER METHODS FOR Unit Test Data Creation.

    private static EmployeeDTO createEmployeeDTO(String fName, String lName) {
        EmployeeDTO employeeDTO = new EmployeeDTO();
        employeeDTO.setFirstName(fName);
        employeeDTO.setLastName(lName);
        employeeDTO.setSkills(Sets.newHashSet(EmployeeSkill.FEEDING, EmployeeSkill.PETTING));
        return employeeDTO;
    }

    private static CustomerDTO createCustomerDTO() {
        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setFirstName("Test");
        customerDTO.setLastName("Employee");
        customerDTO.setPhoneNumber("123-456-789");
        return customerDTO;
    }

    private static PetDTO createPetDTO() {
        PetDTO petDTO = new PetDTO();
        petDTO.setName("TestPet");
        petDTO.setType(PetType.CAT);

        return petDTO;
    }

    private static EmployeeRequestDTO createEmployeeRequestDTO(int year,int month,int dayOfMonth,Set<EmployeeSkill> employeeSkills) {
        EmployeeRequestDTO employeeRequestDTO = new EmployeeRequestDTO();
        employeeRequestDTO.setDate(LocalDate.of(year, month, dayOfMonth));
        employeeRequestDTO.setSkills(employeeSkills);
        return employeeRequestDTO;
    }

    private static ScheduleDTO createScheduleDTO(List<Long> petIds, List<Long> employeeIds, LocalDate date, Set<EmployeeSkill> activities) {
        ScheduleDTO scheduleDTO = new ScheduleDTO();
        scheduleDTO.setPetIds(petIds);
        scheduleDTO.setEmployeeIds(employeeIds);
        scheduleDTO.setDate(date);
        scheduleDTO.setActivities(activities);
        return scheduleDTO;
    }
    private ScheduleDTO populateSchedule(int numEmployees, int numPets, LocalDate date, Set<EmployeeSkill> activities) {
        List<Long> employeeIDs =
                IntStream
                        .range(0, numEmployees)
                        .mapToObj(i -> createEmployeeDTO("Test", "Employee"))
                        .map(e -> {
                            e.setSkills(activities);
                            e.setDaysAvailable(Sets.newHashSet(date.getDayOfWeek()));
                            EmployeeDTO employee = employeeController.saveEmployee(e);
                            long employeeID = employee.getId();

                            return employeeID;
                        })
                        .collect(toList());
        CustomerDTO cust = customerController.saveCustomer(createCustomerDTO());

        long customerID = cust.getId();

        List<Long> petIDs =
                IntStream
                        .range(0, numPets)
                        .mapToObj(i -> createPetDTO())
                        .map(p -> {
                            p.setOwnerId(customerID);
                            PetDTO pet = petController.savePet(p);
                            long petID = pet.getId();

                            return petID;

                        })
                        .collect(toList());

        return scheduleController.createSchedule(createScheduleDTO(petIDs, employeeIDs, date, activities));
    }

    private static void compareSchedules(ScheduleDTO sched1, ScheduleDTO sched2) {
        assertEquals(sched1.getPetIds(), sched2.getPetIds());
        assertEquals(sched1.getActivities(), sched2.getActivities());
        assertEquals(sched1.getEmployeeIds(), sched2.getEmployeeIds());
        assertEquals(sched1.getDate(), sched2.getDate());
    }

    private static boolean containsEmployeeWithID(final List<EmployeeDTO> empList, final Long empID) {
        return empList.stream().map(EmployeeDTO::getId).filter(empID::equals).findFirst().isPresent();
    }


}
