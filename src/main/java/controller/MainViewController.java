package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Transacao;
import service.FinTrackerService;
import utils.Formatador;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;

public class MainViewController {

    @FXML private TableView<Transacao> tabelaTransacoes;
    @FXML private TableColumn<Transacao, Integer> colId;
    @FXML private TableColumn<Transacao, String> colDescricao;
    @FXML private TableColumn<Transacao, String> colTipo;
    @FXML private TableColumn<Transacao, Double> colValor;
    @FXML private TableColumn<Transacao, LocalDate> colData;

    private final FinTrackerService service = new FinTrackerService();
    private final ObservableList<Transacao> listaObservable = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colDescricao.setCellValueFactory(new PropertyValueFactory<>("descricao"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        colValor.setCellValueFactory(new PropertyValueFactory<>("valor"));
        colData.setCellValueFactory(new PropertyValueFactory<>("data"));

        colValor.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Double valor, boolean empty) {
                super.updateItem(valor, empty);
                if (empty || valor == null) {
                    setText(null);
                } else {
                    setText(Formatador.formatarMoeda(valor));
                }
            }
        });

        atualizarTabela();
    }

    public void atualizarTabela() {
        try {
            listaObservable.setAll(service.listarTransacoes());
            tabelaTransacoes.setItems(listaObservable);
        } catch (SQLException e) {
            mostrarAlerta("Erro", "Falha ao carregar transações: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void abrirNovaTransacaoView() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/NovaTransacaoView.fxml"));
        Parent root = loader.load();

        NovaTransacaoController controller = loader.getController();
        controller.setMainController(this);

        Stage stage = new Stage();
        stage.setTitle("Nova Transação");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(new Scene(root));
        stage.showAndWait();
    }

    @FXML
    private void removerTransacao() {
        Transacao selecionada = tabelaTransacoes.getSelectionModel().getSelectedItem();
        if (selecionada != null) {
            try {
                service.removerTransacao(selecionada.getId());
                atualizarTabela();
            } catch (SQLException e) {
                mostrarAlerta("Erro", "Erro ao remover transação: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        } else {
            mostrarAlerta("Aviso", "Selecione uma transação para remover.", Alert.AlertType.WARNING);
        }
    }

    @FXML
    private void abrirRelatorioView() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/RelatorioView.fxml"));
        Parent root = loader.load();

        Stage stage = new Stage();
        stage.setTitle("Relatório");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(new Scene(root));
        stage.showAndWait();
    }

    private void mostrarAlerta(String titulo, String mensagem, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}