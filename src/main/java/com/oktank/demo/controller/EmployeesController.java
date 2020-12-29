package com.oktank.demo.controller;

import com.amazonaws.services.rdsdata.model.ExecuteStatementResult;
import com.amazonaws.services.rdsdata.model.Field;
import com.oktank.demo.model.Employee;
import com.oktank.demo.service.DataService;
import com.oktank.demo.service.S3UploadService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@EnableWebMvc
public class EmployeesController {
    @RequestMapping(path = "/employees", method = RequestMethod.POST)
    public ResponseEntity<Employee> createEmployee(@RequestBody Employee newEmployee) {
        if (newEmployee.getName() == null || newEmployee.getDepartment() == null) {
            return null;
        }
        Employee dbEmployee = newEmployee;
        dbEmployee.setId(UUID.randomUUID().toString());

        S3UploadService s3 = S3UploadService.getInstance();

        s3.UploadBase64(dbEmployee.getPhotoBase64(), dbEmployee.getId()+".jpg");

        DataService ds = DataService.getInstance();
        ExecuteStatementResult result = ds.Query(String.format("INSERT INTO employees (id, name, department, photo_key, email, verified) VALUES (\'%s\', \'%s\', \'%s\', \'%s\')", dbEmployee.getId(), dbEmployee.getName(), dbEmployee.getDepartment(), dbEmployee.getId()+".jpg", dbEmployee.getEmail(), dbEmployee.getVerified()));

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Access-Control-Allow-Origin","*");
        responseHeaders.set("Access-Control-Allow-Credentials","true");
        ResponseEntity responseEntity = new ResponseEntity(dbEmployee,responseHeaders, HttpStatus.OK);
        return responseEntity;

    }

    @RequestMapping(path = "/employees", method = RequestMethod.GET)
    public ResponseEntity<Employee[]> listPets(@RequestParam("limit") Optional<Integer> limit, Principal principal) {
        int queryLimit = 25;
        if (limit.isPresent()) {
            queryLimit = limit.get();
        }

        Employee[] outputEmployees = new Employee[queryLimit];
        DataService ds = DataService.getInstance();
        ExecuteStatementResult result = ds.Query("select * from employees");
        int i = 0;
        for (List<Field> fields: result.getRecords()) {
            String id = fields.get(0).getStringValue();
            String name = fields.get(1).getStringValue();
            String department = fields.get(2).getStringValue();
            String email = fields.get(3).getStringValue();
            String id_photo = fields.get(4).getStringValue();
            Boolean verified = fields.get(5).getBooleanValue();

            Employee newEmployee = new Employee();
            newEmployee.setId(id);
            newEmployee.setName(name);
            newEmployee.setDepartment(department);
            newEmployee.setEmail(email);
            newEmployee.setIdPhoto(id_photo);
            newEmployee.setVerified(verified);
            outputEmployees[i] = newEmployee;
            i++;
            System.out.println(String.format("Fetched row: string = %s", name));
        }

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Access-Control-Allow-Origin","*");
        responseHeaders.set("Access-Control-Allow-Credentials","true");
        ResponseEntity responseEntity = new ResponseEntity(outputEmployees,responseHeaders,HttpStatus.OK);
        return responseEntity;

    }

    /*@RequestMapping(path = "/employees/{petId}", method = RequestMethod.GET)
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
    }*/

    /*
    CREATE TABLE employees (id UUID PRIMARY KEY NOT NULL, name TEXT NOT NULL, department TEXT NOT NULL, email TEXT NOT NULL, photo_key TEXT NOT NULL, verified BOOLEAN NOT NULL);
     */
}
