package com.udacity.jdnd.course3.critter.services;

import com.udacity.jdnd.course3.critter.entities.Pet;
import com.udacity.jdnd.course3.critter.entities.users.Customer;
import com.udacity.jdnd.course3.critter.repositories.CustomerRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@Transactional
public class CustomerService {

    private CustomerRepository _customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        _customerRepository = customerRepository;
    }

    public List<Customer> getAll() {
        return _customerRepository.findAll();
    }

    public Customer getById(Long custId) {
        return _customerRepository.getById(custId);
    }

    public Customer getByPet(Pet pet) {
        return _customerRepository.getCustomerByPets(pet);
    }

    public Customer save(Customer customer) {
        return _customerRepository.save(customer);
    }

    public void delete(Long custId) {
        Customer customer = _customerRepository.getById(custId);
        _customerRepository.delete(customer);
    }

    public void mapCustomerAndPet(Pet pet, Customer customer) {
        customer.addPet(pet);
        _customerRepository.save(customer);
    }

    public List<Long> getCustomerPets(Customer customer) {
        List<Long> petIDs = customer.getPets() != null ? customer.getPets().stream().map(Pet::getId).collect(toList()) : new ArrayList<>();
        return petIDs;
    }

}
