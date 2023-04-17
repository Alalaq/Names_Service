package ru.naumen.khabibullin.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.naumen.khabibullin.service.PersonsService;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class PersonsController {

    @Autowired
    private PersonsService personsService;

    @GetMapping("/name")
    public int getAgeByName(@RequestParam("name") String name) {
        return personsService.getAge(name);
    }

    @GetMapping("/frequency")
    public Map<String, Integer> getNameFrequency() {
        return personsService.getNameFrequency();
    }

    @GetMapping("/highest-age")
    public String getNameWithHighestAge() {
        return personsService.getNameWithHighestAge();
    }

}
