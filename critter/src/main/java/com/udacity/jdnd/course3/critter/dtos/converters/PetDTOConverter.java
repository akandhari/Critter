package com.udacity.jdnd.course3.critter.dtos.converters;

import com.udacity.jdnd.course3.critter.dtos.PetDTO;
import com.udacity.jdnd.course3.critter.entities.Pet;
import com.udacity.jdnd.course3.critter.entities.users.Customer;
import com.udacity.jdnd.course3.critter.services.CustomerService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PetDTOConverter {

    @Autowired
    private CustomerService _customerService;

    public PetDTO petToDTO(Pet pet){
        PetDTO petDTO = new PetDTO();
        Long customerID = pet.getCustomer().getId();

        BeanUtils.copyProperties(pet, petDTO, "customer", "petType");

        //Adds the customer to the PetDTO instance
        Customer customer = _customerService.getById(customerID);
        petDTO.setOwnerId(customer.getId());
        petDTO.setType(pet.getPetType());

        return petDTO;
    }

    public Pet dtoToPet(PetDTO petDTO){
        Pet pet = new Pet();
        Long customerID = petDTO.getOwnerId();

        BeanUtils.copyProperties(petDTO, pet, "ownerId", "type");

        //Adds the customer to the Pet instance
        Customer customer = _customerService.getById(customerID);
        pet.setCustomer(customer);
        pet.setPetType(petDTO.getType());

        return pet;
    }
}
