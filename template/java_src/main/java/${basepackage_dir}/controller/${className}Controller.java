<#include "/macro.include"/>
<#include "/java_copyright.include">
<#assign className = table.className>
<#assign classNameLower = className?uncap_first>
package ${basepackage}.controller;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.isoftoon.fx.dialog.Dialog;
import com.isoftoon.ld.fx.config.CurrentUserSession;
import com.isoftoon.ld.fx.model.${className};
import com.isoftoon.ld.fx.model.Opperson;
import com.isoftoon.ld.fx.service.${className}Service;
import com.isoftoon.ld.fx.service.OppersonService;
import com.isoftoon.orm.Page;
import com.isoftoon.orm.PageRequest;
import com.isoftoon.utils.Constants;
import com.isoftoon.utils.DateConvertUtils;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.utils.FontAwesomeIconFactory;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Pagination;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ToolBar;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.GridPane;

public class ${className}Controller implements Initializable {
    static Logger logger = LoggerFactory.getLogger(${className}.class);
    static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @FXML AnchorPane rootAnchorPane;
    @FXML AnchorPane mainAnchorPane;
    @FXML AnchorPane detailAnchorPane;
    @FXML GridPane detailGridPane;

    @FXML TableView<${className}> ${classNameLower}Table;

    <#list table.columns as column>
    @FXML private TableColumn<${className}, ${column.javaType}> ${column.columnNameLower};
    </#list>

    <#list table.columns as column>
        <#if !column.pk && column.columnNameLower!="version"
                        && column.columnNameLower!="supplierCode"
                        && column.columnNameLower!="unitCode"
                        && column.columnNameLower!="tareUnitCode"
                        && column.columnNameLower!="warehouseCode"
                        && column.columnNameLower!="masterId"
                        && column.columnNameLower!="creatorId"
                        && column.columnNameLower!="updateTime"
                        && column.columnNameLower!="updator"
                        && column.columnNameLower!="updatorId"
                        >
            <#if column.isDateTimeColumn>
    @FXML DatePicker ${column.columnNameLower}Picker;
            <#else>
    @FXML TextField ${column.columnNameLower}Text;
            </#if>
        </#if>
    </#list>

    @FXML private Pagination pagination;

    private Button addBtn;
    private Button refreshBtn;
    private Button printBtn;
    private Button saveBtn;

    @FXML private ToolBar mainToolBar;
    @FXML private ToolBar detailToolBar;

    ProgressIndicator progressIndicator = new ProgressIndicator();

    private ${className} ${classNameLower};
    private IntegerProperty limit;
    private IntegerProperty totalCount;
    private ObservableList<${className}> ${classNameLower}s = FXCollections.observableArrayList();
    private ${className}Service ${classNameLower}Service = new ${className}Service();
    private OppersonService oppersonService;

    ChangeListener<String> forceIntegerListener = (observable, oldValue, newValue) -> {
        if (!newValue.matches("\\d*"))
            ((StringProperty) observable).set(oldValue);
    };
    ChangeListener<String> forceLongListener = (observable, oldValue, newValue) -> {
        if (!newValue.matches("|[-\\+]?|[-\\+]?\\d+\\.?|[-\\+]?\\d+\\.?\\d+"))
            ((StringProperty) observable).set(oldValue);
    };

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        createToolBar();
        oppersonService = new OppersonService();
        limit = new SimpleIntegerProperty(25);
        totalCount = new SimpleIntegerProperty(1);

        progressIndicator.setMaxSize(100, 100);

        <#list table.columns as column>
            <#if column.isDateTimeColumn>
        ${column.columnNameLower}.setCellValueFactory(cellData -> {
            SimpleObjectProperty property = new SimpleObjectProperty();
            if (cellData.getValue().get${column.columnName}() != null) {
                property.setValue(DateConvertUtils.format(cellData.getValue().get${column.columnName}(),Constants.DATETIME_FORMAT));
            }
            return property;
        });
            <#else>
        ${column.columnNameLower}.setCellValueFactory(new PropertyValueFactory<${className}, ${column.javaType}>("${column.columnNameLower}"));
            </#if>
        </#list>

