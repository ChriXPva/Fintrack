package dao;

import config.DatabaseConnection;
import exceptions.EntradaInvalidaException;
import model.Transacao;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TransacaoDAO implements GenericDAO<Transacao, Integer> {

    @Override
    public void salvar(Transacao transacao) throws SQLException {
        String sql = "INSERT INTO transacoes (descricao, valor, tipo, data) VALUES (?, ?, ?, ?)";
        Connection conn = null;

        try {
            conn = DatabaseConnection.getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, transacao.getDescricao());
                stmt.setDouble(2, transacao.getValor());
                stmt.setString(3, transacao.getTipo());
                stmt.setString(4, transacao.getData().toString());

                stmt.executeUpdate();

                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        transacao.setId(rs.getInt(1));
                    }
                }
                conn.commit(); // Garante Atomicidade, Consistência e Durabilidade
            }
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback(); // Rollback em caso de falha (ACID)
            }
            throw e;
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }

    @Override
    public void atualizar(Transacao transacao) throws SQLException {
        String sql = "UPDATE transacoes SET descricao = ?, valor = ?, tipo = ?, data = ? WHERE id = ?";
        Connection conn = null;

        try {
            conn = DatabaseConnection.getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, transacao.getDescricao());
                stmt.setDouble(2, transacao.getValor());
                stmt.setString(3, transacao.getTipo());
                stmt.setString(4, transacao.getData().toString());
                stmt.setInt(5, transacao.getId());

                stmt.executeUpdate();
                conn.commit();
            }
        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (conn != null) conn.close();
        }
    }

    @Override
    public void deletar(Integer id) throws SQLException {
        String sql = "DELETE FROM transacoes WHERE id = ?";
        Connection conn = null;

        try {
            conn = DatabaseConnection.getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, id);
                stmt.executeUpdate();
                conn.commit();
            }
        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (conn != null) conn.close();
        }
    }

    @Override
    public Transacao buscarPorId(Integer id) throws SQLException {
        String sql = "SELECT * FROM transacoes WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return instanciarTransacao(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<Transacao> listarTodos() throws SQLException {
        List<Transacao> lista = new ArrayList<>();
        String sql = "SELECT * FROM transacoes";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lista.add(instanciarTransacao(rs));
            }
        }
        return lista;
    }

    private Transacao instanciarTransacao(ResultSet rs) throws SQLException {
        try {
            return new Transacao(
                    rs.getInt("id"),
                    rs.getString("descricao"),
                    rs.getDouble("valor"),
                    rs.getString("tipo"),
                    LocalDate.parse(rs.getString("data"))
            );
        } catch (EntradaInvalidaException e) {
            throw new SQLException("Erro ao instanciar objeto vindo do banco: " + e.getMessage());
        }
    }
    
    public double calcularSaldoTotal() throws SQLException {
        // COALESCE garante que, se a tabela estiver vazia, o banco retorne 0.0 em vez de NULL
        String sql = "SELECT " +
                     "  COALESCE(SUM(CASE WHEN tipo = 'RECEITA' THEN valor ELSE 0 END), 0) - " +
                     "  COALESCE(SUM(CASE WHEN tipo = 'DESPESA' THEN valor ELSE 0 END), 0) AS saldo_total " +
                     "FROM transacoes";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getDouble("saldo_total");
            }
        }
        return 0.0;
    }
}