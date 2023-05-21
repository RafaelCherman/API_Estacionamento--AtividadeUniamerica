package br.com.uniamerica.estacionamento.controller;

import br.com.uniamerica.estacionamento.entity.Movimentacao;
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

    @GetMapping
    public ResponseEntity<?> findByIdRequest(@RequestParam("id") final Long id) {

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

            this.movimentacaoService.cadastrar(movimentacao);

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
            @RequestBody final Movimentacao movimentacao) {
        try {
            this.movimentacaoService.editar(movimentacao, id);

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

    @PutMapping("/fecha")
    public ResponseEntity<?> finalizar(
            @RequestParam("id") final Long id,
            @RequestBody final Movimentacao movimentacao) {
        try {
            this.movimentacaoService.fecha(movimentacao, id);

            return ResponseEntity.ok(
                    "Entrada: " + movimentacao.getEntrada() + "\n" +
                            "Saida: " + movimentacao.getSaida() + "\n" +
                            "Condutor: " + movimentacao.getCondutor().getNome() + "\n" +
                            "Veiculo: " + movimentacao.getVeiculo().getPlaca() + "\n" +
                            "Quantidade de horas: " + movimentacao.getTempoHora() + " Minutos: " + movimentacao.getTempoMinuto() + "\n" +
                            "Quantidade de horas para o desconto: " + "\n" +
                            "Valor da multa: " + movimentacao.getValorMulta() + "\n" +

                            "Valor total a pagar: " + movimentacao.getValorTotal() + "\n" +
                            "Valor do desconto: " + movimentacao.getValorDesconto()

            );
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
