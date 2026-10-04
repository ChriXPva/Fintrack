package dao;

import config.DatabaseConnection;
import exceptions.EntradaInvalidaException;
import model.Transacao;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes de Integração do TransacaoDAO")
public class TransacaoDAOTest {

    private TransacaoDAO dao;

    @BeforeAll
    public static void configurarAmbienteDeTeste() {
        DatabaseConnection.setTestUrl("jdbc:sqlite:fintrack_test.db");
    }

    @BeforeEach
    public void setUp() throws SQLException {
        DatabaseConnection.inicializarBanco();
        dao = new TransacaoDAO();
    }

    @AfterEach
    public void tearDown() throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS transacoes");
            conn.commit();
        }
    }

    @AfterAll
    public static void restaurarAmbienteOriginal() {
        DatabaseConnection.setTestUrl("jdbc:sqlite:fintrack.db");
        File testDb = new File("fintrack_test.db");
        if (testDb.exists()) {
            testDb.delete();
        }
    }

    @Test
    @DisplayName("Deve gerar ID automático ao salvar transação")
    public void deveGerarIdAoSalvar() throws SQLException, EntradaInvalidaException {
        Transacao t = new Transacao("Freelance", 1200.0, "RECEITA", LocalDate.now());
        dao.salvar(t);
        assertNotNull(t.getId());
    }

    @Test
    @DisplayName("Deve recuperar transação por ID existente")
    public void deveBuscarTransacaoPorId() throws SQLException, EntradaInvalidaException {
        Transacao t = new Transacao("Salário", 3000.0, "RECEITA", LocalDate.now());
        dao.salvar(t);

        Transacao buscada = dao.buscarPorId(t.getId());
        assertNotNull(buscada);
        assertEquals("Salário", buscada.getDescricao());
    }

    @Test
    @DisplayName("Deve deletar registro existente no banco")
    public void deveDeletarTransacao() throws SQLException, EntradaInvalidaException {
        Transacao t = new Transacao("Aluguel", 800.0, "DESPESA", LocalDate.now());
        dao.salvar(t);

        dao.deletar(t.getId());
        Transacao buscada = dao.buscarPorId(t.getId());
        assertNull(buscada);
    }

    @Test
    @DisplayName("Deve listar todas as transações cadastradas")
    public void deveListarTodasAsTransacoes() throws SQLException, EntradaInvalidaException {
        dao.salvar(new Transacao("Item 1", 10.0, "RECEITA", LocalDate.now()));
        dao.salvar(new Transacao("Item 2", 20.0, "DESPESA", LocalDate.now()));

        List<Transacao> lista = dao.listarTodos();
        assertEquals(2, lista.size());
    }

    @Test
    @DisplayName("Deve calcular saldo correto via agregação no banco")
    public void deveCalcularSaldoTotalViaBanco() throws SQLException, EntradaInvalidaException {
        dao.salvar(new Transacao("Venda", 1000.0, "RECEITA", LocalDate.now()));
        dao.salvar(new Transacao("Conta de Água", 100.0, "DESPESA", LocalDate.now()));

        double saldo = dao.calcularSaldoTotal();
        assertEquals(900.0, saldo, 0.001);
    }

    @Test
    @DisplayName("Deve calcular saldo específico de um mês")
    public void deveCalcularSaldoPorMes() throws SQLException, EntradaInvalidaException {
        dao.salvar(new Transacao("Bônus Jan", 500.0, "RECEITA", LocalDate.of(2026, 1, 15)));
        dao.salvar(new Transacao("Luz Jan", 100.0, "DESPESA", LocalDate.of(2026, 1, 20)));
        dao.salvar(new Transacao("Salário Fev", 1000.0, "RECEITA", LocalDate.of(2026, 2, 10)));

        double saldoJan = dao.calcularSaldoPorMes(1, 2026);
        assertEquals(400.0, saldoJan, 0.001);
    }

    @Test
    @DisplayName("Deve filtrar transações por mês e ano")
    public void deveListarTransacoesPorMesEAno() throws SQLException, EntradaInvalidaException {
        dao.salvar(new Transacao("Compra 1", 50.0, "DESPESA", LocalDate.of(2026, 3, 10)));
        dao.salvar(new Transacao("Compra 2", 30.0, "DESPESA", LocalDate.of(2026, 3, 12)));
        dao.salvar(new Transacao("Outro Mês", 100.0, "RECEITA", LocalDate.of(2026, 4, 1)));

        List<Transacao> listaMarco = dao.listarPorMesEAno(3, 2026);
        assertEquals(2, listaMarco.size());
    }
}