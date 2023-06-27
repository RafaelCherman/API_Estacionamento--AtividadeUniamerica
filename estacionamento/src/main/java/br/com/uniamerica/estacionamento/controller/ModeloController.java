package br.com.uniamerica.estacionamento.controller;

import br.com.uniamerica.estacionamento.entity.Marca;
import br.com.uniamerica.estacionamento.entity.Modelo;
import br.com.uniamerica.estacionamento.repository.ModeloRepository;

import br.com.uniamerica.estacionamento.repository.MovimentacaoRepository;
import br.com.uniamerica.estacionamento.service.ModeloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Controller
@RequestMapping(value = "/api/modelo")
public class ModeloController {

    //Injeção

    @Autowired
    private ModeloService modeloService;
    @Autowired
    private ModeloRepository modeloRepository;

    @Autowired
    private MovimentacaoRepository movimentacaoRepository;
    /*
    public ModeloController(ModeloRepository modeloRepository){
        this.modeloRepository = modeloRepository;
    }
    */


    /*
    *
    * 8080/api/modelo/1
    *

    @GetMapping("/{id}")
    public ResponseEntity<?> findByIdPath(@PathVariable("id") final Long id){

        final Modelo modelo = this.modeloRepository.findById(id).orElse(null);

        return modelo == null
                ? ResponseEntity.badRequest().body("Nenhum valor encontrando.")
                : ResponseEntity.ok(modelo);
    }




     *
     * 8080/api/modelo?id=1
     *
     */

    @GetMapping("/marca/{nome}")
    public ResponseEntity<?> findMarcaByNome(@PathVariable("nome") final String nome){

        final Marca marca = this.modeloRepository.findMarcaByNome(nome);

        return marca == null
                ? ResponseEntity.badRequest().body("Marca não encontrada")
                : ResponseEntity.ok(marca);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findByIdRequest(@PathVariable("id") final Long id){

        final Modelo modelo = this.modeloRepository.findById(id).orElse(null);

        return modelo == null
                ? ResponseEntity.badRequest().body("Nenhum valor encontrando.")
                : ResponseEntity.ok(modelo);
    }



    @GetMapping("/lista")
    public ResponseEntity<?> listaCompleta() {
        try {
            final List<Modelo> todos = this.modeloService.tudo();
            return ResponseEntity.ok(todos);
        }
        catch (RuntimeException e) {
            return ResponseEntity.internalServerError().body("Erro: " + e.getMessage());
        }
    }

    @GetMapping("/ativo")
    public ResponseEntity<?> ativo(){
        try {
            final List<Modelo> todos = this.modeloService.ativo();
            return ResponseEntity.ok(todos);
        }
        catch (RuntimeException e) {
            return ResponseEntity.internalServerError().body("Erro: " + e.getMessage());
        }

    }

    /*
    @GetMapping("/ativo")
    public ResponseEntity<?> listaAtivo(){
        final List<Modelo> todos = this.modeloRepository.findAll();

        List<Modelo> ativos = new ArrayList<Modelo>();

        for(Modelo i : todos)
        {
            if(i.isAtivo() == true)
            {
                ativos.add(i);
            }
        }

        return ResponseEntity.ok(ativos);
    }
    */

    @PostMapping
    public ResponseEntity<?> cadastrar(@RequestBody final Modelo modelo) {
        try {

            this.modeloService.cadastrar(modelo);

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
            @RequestBody final Modelo modelo)
    {
        try
        {
            this.modeloService.editar(modelo, id);

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
    public ResponseEntity<?> delete(@PathVariable("id") final Long id)
    {

        try
        {
            if(this.modeloService.deleta(id))
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



       /* final Modelo modelo = this.modeloRepository.findById(id).orElse(null);

        boolean valido = this.modeloRepository.isInVeiculo(id);

        if( modelo == null)
            {
                return ResponseEntity.badRequest().body("Nenhum registro encontrando.");
            }
            else
            {
                if(valido) {
                    modelo.setAtivo(false);
                    this.modeloRepository.save(modelo);
                    return ResponseEntity.ok("Registro desativado com sucesso");
                }
                this.modeloRepository.delete(modelo);
                return ResponseEntity.ok("Registro deletado com sucesso");
            }

*/
    }




}
