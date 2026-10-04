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

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes de Integração do TransacaoDAO (SQLite em Memória)")
public class TransacaoDAOTest {

    private TransacaoDAO dao;

    @BeforeAll
    public static void configurarAmbienteDeTeste() {
        // Redireciona a conexão para o banco SQLite totalmente mantido em memória RAM
        DatabaseConnection.setTestUrl("jdbc:sqlite::memory:");
    }

    @BeforeEach
    public void setUp() throws SQLException {
        // Recria a estrutura do banco antes de cada método de teste
        DatabaseConnection.inicializarBanco();
        dao = new TransacaoDAO();
    }

    @AfterEach
    public void tearDown() throws SQLException {
        // Destrói totalmente as tabelas e dados ao final de cada teste para zerar o estado
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS transacoes");
            conn.commit();
        }
    }

    @AfterAll
    public static void restaurarAmbienteOriginal() {
        DatabaseConnection.setTestUrl("jdbc:sqlite:fintrack.db");
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
}