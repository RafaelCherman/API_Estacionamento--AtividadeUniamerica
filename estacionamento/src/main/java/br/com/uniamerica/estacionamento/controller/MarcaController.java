package br.com.uniamerica.estacionamento.controller;

import br.com.uniamerica.estacionamento.entity.Marca;
import br.com.uniamerica.estacionamento.repository.MarcaRepository;
import br.com.uniamerica.estacionamento.service.MarcaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping(value = "api/marca")
public class MarcaController {

    @Autowired
    private MarcaRepository marcaRepository;

    @Autowired
    private MarcaService marcaService;

    @GetMapping("/{id}")
    public ResponseEntity<?> findByIdRequest(@PathVariable("id") final Long id) {

        final Marca marca = this.marcaRepository.findById(id).orElse(null);

        return marca == null
                ? ResponseEntity.badRequest().body("Nenhum valor encontrando.")
                : ResponseEntity.ok(marca);
    }


    @GetMapping("/lista")
    public ResponseEntity<?> listaCompleta() {
        try
        {
            final List<Marca> todos = this.marcaService.tudo();
            return ResponseEntity.ok(todos);
        }
        catch (RuntimeException e) {
            return ResponseEntity.internalServerError().body("Erro: " + e.getMessage());
        }

    }

    @GetMapping("/ativo")
    public ResponseEntity<?> listaAtivo() {
        try
        {
            final List<Marca> todos = this.marcaService.ativo();
            return ResponseEntity.ok(todos);
        }
        catch (RuntimeException e) {
            return ResponseEntity.internalServerError().body("Erro: " + e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> cadastrar(@RequestBody final Marca marca) {
        try {

            this.marcaService.cadastrar(marca);

            return ResponseEntity.ok("Registro cadastrado com sucesso");
        }
        catch (DataIntegrityViolationException e){
            return ResponseEntity.internalServerError().body("Erro: " + e.getCause().getCause().getMessage());
        }
        catch (RuntimeException e) {
            return ResponseEntity.internalServerError().body("Erro: " + e.getMessage());
        }

    }


    @PutMapping("/{id}")
    public ResponseEntity<?> editar(
            @PathVariable("id") final Long id,
            @RequestBody final Marca marca) {
        try {
            this.marcaService.editar(marca, id);

            return ResponseEntity.ok("Registro atualizado com sucesso");
        }
        catch (DataIntegrityViolationException e)
        {
            return ResponseEntity.internalServerError().body("Erro: " + e.getCause().getCause().getMessage());
        }
        catch (RuntimeException e)
        {
            return ResponseEntity.internalServerError().body("Erro: " + e.getMessage());
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") final Long id) {

        try {
            if (this.marcaService.deleta(id))
            {
                return ResponseEntity.ok("Registro desativado com sucesso");
            }
            else
            {
                return ResponseEntity.ok("Registro deletado com sucesso");
            }
        }
        catch (DataIntegrityViolationException e)
        {
            return ResponseEntity.internalServerError().body("Erro: " + e.getCause().getCause().getMessage());
        }
        catch (RuntimeException e)
        {
            return ResponseEntity.internalServerError().body("Erro: " + e.getMessage());
        }


    }


}
