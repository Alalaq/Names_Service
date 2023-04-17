package ru.naumen.khabibullin.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.naumen.khabibullin.models.Person;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

@Service
public class PersonsService {
    //Integer represents how many times this exact name is in file
    private Map<Person, Integer> personsMap;

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private ObjectMapper objectMapper;

    @PostConstruct
    public void init() throws IOException {
        personsMap = new HashMap<>();
        ClassPathResource resource = new ClassPathResource("names.txt");
        BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split("_");
            Person person = containsName(personsMap, parts[0]);
            if (person != null) {
                personsMap.replace(person, personsMap.get(person) + 1);
            } else {
                personsMap.put(Person.builder()
                    .name(parts[0])
                    .age(Integer.parseInt(parts[1]))
                    .build(), 1);
            }
        }
    }

    public int getAge(String name) {
            Person person = containsName(personsMap, name);
            if (person != null) {
                return person.getAge();
            }
            else {
                int age = getAgeFromExternalService(name);
                personsMap.put(Person.builder()
                        .name(name)
                        .age(age)
                        .build(), 1);
                try (BufferedWriter writer = new BufferedWriter(new FileWriter("src/main/resources/names.txt", true))) {
                    writer.write(name + "_" + age);
                    writer.newLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return age;
            }
    }


    private int getAgeFromExternalService(String name) {
        String url = "https://api.agify.io/?name=" + name;
        try {
            String response = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(response);
            return root.get("age").asInt();
        } catch (Exception e) {
            return 30;
        }
    }

    public Map<String, Integer> getNameFrequency() {
        Map<String, Integer> frequencyMap = new HashMap<>();
        for (Person person : personsMap.keySet()) {
            frequencyMap.put(person.getName(), personsMap.get(person));
        }
        return frequencyMap;
    }

    public String getNameWithHighestAge() {
        String name = "";
        int age = 0;
        for (Person person : personsMap.keySet()) {
            if (person.getAge() > age) {
                name = person.getName();
                age = person.getAge();
            }
        }
        return name;
    }

    private static Person containsName(Map<Person, Integer> personsMap, String name){
        for (Person person : personsMap.keySet()){
            if (person.getName().equals(name)){
                return person;
            }
        }
        return null;
    }
}