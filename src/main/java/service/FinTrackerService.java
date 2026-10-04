package service;

import dao.TransacaoDAO;
import dao.TransacaoDAOInterface;
import model.ResumoMensal;
import model.Transacao;
import utils.Formatador;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FinTrackerService {

    private final TransacaoDAOInterface transacaoDAO;

    public FinTrackerService() {
        this.transacaoDAO = new TransacaoDAO();
    }

    public FinTrackerService(TransacaoDAOInterface transacaoDAO) {
        this.transacaoDAO = transacaoDAO;
    }

    public void adicionarTransacao(Transacao transacao) throws SQLException {
        transacaoDAO.salvar(transacao);
    }

    public void removerTransacao(Integer id) throws SQLException {
        transacaoDAO.deletar(id);
    }

    public List<Transacao> listarTransacoes() throws SQLException {
        return transacaoDAO.listarTodos();
    }

    public double calcularSaldoTotal() throws SQLException {
        return transacaoDAO.calcularSaldoTotal();
    }

    public List<ResumoMensal> obterResumosAnuais(int ano) throws SQLException {
        List<ResumoMensal> resumos = new ArrayList<>();

        for (int m = 1; m <= 12; m++) {
            ResumoMensal resumo = new ResumoMensal(m, Formatador.obterNomeMes(m));
            List<Transacao> transacoesMes = transacaoDAO.listarPorMesEAno(m, ano);
            double saldoMes = transacaoDAO.calcularSaldoPorMes(m, ano);

            resumo.getTransacoes().addAll(transacoesMes);
            resumo.setSaldo(saldoMes);
            resumos.add(resumo);
        }

        return resumos;
    }
}