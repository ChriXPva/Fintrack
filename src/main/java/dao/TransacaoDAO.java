package dao;

import config.DatabaseConnection;
import exceptions.EntradaInvalidaException;
import model.Transacao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TransacaoDAO implements TransacaoDAOInterface {

    @Override
    public void salvar(Transacao t) throws SQLException {
        String sql = "INSERT INTO transacoes (descricao, valor, tipo, data) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, t.getDescricao());
            stmt.setDouble(2, t.getValor());
            stmt.setString(3, t.getTipo());
            stmt.setString(4, t.getData().toString());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    t.setId(rs.getInt(1));
                }
            }

            conn.commit();
        } catch (SQLException e) {
            try (Connection conn = DatabaseConnection.getConnection()) {
                conn.rollback();
            } catch (SQLException ignored) {
            }
            throw e;
        }
    }

    @Override
    public void atualizar(Transacao t) throws SQLException {
        String sql = "UPDATE transacoes SET descricao = ?, valor = ?, tipo = ?, data = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, t.getDescricao());
            stmt.setDouble(2, t.getValor());
            stmt.setString(3, t.getTipo());
            stmt.setString(4, t.getData().toString());
            stmt.setInt(5, t.getId());

            stmt.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            try (Connection conn = DatabaseConnection.getConnection()) {
                conn.rollback();
            } catch (SQLException ignored) {
            }
            throw e;
        }
    }

    @Override
    public void deletar(Integer id) throws SQLException {
        String sql = "DELETE FROM transacoes WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            try (Connection conn = DatabaseConnection.getConnection()) {
                conn.rollback();
            } catch (SQLException ignored) {
            }
            throw e;
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
                    return mapearResultSet(rs);
                }
            }
        } catch (EntradaInvalidaException e) {
            throw new SQLException("Dados inválidos encontrados no banco: " + e.getMessage());
        }

        return null;
    }

    @Override
    public List<Transacao> listarTodos() throws SQLException {
        String sql = "SELECT * FROM transacoes ORDER BY data DESC";
        List<Transacao> lista = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearResultSet(rs));
            }
        } catch (EntradaInvalidaException e) {
            throw new SQLException("Dados inválidos encontrados no banco: " + e.getMessage());
        }

        return lista;
    }

    @Override
    public double calcularSaldoTotal() throws SQLException {
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

    @Override
    public double calcularSaldoPorMes(int mes, int ano) throws SQLException {
        String mesStr = String.format("%02d", mes);
        String anoStr = String.valueOf(ano);

        String sql = "SELECT " +
                     "  COALESCE(SUM(CASE WHEN tipo = 'RECEITA' THEN valor ELSE 0 END), 0) - " +
                     "  COALESCE(SUM(CASE WHEN tipo = 'DESPESA' THEN valor ELSE 0 END), 0) AS saldo_mes " +
                     "FROM transacoes " +
                     "WHERE strftime('%m', data) = ? AND strftime('%Y', data) = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, mesStr);
            stmt.setString(2, anoStr);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("saldo_mes");
                }
            }
        }
        return 0.0;
    }

    @Override
    public List<Transacao> listarPorMesEAno(int mes, int ano) throws SQLException {
        String mesStr = String.format("%02d", mes);
        String anoStr = String.valueOf(ano);

        String sql = "SELECT * FROM transacoes " +
                     "WHERE strftime('%m', data) = ? AND strftime('%Y', data) = ? " +
                     "ORDER BY data DESC";

        List<Transacao> lista = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, mesStr);
            stmt.setString(2, anoStr);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearResultSet(rs));
                }
            }
        } catch (EntradaInvalidaException e) {
            throw new SQLException("Dados inválidos encontrados no banco: " + e.getMessage());
        }

        return lista;
    }

    private Transacao mapearResultSet(ResultSet rs) throws SQLException, EntradaInvalidaException {
        int id = rs.getInt("id");
        String desc = rs.getString("descricao");
        double valor = rs.getDouble("valor");
        String tipo = rs.getString("tipo");
        LocalDate data = LocalDate.parse(rs.getString("data"));

        return new Transacao(id, desc, valor, tipo, data);
    }
}