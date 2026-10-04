package model;

import exceptions.EntradaInvalidaException;
import java.time.LocalDate;

public class Transacao {

    private Integer id;
    private String descricao;
    private double valor;
    private String tipo; // "RECEITA" ou "DESPESA"
    private LocalDate data;
    
    public Transacao() {
    }

    public Transacao(String descricao, double valor, String tipo, LocalDate data) throws EntradaInvalidaException {
        setDescricao(descricao);
        setValor(valor);
        setTipo(tipo);
        setData(data);
    }

    public Transacao(Integer id, String descricao, double valor, String tipo, LocalDate data) throws EntradaInvalidaException {
        this(descricao, valor, tipo, data);
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) throws EntradaInvalidaException {
        if (descricao == null || descricao.trim().isEmpty()) {
            throw new EntradaInvalidaException("A descrição não pode ser vazia.");
        }
        this.descricao = descricao;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) throws EntradaInvalidaException {
        if (valor <= 0) {
            throw new EntradaInvalidaException("O valor deve ser maior que 0.");
        }
        this.valor = valor;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) throws EntradaInvalidaException {
        if (tipo == null || (!tipo.equalsIgnoreCase("RECEITA") && !tipo.equalsIgnoreCase("DESPESA"))) {
            throw new EntradaInvalidaException("O tipo deve ser 'RECEITA' ou 'DESPESA'.");
        }
        this.tipo = tipo.toUpperCase();
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }
}