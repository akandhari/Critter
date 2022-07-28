package com.udacity.jdnd.course3.critter.controllers;

import com.udacity.jdnd.course3.critter.dtos.CustomerDTO;
import com.udacity.jdnd.course3.critter.dtos.converters.CustomerDTOConverter;
import com.udacity.jdnd.course3.critter.entities.Pet;
import com.udacity.jdnd.course3.critter.entities.users.Customer;
import com.udacity.jdnd.course3.critter.services.CustomerService;
import com.udacity.jdnd.course3.critter.services.PetService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/customer")
public class CustomerController {

    private CustomerService _customerService;
    private PetService _petService;
    private CustomerDTOConverter _customerDtoConverter;

    public CustomerController(CustomerService customerService, CustomerDTOConverter customerDtoConverter, PetService petService) {
        _customerService = customerService;
        _customerDtoConverter = customerDtoConverter;
        _petService = petService;
    }

    @GetMapping
    public List<CustomerDTO> getAllCustomers() {
        List<Customer> customerList = _customerService.getAll();
        return customerList.stream()
                .map(_customerDtoConverter::customerToDto)
                .collect(toList());
    }
    @GetMapping("/{id}")
    public CustomerDTO getCustomer(@PathVariable long id) {
        Customer customer = _customerService.getById(id);
        return _customerDtoConverter.customerToDto(customer);
    }

    @GetMapping("/pet/{petId}")
    public CustomerDTO getOwnerByPet(@PathVariable long petId) {
        Pet pet = _petService.getPetById(petId);
        Customer customerByPet = _customerService.getByPet(pet);
        return _customerDtoConverter.customerToDto(customerByPet);
    }

    @PostMapping
    public CustomerDTO saveCustomer(@RequestBody CustomerDTO customerDto) {
        Customer customer = _customerDtoConverter.dtoToCustomer(customerDto);
        Customer savedCustomer = _customerService.save(customer);
        return _customerDtoConverter.customerToDto(savedCustomer);
    }

    @DeleteMapping("/{custId}")
    public void deleteCustomer(@PathVariable Long custId) {
        _customerService.delete(custId);
    }

    //Additional Endpoints
    @PutMapping("/{custId}")
    public void update(@RequestBody Customer updateCustomer, @PathVariable Long custId) {

        Customer customer = _customerService.getById(custId);

        customer.setFirstName(updateCustomer.getFirstName());
        customer.setLastName(updateCustomer.getLastName());
        customer.setPhoneNumber(updateCustomer.getPhoneNumber());
        customer.setNotes(updateCustomer.getNotes());
        _customerService.save(customer);

    }


}
