package controller;

import exceptions.EntradaInvalidaException;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Transacao;
import service.FinTrackerService;

import java.sql.SQLException;

public class NovaTransacaoController {

    @FXML private TextField txtDescricao;
    @FXML private TextField txtValor;
    @FXML private ComboBox<String> comboTipo;
    @FXML private DatePicker dpData;

    private MainViewController mainController;
    private final FinTrackerService service = new FinTrackerService();

    public void setMainController(MainViewController mainController) {
        this.mainController = mainController;
    }

    @FXML
    public void initialize() {
        comboTipo.getItems().addAll("RECEITA", "DESPESA");
    }

    @FXML
    private void salvarTransacao() {
        try {
            String desc = txtDescricao.getText();
            double valor = Double.parseDouble(txtValor.getText().replace(",", "."));
            String tipo = comboTipo.getValue();

            Transacao t = new Transacao(desc, valor, tipo, dpData.getValue());
            service.adicionarTransacao(t);

            if (mainController != null) {
                mainController.atualizarTabela();
            }
            fechar();
        } catch (NumberFormatException e) {
            exibirErro("Digite um valor numérico válido.");
        } catch (EntradaInvalidaException | SQLException e) {
            exibirErro(e.getMessage());
        }
    }

    @FXML
    private void cancelar() {
        fechar();
    }

    private void fechar() {
        Stage stage = (Stage) txtDescricao.getScene().getWindow();
        stage.close();
    }

    private void exibirErro(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}