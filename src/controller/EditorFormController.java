package controller;

import com.sun.javafx.scene.control.skin.TextAreaSkin;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Rectangle2D;
import javafx.print.PrinterJob;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.awt.*;
import java.io.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EditorFormController {
    public AnchorPane pneFindOrReplace;
    public TextField txtSearch;
    public Label lblFind;
    public Label lblReplace;
    public TextField txtReplace;
    public Button btnFindNext;
    public Button btnFindPrevious;
    public Button btnReplace;
    public Button btnReplaceAll;
    public AnchorPane pneRoot;
    public TextArea txtEditor;
    public Tooltip tltpFindNext;
    public Tooltip tltpFindPrevious;
    public Tooltip tltpReplaceNext;
    public Tooltip tltpReplaceAll;
    private int findOffSet = -1;
    private List<Index> searchList = new ArrayList<>();
    private String address = null;
    private PrinterJob printerJob;
    private List<Rectangle> rec = new ArrayList<>();
//    Tooltip tooltip;

    public void initialize(){
        pneFindOrReplace.setVisible(false);
        this.printerJob = PrinterJob.createPrinterJob();


        ChangeListener listener = (ChangeListener<String>) (observable, oldValue, newValue) -> {
           searchMatches(newValue);

        };

        txtSearch.textProperty().addListener(listener);

        pneRoot.addEventHandler(KeyEvent.KEY_PRESSED,keyEvent -> {
            if (KeyCode.ESCAPE == keyEvent.getCode()){
                pneFindOrReplace.setVisible(false);
            }
        });
        tooltipStartTiming(tltpFindNext);
        tooltipStartTiming(tltpFindPrevious);
        tooltipStartTiming(tltpReplaceNext);
        tooltipStartTiming(tltpReplaceAll);


    }

    private void searchMatches(String newValue) {
        Pattern regEx = Pattern.compile(newValue);
        Matcher matcher = regEx.matcher(txtEditor.getText());

        searchList.clear();
        while (matcher.find()){
            searchList.add(new Index(matcher.start(), matcher.end()));
        }

        for (Index index : searchList){
            if (index.indexStart > txtEditor.getCaretPosition()){
                findOffSet = searchList.indexOf(index);
                break;
            }
        }

        if (searchList.isEmpty()){
            findOffSet = -1;
        }

        Text text = (Text) txtEditor.lookup("Text");
        Parent grp = txtEditor.lookup("Text").getParent().getParent();
        TextAreaSkin skin = (TextAreaSkin) txtEditor.getSkin();


        try {
            Field fldChildren = Parent.class.getDeclaredField("children");
            fldChildren.setAccessible(true);
            ObservableList<Node> children = (ObservableList<Node>) fldChildren.get(grp);
            Node node1 = children.get(0);
            Node node2 = children.get(1);
            Node node3 = children.get(2);

            loadDefaultChildren(children,node1,node2,node3);

            rec.clear();

            for (Index index : searchList){
                double x = skin.getCharacterBounds(index.indexStart).getMinX();
                double y = skin.getCharacterBounds(index.indexStart).getMinY();
                double width = skin.getCharacterBounds(index.indexEnd).getMinX()-skin.getCharacterBounds(index.indexStart).getMinX();
                double height = skin.getCharacterBounds(index.indexEnd).getMaxY()-skin.getCharacterBounds(index.indexStart).getMinY();
                Rectangle rectangle = new Rectangle(x,y,width,height);
                rectangle.setFill(Color.rgb(155,125,45,0.25));
                rec.add(rectangle);
            }

            children.addAll(rec);

        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void loadDefaultChildren(ObservableList<Node> children,Node... nodes){
        children.clear();
       for (Node node : nodes){
           children.add(node);
       }
    }

    public static void tooltipStartTiming(Tooltip tooltip) {
        try {
            Field fieldBehavior = tooltip.getClass().getDeclaredField("BEHAVIOR");
            fieldBehavior.setAccessible(true);
            Object objBehavior = fieldBehavior.get(tooltip);

            Field fieldTimer = objBehavior.getClass().getDeclaredField("activationTimer");
            fieldTimer.setAccessible(true);
            Timeline objTimer = (Timeline) fieldTimer.get(objBehavior);

            objTimer.getKeyFrames().clear();
            objTimer.getKeyFrames().add(new KeyFrame(new Duration(250)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void mnuNew_OnAction(ActionEvent actionEvent) {
        txtEditor.clear();
        txtEditor.requestFocus();
        address = null;
    }

    public void mnuOpen_OnAction(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("All Text Files","*.txt","*.html"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("All Files","*"));
        File file = fileChooser.showOpenDialog(txtEditor.getScene().getWindow());

        if (file == null) return;

        txtEditor.clear();

        address = file.getAbsolutePath();
        try (FileReader fr = new FileReader(file);
             BufferedReader br = new BufferedReader(fr)) {

            String line = null;

            while ((line = br.readLine()) != null){
              txtEditor.appendText(line + '\n');
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void mnuSave_OnAction(ActionEvent actionEvent) {
        if (address != null){
            try (FileWriter fw = new FileWriter(address);
                 BufferedWriter bw = new BufferedWriter(fw)) {

                bw.write(txtEditor.getText());
            }catch (IOException e){
                e.printStackTrace();
            }
        }else{
            mnuSaveAs_OnAction(actionEvent);
        }
    }

    public void mnuSaveAs_OnAction(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save File As");
        File file = fileChooser.showSaveDialog(txtEditor.getScene().getWindow());


//        if(address == null){
//            System.out.println("Null .....");
//        }else{
//            System.out.println(address);
//        }

        if (file == null) return;

        try (FileWriter fw = new FileWriter(file);
             BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(txtEditor.getText());
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void mnuExit_OnAction(ActionEvent actionEvent) {
//        StringBuilder sb = new StringBuilder();
//        if (address != null) {
//            try (FileReader fr = new FileReader(address);
//                 BufferedReader br = new BufferedReader(fr)) {
//
//                String line = null;
//                while ((line = br.readLine()) != null) {
//                    sb.append(line + '\n');
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//
//            if (sb.toString() != txtEditor.getText()){
//
//            }
        if (txtEditor.getText() != null){
            Alert exitAlert = new Alert(Alert.AlertType.NONE, "Do you want to Save?", ButtonType.YES, ButtonType.NO);
            ButtonType buttonType = exitAlert.showAndWait().orElse(ButtonType.YES);
            if (ButtonType.YES.equals(buttonType)){
                mnuSave_OnAction(actionEvent);
            }
        }
        ((Stage)txtEditor.getScene().getWindow()).close();

    }

    public void mnuCut_OnAction(ActionEvent actionEvent) {
        txtEditor.cut();
    }

    public void mnuCopy_OnAction(ActionEvent actionEvent) {
        txtEditor.copy();
    }

    public void mnuPaste_OnAction(ActionEvent actionEvent) {
        txtEditor.paste();
    }

    public void mnuFind_OnAction(ActionEvent actionEvent) {
        txtSearch.clear();
//        findOffSet = -1;
        pneFindOrReplace.setVisible(true);
        makeReplaceVisible(false);
        txtSearch.requestFocus();
    }

    public void mnuReplace_OnAction(ActionEvent actionEvent) {
        txtReplace.clear();
//        findOffSet = -1;
        if (pneFindOrReplace.isVisible()){
           makeReplaceVisible(true);
        }
        pneFindOrReplace.setVisible(true);
        txtReplace.requestFocus();
    }

    public void mnuSelectAll_OnAction(ActionEvent actionEvent) {
        txtEditor.selectAll();
    }

    public void mnuAbout_OnAction(ActionEvent actionEvent) {
    }

    void makeReplaceVisible(boolean exp){
        lblReplace.setVisible(exp);
        btnReplace.setVisible(exp);
        btnReplaceAll.setVisible(exp);
        txtReplace.setVisible(exp);
    }

    public void btnFindNext_OnAction(ActionEvent actionEvent) {
        if (!searchList.isEmpty()){
            findOffSet++;

            if (findOffSet >= searchList.size()) {
                findOffSet = 0;
            }
            txtEditor.selectRange(searchList.get(findOffSet).indexStart, searchList.get(findOffSet).indexEnd);
        }
    }

    public void btnFindPrevious_OnAction(ActionEvent actionEvent) {
        if (!searchList.isEmpty()){
            findOffSet--;
            if (findOffSet <0) {
                findOffSet = searchList.size()-1;
            }
            txtEditor.selectRange(searchList.get(findOffSet).indexStart, searchList.get(findOffSet).indexEnd);
        }
    }

    public void btnReplace_OnAction(ActionEvent actionEvent) {
        if (findOffSet == -1) return;
        txtEditor.replaceText(searchList.get(findOffSet).indexStart,searchList.get(findOffSet).indexEnd,txtReplace.getText());
        searchMatches(txtSearch.getText());
    }

    public void btnReplaceAll_OnAction(ActionEvent actionEvent) {
        while (!searchList.isEmpty()){
            txtEditor.replaceText(searchList.get(0).indexStart,searchList.get(0).indexEnd,txtReplace.getText());
            searchMatches(txtSearch.getText());
        }
    }

    public void mnuPrint_OnAction(ActionEvent actionEvent) {
        boolean printDialog = printerJob.showPrintDialog(txtEditor.getScene().getWindow());
        if (printDialog){
            printerJob.printPage(txtEditor.lookup("Text"));
        }

        
    }

    public void mnuPageSetup_OnAction(ActionEvent actionEvent) {
        printerJob.showPageSetupDialog(txtEditor.getScene().getWindow());
    }
}

class Index{
    int indexStart;
    int indexEnd;

    public Index(int indexStart, int indexEnd) {
        this.indexStart = indexStart;
        this.indexEnd = indexEnd;
    }
}
