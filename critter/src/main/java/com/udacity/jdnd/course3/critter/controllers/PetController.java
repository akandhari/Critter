package com.udacity.jdnd.course3.critter.controllers;

import com.udacity.jdnd.course3.critter.dtos.converters.PetDTOConverter;
import com.udacity.jdnd.course3.critter.dtos.PetDTO;
import com.udacity.jdnd.course3.critter.entities.Pet;
import com.udacity.jdnd.course3.critter.services.PetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Handles web requests related to Pets.
 */
@RestController
@RequestMapping("/pet")
public class PetController {

    @Autowired
    private PetService _petService;
    @Autowired
    private PetDTOConverter _petDTOConverter;
    @PostMapping
    public PetDTO savePet(@RequestBody PetDTO petDTO) {

        Pet pet = _petDTOConverter.dtoToPet(petDTO);

        Pet savedPet = _petService.savePet(pet);

        PetDTO savedPetDTO = _petDTOConverter.petToDTO(savedPet);
        savedPetDTO.setType(savedPetDTO.getType());

        return savedPetDTO;
    }

    @GetMapping("/{petID}")
    public PetDTO getPet(@PathVariable long petID) {
        Pet pet = _petService.getPetById(petID);

        PetDTO petDTO = _petDTOConverter.petToDTO(pet);

        return petDTO;
    }

    @GetMapping
    public List<PetDTO> getAllPets(){
        List<Pet> listOfAllPets = _petService.getAllPets();

        List<PetDTO> listOfAllPetsDTO = listOfAllPets.stream()
                .map(_petDTOConverter::petToDTO)
                .collect(toList());

        return listOfAllPetsDTO;
    }

    @GetMapping("/owner/{custId}")
    public List<PetDTO> getPetsByOwner(@PathVariable Long custId) {
        List<Pet> listOfPetsByCustomerID = _petService.getCustomerPets(custId);

        List<PetDTO> listOfPetsByCustomerID_DTO = listOfPetsByCustomerID.stream()
                .map(_petDTOConverter::petToDTO)
                .collect(toList());

        return listOfPetsByCustomerID_DTO;
    }

    @DeleteMapping("/{petID}")
    public void deletePetByID(@PathVariable Long petID){

        Pet pet = _petService.getPetById(petID);

        _petService.deletePet(petID);


    }


}
