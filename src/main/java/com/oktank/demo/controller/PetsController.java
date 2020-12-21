/*
 * Copyright 2016 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"). You may not use this file except in compliance
 * with the License. A copy of the License is located at
 *
 * http://aws.amazon.com/apache2.0/
 *
 * or in the "license" file accompanying this file. This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES
 * OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */
package com.oktank.demo.controller;

import com.amazonaws.services.rdsdata.model.ExecuteStatementResult;
import com.oktank.demo.model.Pet;
import com.oktank.demo.model.PetData;

import com.oktank.demo.model.PetImage;
import com.oktank.demo.service.DataService;

import com.oktank.demo.service.S3UploadService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.security.Principal;
import java.util.Optional;
import java.util.UUID;
import java.util.List;

import com.amazonaws.services.rdsdata.model.Field;


@RestController
@EnableWebMvc
public class PetsController {
    @RequestMapping(path = "/pets", method = RequestMethod.POST)
    public ResponseEntity<Pet> createPet(@RequestBody Pet newPet) {
        if (newPet.getName() == null || newPet.getBreed() == null) {
            return null;
        }
        Pet dbPet = newPet;
        dbPet.setId(UUID.randomUUID().toString());

        S3UploadService s3 = S3UploadService.getInstance();

        s3.UploadBase64(dbPet.getPhotoBase64(), dbPet.getId()+".jpg");

        DataService ds = DataService.getInstance();
        ExecuteStatementResult result = ds.Query(String.format("INSERT INTO pets (id, name, breed, photo_url) VALUES (\'%s\', \'%s\', \'%s\', \'%s\')", dbPet.getId(), dbPet.getName(), dbPet.getBreed(), dbPet.getPhotoUrl()));

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Access-Control-Allow-Origin","*");
        responseHeaders.set("Access-Control-Allow-Credentials","true");
        ResponseEntity responseEntity = new ResponseEntity(dbPet,responseHeaders,HttpStatus.OK);
        return responseEntity;

    }

    @RequestMapping(path = "/pets", method = RequestMethod.GET)
    public ResponseEntity<Pet[]> listPets(@RequestParam("limit") Optional<Integer> limit, Principal principal) {
        int queryLimit = 25;
        if (limit.isPresent()) {
            queryLimit = limit.get();
        }

        Pet[] outputPets = new Pet[queryLimit];
        DataService ds = DataService.getInstance();
        ExecuteStatementResult result = ds.Query("select * from pets");
        int i = 0;
        for (List<Field> fields: result.getRecords()) {
            String id = fields.get(0).getStringValue();
            String name = fields.get(1).getStringValue();
            String breed = fields.get(2).getStringValue();
            String photo_url = fields.get(3).getStringValue();

            Pet newPet = new Pet();
            newPet.setId(id);
            newPet.setName(name);
            newPet.setBreed(breed);
            newPet.setPhotoUrl(photo_url);
            outputPets[i] = newPet;
            i++;
            System.out.println(String.format("Fetched row: string = %s", name));
        }

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Access-Control-Allow-Origin","*");
        responseHeaders.set("Access-Control-Allow-Credentials","true");
        ResponseEntity responseEntity = new ResponseEntity(outputPets,responseHeaders,HttpStatus.OK);
        //comment
        //comm
        return responseEntity;

    }

    @RequestMapping(path = "/pets/{petId}", method = RequestMethod.GET)
    public Pet listPets() {
        Pet newPet = new Pet();
        newPet.setId(UUID.randomUUID().toString());
        newPet.setBreed(PetData.getRandomBreed());
        //newPet.setDateOfBirth(PetData.getRandomDoB());
        newPet.setName(PetData.getRandomName());
        return newPet;
    }

    @RequestMapping(path = "/pets/identify", method = RequestMethod.POST)
    public Pet Identify(@RequestBody PetImage image) {

        if(image.getImageData() == null) {
            return null;
        }

        return new Pet();
    }
}

