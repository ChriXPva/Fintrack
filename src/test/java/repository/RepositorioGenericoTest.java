package repository;

import exceptions.EntradaInvalidaException;
import model.Transacao;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes do RepositorioGenerico")
public class RepositorioGenericoTest {

    private RepositorioGenerico<Transacao> repositorio;
    private Transacao t1;
    private Transacao t2;

    @BeforeEach
    public void setUp() throws EntradaInvalidaException {
        repositorio = new RepositorioGenerico<>();
        t1 = new Transacao("Luz", 150.0, "DESPESA", LocalDate.now());
        t2 = new Transacao("Internet", 100.0, "DESPESA", LocalDate.now());
    }

    @AfterEach
    public void tearDown() {
        repositorio.limpar();
    }

    @Test
    @DisplayName("Deve adicionar um elemento individual ao repositório")
    public void deveAdicionarElemento() {
        repositorio.adicionar(t1);
        assertEquals(1, repositorio.listarTodos().size());
    }

    @Test
    @DisplayName("Deve remover um elemento do repositório")
    public void deveRemoverElemento() {
        repositorio.adicionar(t1);
        repositorio.remover(t1);
        assertEquals(0, repositorio.listarTodos().size());
    }

    @Test
    @DisplayName("Deve adicionar múltiplos elementos usando wildcard extends")
    public void deveAdicionarTodosComWildcardExtends() {
        repositorio.adicionarTodos(List.of(t1, t2));
        assertEquals(2, repositorio.listarTodos().size());
    }

    @Test
    @DisplayName("Deve transferir elementos usando wildcard super")
    public void deveTransferirElementosComWildcardSuper() {
        repositorio.adicionar(t1);
        List<Object> destino = new ArrayList<>();
        repositorio.transferirPara(destino);
        assertEquals(1, destino.size());
    }

    @Test
    @DisplayName("Deve garantir imutabilidade ao listar elementos diretamente")
    public void deveRetornarListaImutavelAoListarTodos() {
        repositorio.adicionar(t1);
        assertThrows(UnsupportedOperationException.class, () -> {
            repositorio.listarTodos().add(t2);
        });
    }
}