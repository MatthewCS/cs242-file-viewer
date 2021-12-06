package com.fileviewer.fileviewer;

import com.fileviewer.tokenizer.JsonTokenizer;
import javafx.application.Application;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;


public class FileWindow extends Application {

    private String filename;
    private GridPane fileContentsGrid;
    private int currentFileState;

    private static boolean DEBUG_MODE;

    private final int FILE_STATE_NONE = 0;
    private final int FILE_STATE_IMAGE = 1;
    private final int FILE_STATE_CSV = 2;
    private final int FILE_STATE_JSON = 3;

    private final int CSV_MAX_COLS = 500;
    private final int CSV_MAX_ROWS = 500;

    public static void main(String [] args)
    {
        DEBUG_MODE = false;
        launch(args);
    }

    @Override
    public void start(Stage primaryStage)
    {
        this.filename = " - none - ";
        this.currentFileState = this.FILE_STATE_NONE;

        primaryStage.setTitle("File Viewer");
        primaryStage.setMinWidth(375);
        primaryStage.setMinHeight(375);

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        grid.setBackground(new Background(new BackgroundFill(
                new LinearGradient(
                        0, 0, 0, 0.4, true,
                        CycleMethod.NO_CYCLE,
                        new Stop(0, Color.web("222222")), new Stop(1, Color.web("1b1b1b"))),
                CornerRadii.EMPTY, Insets.EMPTY
        )));

        Scene scene = new Scene(grid, 800, 800);
        primaryStage.setScene(scene);

        ScrollPane fileContentsScrollPane = new ScrollPane();
        fileContentsScrollPane.setPannable(true);
        fileContentsScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        fileContentsScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        this.fileContentsGrid = new GridPane();
        fileContentsScrollPane.setContent(this.fileContentsGrid);
        grid.add(fileContentsScrollPane, 0, 0);

        // Blank space between the image and the load / close buttons
        Pane blankPaneBelowImage = new Pane();
        blankPaneBelowImage.setMinHeight(25);
        grid.add(blankPaneBelowImage, 0, 1);

        HBox btnsHBox = new HBox(25 );
        btnsHBox.setAlignment(Pos.CENTER);
        grid.add(btnsHBox, 0, 2);

        Button loadFileBtn = new Button("Load file");
        loadFileBtn.setStyle("-fx-background-color: #222222; -fx-text-fill: white;");
        btnsHBox.getChildren().add(loadFileBtn);

        loadFileBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                FileChooser fc = new FileChooser();
                FileChooser.ExtensionFilter[] fileExtensions = {
                        new FileChooser.ExtensionFilter("Image file",
                                "*.jpg", "*.jpeg", "*.png", "*.gif", "*.bmp"),
                        new FileChooser.ExtensionFilter("CSV file", "*.csv"),
                        new FileChooser.ExtensionFilter("JSON file", "*.json")
                };
                fc.setTitle("Load file");
                for(FileChooser.ExtensionFilter ef: fileExtensions)
                    fc.getExtensionFilters().add(ef);

                // Select a file
                File fcFile = fc.showOpenDialog(primaryStage);
                if (fcFile != null) {
                    String fcFilename = fcFile.getName();
                    String fcAbsPath = fcFile.getAbsolutePath();
                    String[] extensionSplitArr = fcFilename.split("\\.");
                    String extension = extensionSplitArr[extensionSplitArr.length - 1];

                    // What type of file do we have?
                    switch(extension)
                    {
                        // Do we have an image?
                        case "png":
                        case "jpg":
                        case "jpeg":
                        case "gif":
                        case "bmp":
                            loadImage(fcFilename, new Image("file:"+fcAbsPath));
                            primaryStage.setTitle("File Viewer (" + filename + ")");
                            break;
                        // Do we have a CSV?
                        case "csv":
                            try {
                                String fileContents = "";
                                String fileLine = "";
                                BufferedReader bufferedReader = new BufferedReader(
                                        new FileReader(fcFile)
                                );

                                while((fileLine = bufferedReader.readLine()) != null)
                                    fileContents += fileLine + "\n";

                                loadCSV(fcFilename, fileContents);
                                primaryStage.setTitle("File Viewer (" + filename + ")");
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            break;
                        case "json":
                            try {
                                String fileContents = "";
                                String fileLine = "";
                                BufferedReader bufferedReader = new BufferedReader(
                                        new FileReader(fcFile)
                                );

                                while((fileLine = bufferedReader.readLine()) != null)
                                    fileContents += fileLine + "\n";

                                loadJson(fcFilename, fileContents);
                                primaryStage.setTitle("File Viewer (" + filename + ")");
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            break;
                        default:
                            break;
                    }
                }
            }
        });

