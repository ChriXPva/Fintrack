package dao;

import java.sql.SQLException;
import java.util.List;

public interface GenericDAO<T, K> {
    void salvar(T entidade) throws SQLException;
    void atualizar(T entidade) throws SQLException;
    void deletar(K id) throws SQLException;
    T buscarPorId(K id) throws SQLException;
    List<T> listarTodos() throws SQLException;
}