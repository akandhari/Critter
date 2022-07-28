package com.udacity.jdnd.course3.critter.dtos.converters;

import com.udacity.jdnd.course3.critter.dtos.CustomerDTO;
import com.udacity.jdnd.course3.critter.entities.users.Customer;
import com.udacity.jdnd.course3.critter.services.CustomerService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CustomerDTOConverter {
    private CustomerService _customerService;

    public CustomerDTOConverter(CustomerService customerService) {
        _customerService = customerService;
    }

    public CustomerDTO customerToDto(Customer customer){
        CustomerDTO customerDTO = new CustomerDTO();
        BeanUtils.copyProperties(customer, customerDTO);
        List<Long> petIds = _customerService.getCustomerPets(customer);
        customerDTO.setPetIds(petIds);
        return customerDTO;
    }

    public Customer dtoToCustomer(CustomerDTO customerDTO) {
        Customer customer = new Customer();
        BeanUtils.copyProperties(customerDTO, customer);
        return customer;

    }

}
