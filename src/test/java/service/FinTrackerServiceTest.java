package service;

import dao.TransacaoDAOInterface;
import exceptions.EntradaInvalidaException;
import model.ResumoMensal;
import model.Transacao;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes da Camada de Serviço FinTrackerService")
public class FinTrackerServiceTest {

    private FinTrackerService service;
    private MockTransacaoDAO mockDAO;

    @BeforeEach
    public void setUp() {
        mockDAO = new MockTransacaoDAO();
        service = new FinTrackerService(mockDAO);
    }

    @AfterEach
    public void tearDown() {
        mockDAO = null;
        service = null;
    }

    @Test
    @DisplayName("Deve gerar exatamente 12 resumos mensais para o ano")
    public void deveGerar12ResumosMensais() throws SQLException {
        List<ResumoMensal> resumos = service.obterResumosAnuais(2026);
        assertEquals(12, resumos.size());
    }

    @Test
    @DisplayName("Deve atribuir nome correto aos meses do ano")
    public void deveAtribuirNomeCorretoAosMeses() throws SQLException {
        List<ResumoMensal> resumos = service.obterResumosAnuais(2026);
        assertEquals("Janeiro", resumos.get(0).getNomeMes());
        assertEquals("Dezembro", resumos.get(11).getNomeMes());
    }

    @Test
    @DisplayName("Deve calcular saldo do mês via serviço")
    public void deveCalcularSaldoDoMesViaServico() throws SQLException, EntradaInvalidaException {
        Transacao t = new Transacao("Freelance", 1500.0, "RECEITA", LocalDate.of(2026, 5, 10));
        mockDAO.salvar(t);

        List<ResumoMensal> resumos = service.obterResumosAnuais(2026);
        assertEquals(1500.0, resumos.get(4).getSaldo(), 0.001);
    }

    private static class MockTransacaoDAO implements TransacaoDAOInterface {

        private final List<Transacao> bancoEmMemoria = new ArrayList<>();

        @Override
        public void salvar(Transacao entidade) {
            entidade.setId(bancoEmMemoria.size() + 1);
            bancoEmMemoria.add(entidade);
        }

        @Override
        public void atualizar(Transacao entidade) {
        }

        @Override
        public void deletar(Integer id) {
            bancoEmMemoria.removeIf(t -> t.getId().equals(id));
        }

        @Override
        public Transacao buscarPorId(Integer id) {
            return bancoEmMemoria.stream().filter(t -> t.getId().equals(id)).findFirst().orElse(null);
        }

        @Override
        public List<Transacao> listarTodos() {
            return new ArrayList<>(bancoEmMemoria);
        }

        @Override
        public double calcularSaldoTotal() {
            return bancoEmMemoria.stream()
                    .mapToDouble(t -> "RECEITA".equals(t.getTipo()) ? t.getValor() : -t.getValor())
                    .sum();
        }

        @Override
        public double calcularSaldoPorMes(int mes, int ano) {
            return bancoEmMemoria.stream()
                    .filter(t -> t.getData().getMonthValue() == mes && t.getData().getYear() == ano)
                    .mapToDouble(t -> "RECEITA".equals(t.getTipo()) ? t.getValor() : -t.getValor())
                    .sum();
        }

        @Override
        public List<Transacao> listarPorMesEAno(int mes, int ano) {
            List<Transacao> filtradas = new ArrayList<>();
            for (Transacao t : bancoEmMemoria) {
                if (t.getData().getMonthValue() == mes && t.getData().getYear() == ano) {
                    filtradas.add(t);
                }
            }
            return filtradas;
        }
    }
}