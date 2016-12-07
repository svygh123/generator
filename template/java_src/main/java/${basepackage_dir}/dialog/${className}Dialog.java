package com.isoftoon.ld.fx.dialog;

<#include "/macro.include"/>
<#include "/java_copyright.include">
<#assign className = table.className>
<#assign classNameLower = className?uncap_first>
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.isoftoon.fx.dialog.Dialog;
import com.isoftoon.ld.fx.model.${className};
import com.isoftoon.ld.fx.service.${className}Service;
import com.isoftoon.orm.Page;
import com.isoftoon.orm.PageRequest;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Callback;

public class ${className}Dialog extends Stage implements Initializable {
    Logger logger = LoggerFactory.getLogger(${className}Dialog.class);

    @FXML private AnchorPane mainAnchorPane;

	@FXML TableView<${className}> ${classNameLower}Table;
    <#list table.columns as column>
    @FXML private TableColumn<${className}, ${column.javaType}> ${column.columnNameLower};
    </#list>

	@FXML private TextField searchText;

	@FXML private Pagination pagination;

	private IntegerProperty limit;
    private IntegerProperty totalCount;

    private ${className}Service ${classNameLower}Service;
	private ObservableList<${className}> ${classNameLower}s = FXCollections.observableArrayList();
	private Callback<${className},${className}> callback;

	public ${className}Dialog(Callback<${className},${className}> callback) {
	    this.callback = callback;
	    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("${className}Dialog.fxml"));
        fxmlLoader.setController(this);
        try {
            setTitle("选择");
            setScene(new Scene((Parent) fxmlLoader.load()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ${classNameLower}Service = new ${className}Service();
        limit = new SimpleIntegerProperty(25);
        totalCount = new SimpleIntegerProperty(1);

        <#list table.columns as column>
        ${column.columnNameLower}.setCellValueFactory(new PropertyValueFactory<${className}, ${column.javaType}>("${column.columnNameLower}"));
        </#list>

        totalCount.addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                pagination.setPageCount(((int) newValue + limit.get() - 1) / limit.get());
            }
        });
        limit.addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                changeTableView(pagination.getCurrentPageIndex(), newValue.intValue());
            }
        });
        pagination.setMaxPageIndicatorCount(2);
        pagination.currentPageIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                changeTableView(newValue.intValue(), limit.get());
            }
        });
        init();
    }

    private void changeTableView(int pageIndex, int limit) {
        Service<Page<${className}>> service = new Service<Page<${className}>>() {
            @Override
            protected Task<Page<${className}>> createTask() {
                return new Task<Page<${className}>>() {
                    @Override
                    protected Page<${className}> call() throws Exception {
                        PageRequest<${className}> pageRequest = new PageRequest<${className}>();
                        pageRequest.setPageSize(limit);
                        pageRequest.setPageNumber(pageIndex + 1);
                        Page<${className}> page = ${classNameLower}Service.findByPage(pageRequest, searchText.getText());
                        return page;
                    }
                };
            }
        };
        service.start();
        service.setOnRunning((WorkerStateEvent event) -> {});
        service.setOnSucceeded((WorkerStateEvent event) -> {
            @SuppressWarnings("unchecked")
            Page<${className}> page = (Page<${className}>) event.getSource().getValue();
            logger.debug("列表：" + page.getResult());
            ${classNameLower}Table.getItems().clear();
            ${classNameLower}Table.setItems(null);
            ${classNameLower}s.addAll(page.getResult());
            ${classNameLower}Table.setItems(${classNameLower}s);
            pagination.setCurrentPageIndex(pageIndex);
            totalCount.setValue(page.getTotalCount());
        });
        service.setOnFailed((WorkerStateEvent event) -> {
            event.getSource().getException().printStackTrace();
            logger.error("查询出错",event.getSource().getException().getStackTrace()[0].toString());
            Dialog.showThrowable("查询出错", event.getSource().getException().getStackTrace()[0].toString(), event.getSource().getException());
        });
    }

    public void init() {
        resetPage();
        changeTableView(0, limit.get());
    }

    public void resetPage() {
        pagination.setPageCount(1);
    }

	@FXML
	public void search(ActionEvent event) {
	    init();
	}
	@FXML
	public void ok(ActionEvent event) {
	    ${className} ${classNameLower} = ${classNameLower}Table.getSelectionModel().getSelectedItem();
        if (${classNameLower} != null) {
            callback.call(${classNameLower});
            close();
        }
	}
	@FXML
	public void cancel(ActionEvent event) {
	    close();
	}
	// <Button fx:id="select${className}Button" defaultButton="true" mnemonicParsing="false" prefHeight="30.0" text="选择" GridPane.columnIndex="2" />
	// @FXML private Button selectMaterialButton;
	/*
	select${className}Button.setOnAction(event -> {
        ${className}Dialog dlg = new ${className}Dialog(new Callback<${className},${className}>(){
        @Override
        public ${className} call(${className} param) {
            if (param instanceof ${className}) {
                ${className} ${classNameLower} = param;

                inStock.setMaterialCode(${classNameLower}.getCode());

                materialCodeText.setText(${classNameLower}.getCode());
            }
            return param;
        }});
        dlg.showAndWait();
    });
    */
}
