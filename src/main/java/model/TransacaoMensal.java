package model;

import exceptions.EntradaInvalidaException;
import java.time.LocalDate;

public class TransacaoMensal extends Transacao {

    private int mes;

    public TransacaoMensal(String descricao, double valor, String tipo, LocalDate data, int mes) throws EntradaInvalidaException {
        super(descricao, valor, tipo, data);
        setMes(mes);
    }

    public int getMes() {
        return mes;
    }

    public void setMes(int mes) throws EntradaInvalidaException {
        if (mes < 1 || mes > 12) {
            throw new EntradaInvalidaException("Mês inválido! Escolha um valor entre 1 e 12.");
        }
        this.mes = mes;
    }

    @Override
    public String toString() {
        return super.toString() + " | Mês: " + mes;
    }
}