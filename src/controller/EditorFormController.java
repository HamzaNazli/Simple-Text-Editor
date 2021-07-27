package controller;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;

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

    public void initialize(){
        pneFindOrReplace.setVisible(false);

        pneRoot.addEventHandler(KeyEvent.KEY_PRESSED,keyEvent -> {
            if (KeyCode.ESCAPE == keyEvent.getCode()){
                pneFindOrReplace.setVisible(false);
            }
        });
    }

    public void mnuNew_OnAction(ActionEvent actionEvent) {
    }

    public void mnuOpen_OnAction(ActionEvent actionEvent) {
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
        pneFindOrReplace.setVisible(true);
        makeReplaceVisible(false);
    }

    public void mnuReplace_OnAction(ActionEvent actionEvent) {
        if (pneFindOrReplace.isVisible()){
           makeReplaceVisible(true);
        }
        pneFindOrReplace.setVisible(true);
    }

    public void mnuSelectAll_OnAction(ActionEvent actionEvent) {
    }

    public void mnuAbout_OnAction(ActionEvent actionEvent) {
    }

    void makeReplaceVisible(boolean exp){
        lblReplace.setVisible(exp);
        btnReplace.setVisible(exp);
        btnReplaceAll.setVisible(exp);
        txtReplace.setVisible(exp);
    }
}
