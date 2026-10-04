package service;

import dao.TransacaoDAO;
import model.Transacao;

import java.sql.SQLException;
import java.util.List;

public class FinTrackerService {

    private final TransacaoDAO transacaoDAO;

    public FinTrackerService() {
        this.transacaoDAO = new TransacaoDAO();
    }

    public FinTrackerService(TransacaoDAO transacaoDAO) {
        this.transacaoDAO = transacaoDAO;
    }

    public void adicionarTransacao(Transacao transacao) throws SQLException {
        transacaoDAO.salvar(transacao);
    }

    public void removerTransacao(int id) throws SQLException {
        transacaoDAO.deletar(id);
    }

    public List<Transacao> listarTransacoes() throws SQLException {
        return transacaoDAO.listarTodos();
    }

    public double calcularSaldoTotal() throws SQLException {
    	return transacaoDAO.calcularSaldoTotal();
    }
}