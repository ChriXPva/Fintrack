package dao;

import model.Transacao;

import java.sql.SQLException;
import java.util.List;

public interface TransacaoDAOInterface extends GenericDAO<Transacao, Integer> {
    double calcularSaldoTotal() throws SQLException;
    double calcularSaldoPorMes(int mes, int ano) throws SQLException;
    List<Transacao> listarPorMesEAno(int mes, int ano) throws SQLException;
}