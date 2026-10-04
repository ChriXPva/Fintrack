package model;

import exceptions.EntradaInvalidaException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes da Entidade Transacao")
public class TransacaoTest {

    private Transacao transacaoValida;

    @BeforeEach
    public void setUp() throws EntradaInvalidaException {
        transacaoValida = new Transacao("Salário", 5000.0, "RECEITA", LocalDate.of(2026, 10, 4));
    }

    @AfterEach
    public void tearDown() {
        transacaoValida = null;
    }

    @Test
    @DisplayName("Deve atribuir a descrição corretamente")
    public void deveAtribuirDescricaoCorretamente() {
        assertEquals("Salário", transacaoValida.getDescricao());
    }

    @Test
    @DisplayName("Deve atribuir o valor corretamente")
    public void deveAtribuirValorCorretamente() {
        assertEquals(5000.0, transacaoValida.getValor());
    }

    @Test
    @DisplayName("Deve atribuir o tipo corretamente")
    public void deveAtribuirTipoCorretamente() {
        assertEquals("RECEITA", transacaoValida.getTipo());
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar transação com valor negativo")
    public void deveLancarExcecaoQuandoValorForNegativo() {
        assertThrows(EntradaInvalidaException.class, () -> {
            new Transacao("Mercado", -10.0, "DESPESA", LocalDate.now());
        });
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar transação com valor zero")
    public void deveLancarExcecaoQuandoValorForZero() {
        assertThrows(EntradaInvalidaException.class, () -> {
            new Transacao("Pix", 0.0, "DESPESA", LocalDate.now());
        });
    }

    @Test
    @DisplayName("Deve lançar exceção quando o tipo for inválido")
    public void deveLancarExcecaoQuandoTipoForInvalido() {
        assertThrows(EntradaInvalidaException.class, () -> {
            new Transacao("Investimento", 100.0, "OUTRO", LocalDate.now());
        });
    }

    @Test
    @DisplayName("Deve lançar exceção quando a descrição for vazia")
    public void deveLancarExcecaoQuandoDescricaoForVazia() {
        assertThrows(EntradaInvalidaException.class, () -> {
            new Transacao("", 100.0, "RECEITA", LocalDate.now());
        });
    }
}