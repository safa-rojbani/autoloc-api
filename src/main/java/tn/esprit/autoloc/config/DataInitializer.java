package tn.esprit.autoloc.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tn.esprit.autoloc.domain.CategorieVehicule;
import tn.esprit.autoloc.domain.StatutVehicule;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.repository.VehiculeRepository;

import java.math.BigDecimal;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initVehicules(VehiculeRepository vehiculeRepository) {
        return args -> {

            if (vehiculeRepository.count() == 0) {

                Vehicule v1 = new Vehicule(
                        null,
                        "123-TUN-4567",
                        "Peugeot",
                        "208",
                        CategorieVehicule.CITADINE,
                        new BigDecimal("120.00"),
                        StatutVehicule.DISPONIBLE
                );

                Vehicule v2 = new Vehicule(
                        null,
                        "456-TUN-7890",
                        "Toyota",
                        "Corolla",
                        CategorieVehicule.BERLINE,
                        new BigDecimal("150.00"),
                        StatutVehicule.DISPONIBLE
                );

                Vehicule v3 = new Vehicule(
                        null,
                        "789-TUN-1234",
                        "Hyundai",
                        "Tucson",
                        CategorieVehicule.SUV,
                        new BigDecimal("200.00"),
                        StatutVehicule.MAINTENANCE
                );

                vehiculeRepository.save(v1);
                vehiculeRepository.save(v2);
                vehiculeRepository.save(v3);

                System.out.println("3 véhicules de démonstration ajoutés !");
            }
        };
    }
}