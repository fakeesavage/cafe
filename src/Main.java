import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class Main extends Application {

    public static final int tables = 10;
    public static final double cost_per_minute = 5;

    private Label label;
    private ComboBox<String> comboBox;
    private Button startButton;
    private Button endButton;
    private Button statsButton;
    private TextArea textArea;

    private Map<String, LocalDateTime> startTimeMap;
    private Map<String, Double> totalCostMap;
    private Map<String, Long> totalTimeMap;
    private Map<String, Integer> frequencyMap;
    private double totalIncome;
    private long totalVisits;

    public void start(Stage primaryStage) {
        startTimeMap = new HashMap<>();
        totalCostMap = new HashMap<>();
        totalTimeMap = new HashMap<>();
        frequencyMap = new HashMap<>();
        totalIncome = 0;
        totalVisits = 0;

        for (int i = 1; i <= tables; i++) {
            String table = "Столик " + i;
            startTimeMap.put(table, null);
            totalCostMap.put(table, 0.0);
            totalTimeMap.put(table, 0L);
            frequencyMap.put(table, 0);
        }

        label = new Label("Выберите столик:");
        comboBox = new ComboBox<>();
        startButton = new Button("Начать посещение");
        endButton = new Button("Закончить посещение");
        statsButton = new Button("Показать статистику");
        textArea = new TextArea();

        for (int i = 1; i <= tables; i++) {
            comboBox.getItems().add("Столик " + i);
        }

        comboBox.setPrefWidth(150);
        startButton.setPrefWidth(150);
        endButton.setPrefWidth(150);
        statsButton.setPrefWidth(150);
        textArea.setPrefSize(400, 300);
        textArea.setEditable(false);

        GridPane gridPane = new GridPane();

        gridPane.add(label, 0, 0);
        gridPane.add(comboBox, 1, 0);
        gridPane.add(startButton, 0, 1);
        gridPane.add(endButton, 1, 1);
        gridPane.add(statsButton, 0, 2);
        gridPane.add(textArea, 0, 3, 2, 1);

        gridPane.setPadding(new Insets(10));
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setAlignment(Pos.CENTER);
        GridPane.setHalignment(label, HPos.RIGHT);
        GridPane.setHalignment(startButton, HPos.CENTER);
        GridPane.setHalignment(endButton, HPos.CENTER);
        GridPane.setHalignment(statsButton, HPos.CENTER);

        startButton.setOnAction(this::handleStart);
        endButton.setOnAction(this::handleEnd);
        statsButton.setOnAction(this::handleStats);

        Scene scene = new Scene(gridPane);
        primaryStage.setScene(scene);

        primaryStage.setTitle("Антикафе");
        primaryStage.setWidth(450);
        primaryStage.setHeight(400);

        primaryStage.show();
    }

    private void handleStart(javafx.event.ActionEvent event) {
        String table = comboBox.getValue();

        if (table == null) {
            textArea.setText("Пожалуйста, выберите столик из списка.");
        } else {
            if (startTimeMap.get(table) == null) {
                LocalDateTime startTime = LocalDateTime.now();
                startTimeMap.put(table, startTime);
                frequencyMap.put(table, frequencyMap.get(table) + 1);
                textArea.setText("Посещение столика " + table + " началось в " + startTime );
            } else {
                textArea.setText("Столик " + table + " уже занят.");
            }
        }
    }

    private void handleEnd(javafx.event.ActionEvent event) {
        String table = comboBox.getValue();

        if (table == null) {
            textArea.setText("Пожалуйста, выберите столик из списка.");
        } else {
            if (startTimeMap.get(table) != null) {
                LocalDateTime endTime = LocalDateTime.now();
                long duration = Duration.between(startTimeMap.get(table), endTime).toMinutes();
                double cost = cost_per_minute * duration;
                startTimeMap.put(table, null);
                totalCostMap.put(table, totalCostMap.get(table) + cost);
                totalTimeMap.put(table, totalTimeMap.get(table) + duration);
                totalIncome += cost;
                totalVisits++;

                textArea.setText("Посещение столика " + table + " завершилось.\n" +
                        "Продолжительность: " + duration + " минут\n" +
                        "Стоимость: " + cost + " руб.\n" +
                        "Общая стоимость за столик: " + totalCostMap.get(table) + " руб.\n" +
                        "Общий доход антикафе: " + totalIncome + " руб.\n" +
                        "Общее количество посещений: " + totalVisits);
            } else {
                textArea.setText("Столик " + table + " не занят. Выберите другой столик.");
            }
        }
    }

    private void handleStats(javafx.event.ActionEvent event) {
        textArea.setText("Статистика по столикам:\n");

        for (int i = 1; i <= tables; i++) {
            String table = "Столик " + i;
            textArea.appendText(table + ":\n");

            if (startTimeMap.get(table) != null) {
                textArea.appendText("Столик занят.\n");
                textArea.appendText("Время начала посещения: " + startTimeMap.get(table) + "\n");
                textArea.appendText("Продолжительность текущего посещения: " +
                        Duration.between(startTimeMap.get(table), LocalDateTime.now()).toMinutes() + " минут\n");
            } else {
                textArea.appendText("Столик свободен.\n");
            }

            textArea.appendText("Общая стоимость за столик: " + totalCostMap.get(table) + " руб.\n");
            textArea.appendText("Общее время посещения: " + totalTimeMap.get(table) + " минут\n");
            textArea.appendText("Частота выбора столика: " + frequencyMap.get(table) + " раз\n\n");
        }

        textArea.appendText("Общая статистика антикафе:\n");
        textArea.appendText("Общий доход антикафе: " + totalIncome + " руб\n");
        textArea.appendText("Общее количество посещений антикафе: " + totalVisits + " раз\n");

        if (totalVisits > 0) {
            textArea.appendText("Среднее время посещения одного столика: " +
                    totalIncome / totalVisits + " мин.\n");
        }

        String maxIncomeTable = findMaxIncomeTable();
        textArea.appendText("Столик с максимальной общей стоимостью: " + maxIncomeTable + "\n");
    }

    private String findMaxIncomeTable() {
        String maxIncomeTable = null;
        double maxIncome = Double.MIN_VALUE;

        for (int i = 1; i <= tables; i++) {
            String table = "Столик " + i;
            if (totalCostMap.get(table) > maxIncome) {
                maxIncome = totalCostMap.get(table);
                maxIncomeTable = table;
            }
        }

        return maxIncomeTable;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
