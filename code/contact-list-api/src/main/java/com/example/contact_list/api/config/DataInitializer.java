package com.example.contact_list.api.config;

import com.example.contact_list.api.model.Contact;
import com.example.contact_list.api.repository.ContactRepository;
import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final ContactRepository contactRepository;
    private final Faker faker = new Faker(Locale.forLanguageTag("es-CL"));

    @Override
    public void run(String... args) throws Exception {
        List<String> cities = List.of(
                "Santiago",
                "Providencia",
                "Maipú",
                "Las Condes",
                "Ñuñoa",
                "Vitacura",
                "La Florida",
                "Pudahuel",
                "Cerrillos",
                "San Miguel",
                "Macul",
                "Estación Central",
                "Recoleta",
                "Independencia",
                "Lo Barnechea"
        );
        for (int i = 0; i < 50; i++) {
            Random random = new Random();
            String firstName = faker.name().firstName();
            String lastName = faker.name().lastName();
            String street = faker.address().streetName();
            String number = faker.address().buildingNumber();
            Contact contact = Contact.builder()
                    .firstName(firstName)
                    .lastName(lastName)
                    .email(faker.internet().emailAddress(firstName + " " + lastName))
                    .phone(faker.numerify("+569########"))
                    .address(street + " " + number)
                    .city(cities.get(random.nextInt(cities.size())))
                    .build();
            contactRepository.save(contact);
        }
    }
}
