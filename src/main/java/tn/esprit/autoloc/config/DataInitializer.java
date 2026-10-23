package tn.esprit.autoloc.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tn.esprit.autoloc.domain.CategorieVehicule;
import tn.esprit.autoloc.domain.StatutVehicule;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.repository.IVehiculeRepository;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

@Configuration
@Slf4j
public class DataInitializer {

    @Bean
    CommandLineRunner initVehicules(IVehiculeRepository vehiculeRepository) {
        return args -> {

            if (vehiculeRepository.count() == 0) {

                Vehicule v1 = new Vehicule();
                v1.setImmatriculation("123-TUN-4567");
                v1.setMarque("Peugeot");
                v1.setModele("208");
                v1.setCategorie(CategorieVehicule.CITADINE);
                v1.setTarifJournalier(new BigDecimal("120.00"));
                v1.setStatut(StatutVehicule.DISPONIBLE);

                Vehicule v2 = new Vehicule();
                v2.setImmatriculation("456-TUN-7890");
                v2.setMarque("Toyota");
                v2.setModele("Corolla");
                v2.setCategorie(CategorieVehicule.BERLINE);
                v2.setTarifJournalier(new BigDecimal("150.00"));
                v2.setStatut(StatutVehicule.DISPONIBLE);

                Vehicule v3 = new Vehicule();
                v3.setImmatriculation("789-TUN-1234");
                v3.setMarque("Hyundai");
                v3.setModele("Tucson");
                v3.setCategorie(CategorieVehicule.SUV);
                v3.setTarifJournalier(new BigDecimal("180.00"));
                v3.setStatut(StatutVehicule.MAINTENANCE);

                vehiculeRepository.save(v1);
                vehiculeRepository.save(v2);
                vehiculeRepository.save(v3);

                log.info("3 véhicules de démonstration ajoutés !");
            }
        };
    }
}