        Button closeWindowBtn = new Button("Close window");
        closeWindowBtn.setStyle("-fx-background-color: #9c2222; -fx-text-fill: white;");
        btnsHBox.getChildren().add(closeWindowBtn);

        closeWindowBtn.setOnAction(new EventHandler<ActionEvent>() {
            private GridPane fileContentsGrid;

            @Override
            public void handle(ActionEvent event) {
                primaryStage.close();
                System.exit(0);
            }
            public EventHandler<ActionEvent> init(GridPane gp)
            {
                this.fileContentsGrid = gp;
                return this;
            }
        }.init(this.fileContentsGrid));

        // Load a test image / text csv
        if(DEBUG_MODE) {

            /*
            Image img = new Image("file:./res/icon1.png");
            this.loadImage("icon1.png", img);
            */

            /*
            String csvData1 = "HEADER 1, HEADER 2, HEADER 3, HEADER 4\n";
            csvData1 += "Clarkson, SUNY Potsdam, SUNY Canton, Syracuse, SUNY Albany, SUNY Poly, OCC, SLU :(\n";
            csvData1 += "$5.99, $6.99, 10%, 250%, -$15.00\n";
            this.loadCSV("Test data", csvData1);
            */

            /*
            String csvData2 = "manufacturer,model,displ,year,cyl,trans,drv,cty,hwy,fl,class\r\n";
            csvData2 += "honda,civic,1.6,1999,4,manual(m5),f,28,33,r,subcompact\r\n";
            csvData2 += "honda,civic,1.6,1999,4,auto(l4),f,24,32,r,subcompact\r\n";
            csvData2 += "honda,civic,1.6,1999,4,manual(m5),f,25,32,r,subcompact\r";
            csvData2 += "honda,civic,1.6,1999,4,manual(m5),f,23,29,p,subcompact\r";
            csvData2 += "honda,civic,1.6,1999,4,auto(l4),f,24,32,r,subcompact\r";
            csvData2 += "honda,civic,1.8,2008,4,manual(m5),f,26,34,r,subcompact\n";
            csvData2 += "honda,civic,1.8,2008,4,auto(l5),f,25,36,r,subcompact\n";
            csvData2 += "honda,civic,1.8,2008,4,auto(l5),f,24,36,c,subcompact\n";
            csvData2 += "honda,civic,2,2008,4,manual(m6),f,21,29,p,subcompact\n";
            this.loadCSV("honda.csv", csvData2);
            */

            String jsonInput = "{\n";
            jsonInput += "  \"Integer\": 1,\n";
            jsonInput += "  \"Decimal\": 3.14,\n";
            jsonInput += "  \"String\": \"delectus aut autem\",\n";
            jsonInput += "  \"Bool1\": true,\n";
            jsonInput += "  \"Bool2\": false,\n";
            jsonInput += "  \"List\": [\n";
            jsonInput += "    100,\n";
            jsonInput += "    200,\n";
            jsonInput += "    300\n";
            jsonInput += "  ],\n";
            jsonInput += "  \"Object\": {\n";
            jsonInput += "    \"ElemA\": \"Hello, \",\n";
            jsonInput += "    \"ElemB\": \"World!\"\n";
            jsonInput += "  },\n";
            jsonInput += "  \"All done!\": true\n";
            jsonInput += "}";
            this.loadJson("example.json", jsonInput);

            primaryStage.setTitle("File Viewer (" + this.filename + ")");
        }

