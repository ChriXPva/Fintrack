package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import service.FinTrackerService;
import utils.Formatador;

import java.sql.SQLException;

public class RelatorioController {

    @FXML private Label lblSaldo;
    private final FinTrackerService service = new FinTrackerService();

    @FXML
    public void initialize() {
        try {
            double saldo = service.calcularSaldoTotal();
            lblSaldo.setText("Saldo Total: " + Formatador.formatarMoeda(saldo));
        } catch (SQLException e) {
            lblSaldo.setText("Erro ao carregar saldo");
        }
    }

    @FXML
    private void fecharJanela() {
        Stage stage = (Stage) lblSaldo.getScene().getWindow();
        stage.close();
    }
}