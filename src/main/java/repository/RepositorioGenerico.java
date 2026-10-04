package repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RepositorioGenerico<T> {

    private final List<T> elementos = new ArrayList<>();

    public void adicionar(T elemento) {
        elementos.add(elemento);
    }

    public void adicionarTodos(List<? extends T> novosElementos) {
        elementos.addAll(novosElementos);
    }

    public boolean remover(T elemento) {
        return elementos.remove(elemento);
    }

    public void transferirPara(List<? super T> destino) {
        destino.addAll(elementos);
    }

    public List<T> listarTodos() {
        return Collections.unmodifiableList(elementos);
    }

    public void limpar() {
        elementos.clear();
    }
}