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
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.isoftoon.fx.dialog.Dialog;
import com.isoftoon.ld.fx.animations.FadeInUpTransition;
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
import javafx.beans.binding.Bindings;
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
import javafx.util.Callback;
import javafx.scene.layout.GridPane;

public class ${className}Controller implements Initializable,Observer {
    static Logger logger = LoggerFactory.getLogger(${className}.class);

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
    private Button mainPrintBtn;
    private Button printBtn;
    private Button detailBtn;
    private Button closeBtn;
    private Button saveBtn;
    private Button backBtn;

    @FXML private ToolBar mainToolBar;
    @FXML private ToolBar detailToolBar;

    ProgressIndicator progressIndicator = new ProgressIndicator();

    private ${className} ${classNameLower};
    private IntegerProperty limit;
    private IntegerProperty totalCount;
    private ObservableList<${className}> ${classNameLower}s = FXCollections.observableArrayList();
    private ${className}Service ${classNameLower}Service = new ${className}Service();
    private OppersonService oppersonService;
    public Callback<Void, Void> callback;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        createToolBar();
        oppersonService = new OppersonService();
        limit = new SimpleIntegerProperty(25);
        totalCount = new SimpleIntegerProperty(1);

        progressIndicator.setMaxSize(100, 100);

        setupTable();

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
            mainAnchorPane.setOpacity(0);
            mainAnchorPane.toBack();
            new FadeInUpTransition(mainAnchorPane).play();
            mainAnchorPane.setVisible(false);
            detailAnchorPane.setOpacity(1);
            new FadeInUpTransition(detailAnchorPane).play();
            detailAnchorPane.setVisible(true);
            this.${classNameLower} = new ${className}();
            clearForm();
        });
        detailBtn.disableProperty().bind(
            Bindings.isNull(
                ${classNameLower}Table.getSelectionModel().selectedItemProperty()
            )
        );
        detailBtn.setOnAction(e -> {
            mainAnchorPane.setOpacity(0);
            new FadeInUpTransition(mainAnchorPane).play();
            mainAnchorPane.setVisible(false);
            detailAnchorPane.setOpacity(1);
            new FadeInUpTransition(detailAnchorPane).play();
            detailAnchorPane.setVisible(true);
        });
        closeBtn.setOnAction(e -> {
            callback.call(null);
        });
        printBtn.disableProperty().bind(
            Bindings.isNull(
                ${classNameLower}Table.getSelectionModel().selectedItemProperty()
            )
        );
        mainPrintBtn.disableProperty().bind(
            Bindings.isNull(
                ${classNameLower}Table.getSelectionModel().selectedItemProperty()
            )
        );
        ${classNameLower}Table.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            saveBtn.setDisable(true);
            tableSelectionChanged(newValue);
        });

        StackPane.setAlignment(pagination, Pos.CENTER);
        progressIndicator.setVisible(true);
        init();
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

    private void clearForm() {
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

    private void tableSelectionChanged(${className} ${classNameLower}) {
        this.${classNameLower} = ${classNameLower};
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

        detailBtn = FontAwesomeIconFactory.get().createIconButton(FontAwesomeIcon.FLOPPY_ALT, "详细");
        detailBtn.setPrefSize(150, 50);
        detailBtn.setDefaultButton(true);

        mainPrintBtn = FontAwesomeIconFactory.get().createIconButton(FontAwesomeIcon.PRINT, "打印");
        mainPrintBtn.setPrefSize(150, 50);
        mainPrintBtn.setDefaultButton(true);

        closeBtn = FontAwesomeIconFactory.get().createIconButton(FontAwesomeIcon.CLOSE, "关闭");
        closeBtn.setPrefSize(150, 50);
        closeBtn.setDefaultButton(true);

        mainToolBar.getItems().add(addBtn);
        // mainToolBar.getItems().add(refreshBtn);
        mainToolBar.getItems().add(detailBtn);
        mainToolBar.getItems().add(mainPrintBtn);
        mainToolBar.getItems().add(closeBtn);

        saveBtn = FontAwesomeIconFactory.get().createIconButton(FontAwesomeIcon.FLOPPY_ALT, "保存");
        saveBtn.setPrefSize(150, 50);
        saveBtn.setDefaultButton(true);

        backBtn = FontAwesomeIconFactory.get().createIconButton(FontAwesomeIcon.ARROW_LEFT, "返回");
        backBtn.setPrefSize(150, 50);
        backBtn.setDefaultButton(true);

        printBtn = FontAwesomeIconFactory.get().createIconButton(FontAwesomeIcon.PRINT, "打印");
        printBtn.setPrefSize(150, 50);
        printBtn.setDefaultButton(true);

        detailToolBar.getItems().add(backBtn);
        detailToolBar.getItems().add(saveBtn);
        detailToolBar.getItems().add(printBtn);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void setupTable() {
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
    }

    @Override
    public void update(Observable o, Object arg) {
        // TODO Auto-generated method stub

    }
}