package br.com.uniamerica.estacionamento.controller;

import br.com.uniamerica.estacionamento.entity.Veiculo;
import br.com.uniamerica.estacionamento.repository.VeiculoRepository;
import br.com.uniamerica.estacionamento.service.VeiculoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping(value = "api/veiculo")
public class VeiculoController {

    @Autowired
    private VeiculoRepository veiculoRepository;

    @Autowired
    private VeiculoService veiculoService;

    @GetMapping
    public ResponseEntity<?> findByIdRequest(@RequestParam("id") final Long id) {

        final Veiculo veiculo = this.veiculoRepository.findById(id).orElse(null);

        return veiculo == null
                ? ResponseEntity.badRequest().body("Nenhum valor encontrando.")
                : ResponseEntity.ok(veiculo);
    }


    @GetMapping("/lista")
    public ResponseEntity<?> listaCompleta() {
        try
        {
            final List<Veiculo> todos = this.veiculoService.tudo();
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
            final List<Veiculo> todos = this.veiculoService.ativo();
            return ResponseEntity.ok(todos);
        }
        catch (RuntimeException e) {
            return ResponseEntity.internalServerError().body("Erro: " + e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> cadastrar(@RequestBody final Veiculo veiculo) {
        try {

            this.veiculoService.cadastrar(veiculo);

            return ResponseEntity.ok("Registro cadastrado com sucesso");
        }
        catch (DataIntegrityViolationException e){
            return ResponseEntity.internalServerError().body("Erro: " + e.getCause().getCause().getMessage());
        }
        catch (RuntimeException e) {
            return ResponseEntity.internalServerError().body("Erro: " + e.getMessage());
        }

    }


    @PutMapping
    public ResponseEntity<?> editar(
            @RequestParam("id") final Long id,
            @RequestBody final Veiculo veiculo) {
        try {
            this.veiculoService.editar(veiculo, id);

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


    @DeleteMapping
    public ResponseEntity<?> delete(@RequestParam("id") final Long id) {

        try {
            if (this.veiculoService.deleta(id))
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