        primaryStage.show();
    }

    public void loadImage(String filename, Image img)
    {
        this.filename = filename;
        this.currentFileState = this.FILE_STATE_IMAGE;

        ImageView imgViewer = new ImageView(img);
        imgViewer.setSmooth(true);
        imgViewer.setPreserveRatio(true);

        // Background image
        Image bgImg = new Image("file:./res/grid.png");
        BackgroundImage gridBackgroundImage = new BackgroundImage(bgImg,
                BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT,
                BackgroundPosition.DEFAULT,
                new BackgroundSize(16, 16, false, false, false, false));

        this.fileContentsGrid.getChildren().clear();
        this.fileContentsGrid.add(imgViewer, 0, 0);
        this.fileContentsGrid.setBackground(new Background(gridBackgroundImage));
    }

    public void loadCSV(String filename, String csvRawText)
    {
        this.filename = filename;
        this.currentFileState = this.FILE_STATE_CSV;

        ObservableList<ObservableList<String>> csvEntries = FXCollections.observableArrayList();

        // Populate our CSV array
        String[] csvRawLines = csvRawText.split("\\R");
        for(String line : csvRawLines)
        {
            ObservableList<String> row = FXCollections.observableArrayList();
            for(String entry : line.split(","))
                row.add(entry);
            csvEntries.add(row);
        }

        // We want to have a certain number of rows and cols
        // Shrink rows
        if(csvEntries.size() > this.CSV_MAX_ROWS)
            csvEntries.subList(this.CSV_MAX_ROWS, csvEntries.size()).clear();
        // Shrink cols & get max col size
        int maxColSize = 0;
        for(int i = 0; i < csvEntries.size(); i += 1)
        {
            if(csvEntries.get(i).size() > this.CSV_MAX_COLS)
                csvEntries.get(i).subList(this.CSV_MAX_COLS, csvEntries.get(i).size()).clear();
            if(csvEntries.get(i).size() > maxColSize)
                maxColSize = csvEntries.get(i).size();
        }
        // Grow all cols to match max size
        for(int i = 0; i < csvEntries.size(); i += 1)
            for(int j = csvEntries.get(i).size(); j < maxColSize; j += 1)
                csvEntries.get(i).add("");

        // We need to put that into a table view
        TableView<ObservableList<String>> csvTable = new TableView<ObservableList<String>>();
        csvTable.setItems(csvEntries);
        for (int i = 0; i < maxColSize; i++) {
            final int colIdx = i;
            final TableColumn<ObservableList<String>, String> column = new TableColumn<>(
                    "Col " + (colIdx + 1)
            );
            column.setCellValueFactory(
                    c -> new ReadOnlyObjectWrapper<>(c.getValue().get(colIdx))
            );
            column.setMinWidth(80);
            column.setEditable(false);
            csvTable.getColumns().add(column);
        }

        this.fileContentsGrid.getChildren().clear();
        this.fileContentsGrid.setBackground(null);
        this.fileContentsGrid.add(csvTable, 0, 0);
    }

    public void loadJson(String filename, String jsonRawText)
    {
        this.filename = filename;

        SwingNode jsonSwingNode = new SwingNode();
        JTextPane jsonTextPane = new JTextPane();
        ArrayList<JsonTokenizer.TokenPos> tokenPositions = JsonTokenizer.tokenize(jsonRawText);

        jsonTextPane.setFont(new Font("Courier New", Font.PLAIN, 16));
        jsonTextPane.setMinimumSize(new java.awt.Dimension(500, 500));
        jsonTextPane.setBackground(java.awt.Color.darkGray);
        jsonTextPane.setEditable(false);

        int jsonIndex = 0;
        int tokenIndex = 0;
        while(jsonIndex < jsonRawText.length())
        {
            JsonTokenizer.TokenPos tp = tokenPositions.get(tokenIndex);

            // Are we on a token?
            if(tp.start == jsonIndex)
            {
                MutableAttributeSet colorAttribute = new SimpleAttributeSet(jsonTextPane.getInputAttributes());
                String tokenString = jsonRawText.substring(tp.start, tp.start+tp.length);
                Color tokenColor = tp.token.getColor();
                java.awt.Color awtTokenColor = new java.awt.Color(
                        (float)tokenColor.getRed(),
                        (float)tokenColor.getGreen(),
                        (float)tokenColor.getBlue(),
                        (float)tokenColor.getOpacity()
                );

                StyleConstants.setForeground(colorAttribute, awtTokenColor);

                try {
                    jsonTextPane.getStyledDocument().insertString(
                            jsonTextPane.getDocument().getLength(),
                            tokenString,
                            colorAttribute);
                } catch (BadLocationException ble) {
                    System.err.println(ble);
                }

                jsonIndex += tp.length;
                tokenIndex += 1;
            }
            // If we are not on a token, do this instead
            else
            {
                jsonIndex += 1;
            }
        }
        this.fileContentsGrid.getChildren().clear();
        this.fileContentsGrid.setBackground(null);
        this.fileContentsGrid.add(jsonSwingNode, 0, 0);
        jsonSwingNode.setContent(jsonTextPane);
    }
}
