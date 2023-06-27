package br.com.uniamerica.estacionamento.controller;

import br.com.uniamerica.estacionamento.entity.Condutor;
import br.com.uniamerica.estacionamento.entity.Movimentacao;
import br.com.uniamerica.estacionamento.entity.Veiculo;
import br.com.uniamerica.estacionamento.repository.MovimentacaoRepository;
import br.com.uniamerica.estacionamento.service.MovimentacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping(value = "api/movimentacao")
public class MovimentacaoController {

    @Autowired
    private MovimentacaoRepository movimentacaoRepository;

    @Autowired
    private MovimentacaoService movimentacaoService;


    @GetMapping("/cpf/{cpf}")
    public ResponseEntity<?> findCondutorByCpf(@PathVariable("cpf") final String cpf)
    {
        final Condutor condutor = this.movimentacaoRepository.findCondutorByCpf(cpf);

        return condutor == null
                ? ResponseEntity.badRequest().body("Nenhum condutor encontrado")
                : ResponseEntity.ok(condutor);

    }

    @GetMapping("/placa/{placa}")
    public ResponseEntity<?> findVeiculoByPlaca(@PathVariable("placa") final String placa)
    {
        final Veiculo veiculo= this.movimentacaoRepository.findVeiculoByPlaca(placa);

        return veiculo == null
                ? ResponseEntity.badRequest().body("Nenhum condutor encontrado")
                : ResponseEntity.ok(veiculo);

    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findByIdRequest(@PathVariable("id") final Long id) {

        final Movimentacao movimentacao = this.movimentacaoRepository.findById(id).orElse(null);

        return movimentacao == null
                ? ResponseEntity.badRequest().body("Nenhum valor encontrando.")
                : ResponseEntity.ok(movimentacao);
    }


    @GetMapping("/lista")
    public ResponseEntity<?> listaCompleta() {
        try
        {
            final List<Movimentacao> todos = this.movimentacaoService.tudo();
            return ResponseEntity.ok(todos);
        }
        catch (RuntimeException e) {
            return ResponseEntity.internalServerError().body("Erro: " + e.getMessage());
        }

    }

    @GetMapping("/aberto")
    public ResponseEntity<?> listaAberto() {
        try
        {
            final List<Movimentacao> todos = this.movimentacaoService.aberto();
            return ResponseEntity.ok(todos);
        }
        catch (RuntimeException e) {
            return ResponseEntity.internalServerError().body("Erro: " + e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> cadastrar(@RequestBody final Movimentacao movimentacao) {
        try {

            String resposta = this.movimentacaoService.cadastrar(movimentacao);

            return ResponseEntity.ok(resposta);
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
            @RequestBody final Movimentacao movimentacao) {
        try {
            String resposta = this.movimentacaoService.editar(movimentacao, id);

            return ResponseEntity.ok(resposta);
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

    @PutMapping("/fecha/{id}")
    public ResponseEntity<?> finalizar(
            @PathVariable("id") final Long id,
            @RequestBody final Movimentacao movimentacao) {
        try {
            String resposta;
            resposta = this.movimentacaoService.fecha(movimentacao, id);

            return ResponseEntity.ok(resposta);
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
            this.movimentacaoService.deleta(id);
            return ResponseEntity.ok("Registro desativado com sucesso");

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
