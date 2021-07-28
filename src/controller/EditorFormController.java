package controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
    private int findOffSet = -1;
    private List<Index> searchList = new ArrayList<>();

    public void initialize(){
        pneFindOrReplace.setVisible(false);

        ChangeListener listener = (ChangeListener<String>) (observable, oldValue, newValue) -> {
            Pattern regEx = Pattern.compile(newValue);
            Matcher matcher = regEx.matcher(txtEditor.getText());

            searchList.clear();
            while (matcher.find()){
                searchList.add(new Index(matcher.start(), matcher.end()));
            }
        };

        txtSearch.textProperty().addListener(listener);


        pneRoot.addEventHandler(KeyEvent.KEY_PRESSED,keyEvent -> {
            if (KeyCode.ESCAPE == keyEvent.getCode()){
                pneFindOrReplace.setVisible(false);
            }
        });
    }

    public void mnuNew_OnAction(ActionEvent actionEvent) {
        txtEditor.clear();
        txtEditor.requestFocus();
    }

    public void mnuOpen_OnAction(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open File");
        File file = fileChooser.showOpenDialog(txtEditor.getScene().getWindow());

        if (file == null) return;

        txtEditor.clear();
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
    }

    public void mnuSaveAs_OnAction(ActionEvent actionEvent) {
    }

    public void mnuExit_OnAction(ActionEvent actionEvent) {
    }

    public void mnuCut_OnAction(ActionEvent actionEvent) {
    }

    public void mnuCopy_OnAction(ActionEvent actionEvent) {
    }

    public void mnuPaste_OnAction(ActionEvent actionEvent) {
    }

    public void mnuFind_OnAction(ActionEvent actionEvent) {
        txtSearch.clear();
        findOffSet = -1;
        pneFindOrReplace.setVisible(true);
        makeReplaceVisible(false);
        txtSearch.requestFocus();
    }

    public void mnuReplace_OnAction(ActionEvent actionEvent) {
        txtReplace.clear();
        findOffSet = -1;
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
//        int currentIndex = txtEditor.getSelection().getStart();
//        System.out.println(currentIndex);
        if (!searchList.isEmpty()){
            findOffSet++;
//            int count=0;
//            while ((searchList.get(count).indexStart)>currentIndex){
//                findOffSet = count;
//                count++;
//            }
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
    }

    public void btnReplaceAll_OnAction(ActionEvent actionEvent) {
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
