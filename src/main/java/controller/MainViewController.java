package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.ResumoMensal;
import model.Transacao;
import service.FinTrackerService;
import utils.Formatador;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class MainViewController {

    @FXML private Label lblSaldoTotal;
    @FXML private Accordion accordionMeses;

    private final FinTrackerService service = new FinTrackerService();

    @FXML
    public void initialize() {
        atualizarTela();
    }

    public void atualizarTela() {
        try {
            atualizarSaldoTotal();
            carregarMesesAccordion();
        } catch (SQLException e) {
            exibirErro("Erro ao carregar dados do banco: " + e.getMessage());
        }
    }

    private void atualizarSaldoTotal() throws SQLException {
        double saldoTotal = service.calcularSaldoTotal();
        lblSaldoTotal.setText(Formatador.formatarMoeda(saldoTotal));

        if (saldoTotal > 0) {
            lblSaldoTotal.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #10B981;");
        } else if (saldoTotal < 0) {
            lblSaldoTotal.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #EF4444;");
        } else {
            lblSaldoTotal.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #F8FAFC;");
        }
    }

    private void carregarMesesAccordion() throws SQLException {
        accordionMeses.getPanes().clear();

        int anoAtual = LocalDate.now().getYear();
        List<ResumoMensal> resumos = service.obterResumosAnuais(anoAtual);

        for (ResumoMensal resumo : resumos) {
            TitledPane pane = criarPaneMes(resumo);
            accordionMeses.getPanes().add(pane);
        }
    }

    private TitledPane criarPaneMes(ResumoMensal resumo) {
        BorderPane headerPane = new BorderPane();
        headerPane.setPrefWidth(800);

        Label lblNomeMes = new Label(resumo.getNomeMes());
        lblNomeMes.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        Label lblSaldoMes = new Label(Formatador.formatarMoeda(resumo.getSaldo()));
        if (resumo.getSaldo() > 0) {
            lblSaldoMes.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #10B981;");
        } else if (resumo.getSaldo() < 0) {
            lblSaldoMes.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #EF4444;");
        } else {
            lblSaldoMes.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #64748B;");
        }

        headerPane.setLeft(lblNomeMes);
        headerPane.setRight(lblSaldoMes);

        TitledPane pane = new TitledPane();
        pane.setGraphic(headerPane);

        if (resumo.getTransacoes().isEmpty()) {
            VBox vboxVazio = new VBox();
            vboxVazio.setAlignment(Pos.CENTER);
            vboxVazio.setStyle("-fx-padding: 10px;");
            Label lblVazio = new Label("Nenhuma finança registrada neste mês.");
            lblVazio.setStyle("-fx-text-fill: #94A3B8; -fx-font-style: italic;");
            vboxVazio.getChildren().add(lblVazio);
            pane.setContent(vboxVazio);
        } else {
            TableView<Transacao> tabela = criarTabelaTransacoes(resumo.getTransacoes());
            VBox vboxConteudo = new VBox(tabela);
            vboxConteudo.setSpacing(5);
            pane.setContent(vboxConteudo);
        }

        return pane;
    }

    private TableView<Transacao> criarTabelaTransacoes(List<Transacao> transacoes) {
        TableView<Transacao> tabela = new TableView<>();
        tabela.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tabela.setPrefHeight(180);

        TableColumn<Transacao, String> colDesc = new TableColumn<>("Descrição");
        colDesc.setCellValueFactory(new PropertyValueFactory<>("descricao"));

        TableColumn<Transacao, String> colTipo = new TableColumn<>("Tipo");
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));

        TableColumn<Transacao, LocalDate> colData = new TableColumn<>("Data");
        colData.setCellValueFactory(new PropertyValueFactory<>("data"));
        colData.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(Formatador.formatarData(item));
                }
            }
        });

        TableColumn<Transacao, Double> colValor = new TableColumn<>("Valor");
        colValor.setCellValueFactory(new PropertyValueFactory<>("valor"));
        colValor.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(Formatador.formatarMoeda(item));
                    Transacao t = getTableView().getItems().get(getIndex());
                    if ("RECEITA".equals(t.getTipo())) {
                        setStyle("-fx-text-fill: #10B981; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: #EF4444; -fx-font-weight: bold;");
                    }
                }
            }
        });

        tabela.getColumns().add(colDesc);
        tabela.getColumns().add(colTipo);
        tabela.getColumns().add(colData);
        tabela.getColumns().add(colValor);

        ObservableList<Transacao> items = FXCollections.observableArrayList(transacoes);
        tabela.setItems(items);

        return tabela;
    }

    @FXML
    private void abrirFormularioCadastro() {
        try {
            var resource = getClass().getResource("/fxml/NovaTransacaoView.fxml");
            if (resource == null) {
                exibirErro("Arquivo FXML não encontrado: /fxml/NovaTransacaoView.fxml");
                return;
            }

            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();

            NovaTransacaoController controller = loader.getController();
            controller.setMainController(this);

            Stage stage = new Stage();
            stage.setTitle("Nova Transação");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (IOException e) {
            exibirErro("Erro ao abrir janela de cadastro: " + e.getMessage());
        }
    }

    private void exibirErro(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}