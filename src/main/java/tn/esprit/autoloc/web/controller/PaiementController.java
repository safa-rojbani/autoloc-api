package tn.esprit.autoloc.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tn.esprit.autoloc.domain.Paiement;
import tn.esprit.autoloc.service.IPaiementService;

import java.util.List;

@RestController
@RequestMapping("/paiements")
@RequiredArgsConstructor
@Tag(
        name = "Paiements",
        description = "Consultation des paiements"
)
public class PaiementController {

    private final IPaiementService paiementService;

    @GetMapping
    @Operation(
            summary = "Lister les paiements",
            description = "Retourne la liste de tous les paiements."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Liste des paiements récupérée avec succès"
    )
    public List<Paiement> findAll() {
        return paiementService.findAll();
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Consulter un paiement",
            description = "Retourne un paiement à partir de son identifiant."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Paiement trouvé"
    )
    @ApiResponse(
            responseCode = "404",
            description = "Paiement introuvable"
    )
    public Paiement findById(
            @Parameter(
                    description = "Identifiant du paiement",
                    example = "1"
            )
            @PathVariable Long id) {

        return paiementService.findById(id);
    }
}