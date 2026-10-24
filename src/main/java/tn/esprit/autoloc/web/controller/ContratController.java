package tn.esprit.autoloc.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import tn.esprit.autoloc.domain.Contrat;
import tn.esprit.autoloc.service.IContratService;

import java.util.List;

@RestController
@RequestMapping("/contrats")
@RequiredArgsConstructor
@Tag(
        name = "Contrats",
        description = "Gestion des contrats de location"
)
public class ContratController {

    private final IContratService contratService;

    @GetMapping
    @Operation(
            summary = "Lister les contrats",
            description = "Retourne la liste de tous les contrats."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Liste des contrats récupérée avec succès"
    )
    public List<Contrat> findAll() {
        return contratService.findAll();
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Consulter un contrat",
            description = "Retourne un contrat à partir de son identifiant."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Contrat trouvé"
    )
    @ApiResponse(
            responseCode = "404",
            description = "Contrat introuvable"
    )
    public Contrat findById(
            @Parameter(
                    description = "Identifiant du contrat",
                    example = "1"
            )
            @PathVariable Long id) {

        return contratService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Créer un contrat",
            description = "Crée un nouveau contrat."
    )
    @ApiResponse(
            responseCode = "201",
            description = "Contrat créé avec succès"
    )
    @ApiResponse(
            responseCode = "400",
            description = "Données du contrat invalides"
    )
    public Contrat create(@RequestBody Contrat contrat) {
        return contratService.create(contrat);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Modifier un contrat",
            description = "Modifie un contrat existant à partir de son identifiant."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Contrat modifié avec succès"
    )
    @ApiResponse(
            responseCode = "400",
            description = "Données du contrat invalides"
    )
    @ApiResponse(
            responseCode = "404",
            description = "Contrat introuvable"
    )
    public Contrat update(
            @Parameter(
                    description = "Identifiant du contrat",
                    example = "1"
            )
            @PathVariable Long id,
            @RequestBody Contrat contrat) {

        return contratService.update(id, contrat);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Supprimer un contrat",
            description = "Supprime un contrat à partir de son identifiant."
    )
    @ApiResponse(
            responseCode = "204",
            description = "Contrat supprimé avec succès"
    )
    @ApiResponse(
            responseCode = "404",
            description = "Contrat introuvable"
    )
    public void deleteById(
            @Parameter(
                    description = "Identifiant du contrat",
                    example = "1"
            )
            @PathVariable Long id) {

        contratService.deleteById(id);
    }
}