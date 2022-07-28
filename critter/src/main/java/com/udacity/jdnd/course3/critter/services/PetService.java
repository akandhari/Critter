package com.udacity.jdnd.course3.critter.services;

import com.udacity.jdnd.course3.critter.entities.Pet;
import com.udacity.jdnd.course3.critter.entities.users.Customer;
import com.udacity.jdnd.course3.critter.repositories.PetRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class PetService {


    private PetRepository _petRepository;
    private CustomerService _customerService;

    public PetService(PetRepository petRepository, CustomerService customerService) {
        _petRepository = petRepository;
        _customerService = customerService;
    }

    public List<Pet> getAllPets() {
        return _petRepository.findAll();
    }

    public Pet getPetById(Long petId) {
        return _petRepository.getById(petId);
    }

    public List<Pet> getCustomerPets(Long custId) {
        return _petRepository.getAllPetsByCustomerId(custId);
    }
    public Pet savePet(Pet pet) {
        Pet savedPet = _petRepository.save(pet);
        Customer customer = savedPet.getCustomer();
        _customerService.mapCustomerAndPet(savedPet, customer);
        return savedPet;
    }

    public void deletePet(Long petId) {
        Pet pet = _petRepository.getById(petId);
        _petRepository.delete(pet);
    }

}
