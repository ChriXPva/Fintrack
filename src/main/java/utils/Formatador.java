package utils;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Formatador {

    private static final Locale LOCALE_PT_BR = new Locale("pt", "BR");
    private static final NumberFormat FORMATO_MOEDA = NumberFormat.getCurrencyInstance(LOCALE_PT_BR);
    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static String formatarMoeda(double valor) {
        return FORMATO_MOEDA.format(valor);
    }

    public static String formatarData(LocalDate data) {
        if (data == null) {
            return "";
        }
        return data.format(FORMATO_DATA);
    }

    public static String obterNomeMes(int mes) {
        switch (mes) {
            case 1: return "Janeiro";
            case 2: return "Fevereiro";
            case 3: return "Março";
            case 4: return "Abril";
            case 5: return "Maio";
            case 6: return "Junho";
            case 7: return "Julho";
            case 8: return "Agosto";
            case 9: return "Setembro";
            case 10: return "Outubro";
            case 11: return "Novembro";
            case 12: return "Dezembro";
            default: return "";
        }
    }
}