        <#list table.columns as column>
            <#if !column.pk && column.columnNameLower!="version"
                            && column.columnNameLower!="supplierCode"
                            && column.columnNameLower!="unitCode"
                            && column.columnNameLower!="tareUnitCode"
                            && column.columnNameLower!="warehouseCode"
                            && column.columnNameLower!="masterId"
                            && column.columnNameLower!="creatorId"
                            && column.columnNameLower!="updateTime"
                            && column.columnNameLower!="updator"
                            && column.columnNameLower!="updatorId"
                            >
                <#if column.javaType=="Integer">
        ${column.columnNameLower}Text.textProperty().addListener(forceIntegerListener);
                <#elseif column.javaType=="Long">
        ${column.columnNameLower}Text.textProperty().addListener(forceLongListener);
                </#if>
            </#if>
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
        addBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent e) -> {
            saveBtn.setDisable(false);
            this.${classNameLower} = new ${className}();
            clearForm();
            Opperson person = null;
            if (CurrentUserSession.getInstance() !=  null) {
                person = CurrentUserSession.getInstance().getOpperson();
            } else {
                person = oppersonService.getLocalPerson(CurrentUserSession.getInstance().getUserName());
            }
            ${classNameLower}.setCreateTime(new Date());
            ${classNameLower}.setCreatorId(person.getSid());
            ${classNameLower}.setCreatorName(person.getSname());
            ${classNameLower}.setUpdatorId(person.getSid());
            ${classNameLower}.setUpdator(person.getSname());
            // materialLabelText.setFocusTraversable(true);
            Platform.runLater(new Runnable() {
                  @Override
                  public void run() {
                      // materialLabelText.requestFocus();
                  }
            });
            // 显示条码
            // shewBarCodeDetail("");
        });
        saveBtn.setDisable(true);
        saveBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent e) -> {
            if (!validate()) {
                return;
            }

            if (${classNameLower}.getId() == null) {
                ${classNameLower}.setId(UUID.randomUUID().toString());
                // ${classNameLower}.setTagDatetime(sdf.format(new Date()));
                ${classNameLower}.setVersion(0);
            }

            <#list table.columns as column>
                <#if !column.pk && column.columnNameLower!="version"
                                && column.columnNameLower!="supplierCode"
                                && column.columnNameLower!="unitCode"
                                && column.columnNameLower!="tareUnitCode"
                                && column.columnNameLower!="warehouseCode"
                                && column.columnNameLower!="masterId"
                                && column.columnNameLower!="creatorId"
                                && column.columnNameLower!="updateTime"
                                && column.columnNameLower!="updator"
                                && column.columnNameLower!="updatorId"
                                >
                <#if column.isDateTimeColumn>
            if (${column.columnNameLower}Picker.getValue() != null) {
                LocalDate localDate = ${column.columnNameLower}Picker.getValue();
                Date date = Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
                ${classNameLower}.set${column.columnName}(date);
            }
                <#elseif column.javaType=="Long">
            if (!${column.columnNameLower}Text.getText().trim().isEmpty()) {
                ${classNameLower}.set${column.columnName}(Long.parseLong(${column.columnNameLower}Text.getText()));
            }
                <#else>
            ${classNameLower}.set${column.columnName}(${column.columnNameLower}Text.getText());
                </#if>
                </#if>
            </#list>

            ${classNameLower}.setUpdateTime(new Date());

            // inStorage.setBarCode(Config2.getBarCode());
            // shewBarCodeDetail(inStorage.getBarCode());
            saveTask(${classNameLower});
        });
        ${classNameLower}Table.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            saveBtn.setDisable(true);
            show${className}Details(newValue);
        });

        StackPane.setAlignment(pagination, Pos.CENTER);
        progressIndicator.setVisible(true);
        init();
    }

    private void clearForm() {
        <#list table.columns as column>
            <#if !column.pk && column.columnNameLower!="version"
                            && column.columnNameLower!="supplierCode"
                            && column.columnNameLower!="unitCode"
                            && column.columnNameLower!="tareUnitCode"
                            && column.columnNameLower!="warehouseCode"
                            && column.columnNameLower!="masterId"
                            && column.columnNameLower!="creatorId"
                            && column.columnNameLower!="updateTime"
                            && column.columnNameLower!="updator"
                            && column.columnNameLower!="updatorId"
                            >
                <#if column.isDateTimeColumn>
        ${column.columnNameLower}Picker.setValue(LocalDate.now());
                <#else>
        ${column.columnNameLower}Text.clear();
                </#if>
            </#if>
        </#list>
    }

    private boolean validate() {
        /*
        <#list table.columns as column>
            <#if !column.pk && column.columnNameLower!="version"
                            && column.columnNameLower!="supplierCode"
                            && column.columnNameLower!="unitCode"
                            && column.columnNameLower!="tareUnitCode"
                            && column.columnNameLower!="warehouseCode"
                            && column.columnNameLower!="masterId"
                            && column.columnNameLower!="creatorId"
                            && column.columnNameLower!="updateTime"
                            && column.columnNameLower!="updator"
                            && column.columnNameLower!="updatorId"
                            >
        if (${column.columnNameLower}Text.getText() == null || "".equals(${column.columnNameLower}Text.getText())) {
            Dialog.showError("提示", "${column.columnAlias}不能为空");
            return false;
        }
            </#if>
        </#list>
        */
        return true;
    }

    public void init() {
        resetPage();
        changeTableView(0, limit.get());
    }

    public void resetPage() {
        pagination.setPageCount(1);
    }

    public ObservableList<${className}> get${className}s(List<${className}> list) {
        ${classNameLower}s.addAll(list);
        return this.${classNameLower}s;
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
                        Page<${className}> page = ${classNameLower}Service.findByPage(pageRequest);
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
            ${classNameLower}Table.setItems(get${className}s(page.getResult()));
            pagination.setCurrentPageIndex(pageIndex);
            totalCount.setValue(page.getTotalCount());
        });
        service.setOnFailed((WorkerStateEvent event) -> {
            event.getSource().getException().printStackTrace();
            logger.error("查询出错",event.getSource().getException().getStackTrace()[0].toString());
            Dialog.showThrowable("查询出错", event.getSource().getException().getStackTrace()[0].toString(), event.getSource().getException());
        });
    }

    private void show${className}Details(${className} ${classNameLower}) {
        this.${classNameLower} = ${classNameLower};
        if (${classNameLower} != null) {
            <#list table.columns as column>
            <#if !column.pk && column.columnNameLower!="version"
                            && column.columnNameLower!="supplierCode"
                            && column.columnNameLower!="unitCode"
                            && column.columnNameLower!="tareUnitCode"
                            && column.columnNameLower!="warehouseCode"
                            && column.columnNameLower!="masterId"
                            && column.columnNameLower!="creatorId"
                            && column.columnNameLower!="updateTime"
                            && column.columnNameLower!="updator"
                            && column.columnNameLower!="updatorId"
                            >
                <#if column.javaType=="Long">
            ${column.columnNameLower}Text.setText(String.valueOf(${classNameLower}.get${column.columnName}()));
                <#elseif  column.isDateTimeColumn>
            if (${classNameLower}.get${column.columnName}() == null) {
                ${column.columnNameLower}Picker.getEditor().clear();
                ${column.columnNameLower}Picker.setValue(null);
            } else {
                ${column.columnNameLower}Picker.setValue(${classNameLower}.get${column.columnName}().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
            }
                <#else>
            ${column.columnNameLower}Text.setText(${classNameLower}.get${column.columnName}());
                </#if>
            </#if>
        </#list>
        }
    }

    private void saveTask(${className} ${classNameLower}) {
        Service<Boolean> service = new Service<Boolean>() {
            @Override
            protected Task<Boolean> createTask() {
                return new Task<Boolean>() {
                    @Override
                    protected Boolean call() throws Exception {
                        ${classNameLower}Service.save(${classNameLower});
                        return true;
                    }
                };
            }
        };
        service.start();
        service.setOnRunning((WorkerStateEvent event) -> {});
        service.setOnSucceeded((WorkerStateEvent event) -> {
            ${classNameLower}s.add(0, ${classNameLower});
            Dialog.showInfo("提示", "保存成功");
        });
        service.setOnFailed((WorkerStateEvent event) -> {
            event.getSource().getException().getStackTrace();
            Dialog.showThrowable("查询出错", event.getSource().getException().getStackTrace()[0].toString(), event.getSource().getException());
        });
    }

    private void createToolBar() {
        addBtn = FontAwesomeIconFactory.get().createIconButton(FontAwesomeIcon.PLUS, "添加");
        addBtn.setPrefSize(150, 50);
        addBtn.setDefaultButton(true);
        refreshBtn = FontAwesomeIconFactory.get().createIconButton(FontAwesomeIcon.REFRESH, "刷新");
        refreshBtn.setPrefSize(150, 50);
        refreshBtn.setDefaultButton(true);

        mainToolBar.getItems().add(addBtn);
        mainToolBar.getItems().add(refreshBtn);

        saveBtn = FontAwesomeIconFactory.get().createIconButton(FontAwesomeIcon.FLOPPY_ALT, "保存");
        saveBtn.setPrefSize(150, 50);
        saveBtn.setDefaultButton(true);
        printBtn = FontAwesomeIconFactory.get().createIconButton(FontAwesomeIcon.PRINT, "打印");
        printBtn.setPrefSize(150, 50);
        printBtn.setDefaultButton(true);

        detailToolBar.getItems().add(saveBtn);
        detailToolBar.getItems().add(printBtn);
     }
}