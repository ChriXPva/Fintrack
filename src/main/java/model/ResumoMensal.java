package model;

import java.util.ArrayList;
import java.util.List;

public class ResumoMensal {

    private final int mes;
    private final String nomeMes;
    private final List<Transacao> transacoes;
    private double saldo;

    public ResumoMensal(int mes, String nomeMes) {
        this.mes = mes;
        this.nomeMes = nomeMes;
        this.transacoes = new ArrayList<>();
        this.saldo = 0.0;
    }

    public int getMes() {
        return mes;
    }

    public String getNomeMes() {
        return nomeMes;
    }

    public List<Transacao> getTransacoes() {
        return transacoes;
    }

    public double getSaldo() {
        return saldo;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }
}