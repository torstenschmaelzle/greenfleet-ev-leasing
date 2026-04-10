package io.carlease.EV_Catalogue.adapter.in.vaadin;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import io.carlease.EV_Catalogue.domain.Vehicle;
import io.carlease.EV_Catalogue.domain.VehicleStatus;
import io.carlease.EV_Catalogue.domain.VehicleType;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Route("catalogue")
public class ListCatalogueView extends VerticalLayout implements BeforeEnterObserver {

    @Autowired
    private EVCatalogueVaadinController controller;

    private final Grid<Vehicle> grid = new Grid<>(Vehicle.class, false);
    private final TextField searchField = new TextField();
    private List<Vehicle> allVehicles = List.of();

    public ListCatalogueView() {
        setSizeFull();
        setPadding(false);
        setSpacing(false);
        getStyle()
                .set("background", "#f0f4f8")
                .set("min-height", "100vh");

        VerticalLayout content = new VerticalLayout();
        content.setSizeFull();
        content.setMaxWidth("1200px");
        content.setPadding(false);
        content.setSpacing(false);
        content.getStyle()
                .set("margin", "0 auto")
                .set("padding", "var(--lumo-space-l)")
                .set("gap", "var(--lumo-space-m)");

        content.add(buildPageHeader());
        content.add(buildToolbar());
        content.add(buildGridCard());

        setAlignItems(FlexComponent.Alignment.CENTER);
        add(content);
    }

    // ── Page header ──────────────────────────────────────────────────────────────

    private Div buildPageHeader() {
        Div header = new Div();
        header.getStyle()
                .set("background", "linear-gradient(135deg, #1a237e 0%, #283593 60%, #3949ab 100%)")
                .set("border-radius", "var(--lumo-border-radius-l)")
                .set("padding", "var(--lumo-space-l) var(--lumo-space-xl)")
                .set("width", "100%")
                .set("box-sizing", "border-box")
                .set("display", "flex")
                .set("align-items", "center")
                .set("justify-content", "space-between");

        Div left = new Div();
        left.getStyle()
                .set("display", "flex")
                .set("align-items", "center")
                .set("gap", "var(--lumo-space-m)");

        Div iconCircle = new Div();
        iconCircle.getStyle()
                .set("background", "rgba(255,255,255,0.15)")
                .set("border-radius", "50%")
                .set("width", "48px")
                .set("height", "48px")
                .set("display", "flex")
                .set("align-items", "center")
                .set("justify-content", "center")
                .set("flex-shrink", "0");
        var bolt = VaadinIcon.FLASH.create();
        bolt.setSize("24px");
        bolt.getStyle().set("color", "white");
        iconCircle.add(bolt);

        Div textBlock = new Div();
        H1 title = new H1("EV Catalogue");
        title.getStyle()
                .set("color", "white")
                .set("margin", "0 0 2px 0")
                .set("font-size", "var(--lumo-font-size-xl)")
                .set("font-weight", "700")
                .set("line-height", "1.2");
        Paragraph subtitle = new Paragraph("GreenFleet electric vehicle inventory");
        subtitle.getStyle()
                .set("color", "rgba(255,255,255,0.70)")
                .set("margin", "0")
                .set("font-size", "var(--lumo-font-size-s)");
        textBlock.add(title, subtitle);
        left.add(iconCircle, textBlock);

        Button addButton = new Button("Add Vehicle", VaadinIcon.PLUS.create());
        addButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        addButton.getStyle()
                .set("background", "rgba(255,255,255,0.15)")
                .set("color", "white")
                .set("border", "1px solid rgba(255,255,255,0.3)")
                .set("font-weight", "600");
        addButton.addClickListener(e ->
                addButton.getUI().ifPresent(ui -> ui.navigate("add-vehicle")));

        header.add(left, addButton);
        return header;
    }

    // ── Toolbar ──────────────────────────────────────────────────────────────────

    private Div buildToolbar() {
        Div toolbar = new Div();
        toolbar.getStyle()
                .set("background", "var(--lumo-base-color)")
                .set("border-radius", "var(--lumo-border-radius-l)")
                .set("padding", "var(--lumo-space-m) var(--lumo-space-l)")
                .set("box-shadow", "0 1px 4px rgba(0,0,0,0.08), 0 0 0 1px rgba(0,0,0,0.04)")
                .set("width", "100%")
                .set("box-sizing", "border-box")
                .set("display", "flex")
                .set("align-items", "center")
                .set("gap", "var(--lumo-space-m)");

        searchField.setPlaceholder("Search by brand, model or VIN…");
        searchField.setPrefixComponent(VaadinIcon.SEARCH.create());
        searchField.setClearButtonVisible(true);
        searchField.setValueChangeMode(ValueChangeMode.EAGER);
        searchField.getStyle().set("flex", "1");
        searchField.addValueChangeListener(e -> applyFilter(e.getValue()));

        Button refreshButton = new Button(VaadinIcon.REFRESH.create());
        refreshButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        refreshButton.getElement().setAttribute("title", "Refresh");
        refreshButton.addClickListener(e -> loadData());

        toolbar.add(searchField, refreshButton);
        return toolbar;
    }

    // ── Grid card ─────────────────────────────────────────────────────────────────

    private Div buildGridCard() {
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_NO_BORDER);
        grid.setHeightFull();

        grid.addColumn(v -> v.getVehicleId().getVin())
                .setHeader("VIN")
                .setWidth("190px")
                .setFlexGrow(0)
                .setSortable(true);

        grid.addColumn(v -> v.getVehicleSpecification().getBrand())
                .setHeader("Brand")
                .setAutoWidth(true)
                .setSortable(true);

        grid.addColumn(v -> v.getVehicleSpecification().getModel())
                .setHeader("Model")
                .setAutoWidth(true)
                .setSortable(true);

        grid.addComponentColumn(v -> buildTypeBadge(v.getVehicleSpecification().getVehicleType()))
                .setHeader("Type")
                .setAutoWidth(true);

        grid.addColumn(v -> v.getVehicleSpecification().getRangeKm().getValue() + " km")
                .setHeader("Range")
                .setAutoWidth(true)
                .setSortable(true);

        grid.addColumn(v -> v.getBatterySpecification().getCapacityKwh() + " kWh")
                .setHeader("Battery")
                .setAutoWidth(true);

        grid.addColumn(v -> v.getPerformance().getHorsePowerHp() + " HP")
                .setHeader("Power")
                .setAutoWidth(true)
                .setSortable(true);

        grid.addColumn(this::formatPrice)
                .setHeader("Monthly Price")
                .setAutoWidth(true)
                .setSortable(true);

        grid.addComponentColumn(v -> buildStatusBadge(v.getStatus()))
                .setHeader("Status")
                .setAutoWidth(true);

        grid.addComponentColumn(this::buildActionButtons)
                .setHeader("")
                .setWidth("110px")
                .setFlexGrow(0);

        Div card = new Div();
        card.getStyle()
                .set("background", "var(--lumo-base-color)")
                .set("border-radius", "var(--lumo-border-radius-l)")
                .set("box-shadow", "0 1px 4px rgba(0,0,0,0.08), 0 0 0 1px rgba(0,0,0,0.04)")
                .set("width", "100%")
                .set("overflow", "hidden")
                .set("flex", "1");
        card.add(grid);
        return card;
    }

    // ── Badges ───────────────────────────────────────────────────────────────────

    private Span buildStatusBadge(VehicleStatus status) {
        Span badge = new Span(status.name().replace("_", " "));
        badge.getStyle()
                .set("padding", "3px 10px")
                .set("border-radius", "12px")
                .set("font-size", "var(--lumo-font-size-xs)")
                .set("font-weight", "600")
                .set("white-space", "nowrap");
        switch (status) {
            case AVAILABLE -> badge.getStyle()
                    .set("background", "#e8f5e9").set("color", "#2e7d32");
            case RESERVED -> badge.getStyle()
                    .set("background", "#fff3e0").set("color", "#e65100");
            case UNDER_CONTRACT -> badge.getStyle()
                    .set("background", "#e3f2fd").set("color", "#1565c0");
            case REMOVED -> badge.getStyle()
                    .set("background", "#fafafa").set("color", "#757575");
        }
        return badge;
    }

    private HorizontalLayout buildActionButtons(Vehicle vehicle) {
        Button editButton = new Button(VaadinIcon.EDIT.create());
        editButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL);
        editButton.getElement().setAttribute("title", "Edit vehicle");
        editButton.addClickListener(e ->
                editButton.getUI().ifPresent(ui ->
                        ui.navigate("update-vehicle/" + vehicle.getVehicleId().getVin())));

        Button deleteButton = buildDeleteButton(vehicle);

        HorizontalLayout actions = new HorizontalLayout(editButton, deleteButton);
        actions.setSpacing(false);
        actions.setPadding(false);
        return actions;
    }

    private Button buildDeleteButton(Vehicle vehicle) {
        Button deleteButton = new Button(VaadinIcon.TRASH.create());
        deleteButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_SMALL);
        deleteButton.getElement().setAttribute("title", "Remove vehicle");

        deleteButton.addClickListener(e -> {
            String vin = vehicle.getVehicleId().getVin();
            String label = vehicle.getVehicleSpecification().getBrand()
                    + " " + vehicle.getVehicleSpecification().getModel()
                    + " (" + vin + ")";

            ConfirmDialog dialog = new ConfirmDialog();
            dialog.setHeader("Remove Vehicle");
            dialog.setText("Are you sure you want to remove " + label + " from the catalogue? This action cannot be undone.");
            dialog.setCancelable(true);
            dialog.setCancelText("Cancel");
            dialog.setConfirmText("Remove");
            dialog.setConfirmButtonTheme("error primary");
            dialog.addConfirmListener(confirm -> {
                try {
                    controller.removeVehicle(vin);
                    loadData();
                } catch (Exception ex) {
                    var notification = com.vaadin.flow.component.notification.Notification
                            .show(ex.getMessage(), 5000, com.vaadin.flow.component.notification.Notification.Position.BOTTOM_END);
                    notification.addThemeVariants(com.vaadin.flow.component.notification.NotificationVariant.LUMO_ERROR);
                }
            });
            dialog.open();
        });

        return deleteButton;
    }

    private Span buildTypeBadge(VehicleType type) {
        Span badge = new Span(type.name());
        badge.getStyle()
                .set("padding", "3px 10px")
                .set("border-radius", "12px")
                .set("font-size", "var(--lumo-font-size-xs)")
                .set("font-weight", "500")
                .set("background", "var(--lumo-contrast-10pct)")
                .set("color", "var(--lumo-secondary-text-color)");
        return badge;
    }

    // ── Data loading & filtering ──────────────────────────────────────────────────

    private void loadData() {
        allVehicles = controller.listAll();
        applyFilter(searchField.getValue());
    }

    private void applyFilter(String query) {
        if (query == null || query.isBlank()) {
            grid.setItems(allVehicles);
            return;
        }
        String lower = query.toLowerCase();
        List<Vehicle> filtered = allVehicles.stream()
                .filter(v ->
                        v.getVehicleId().getVin().toLowerCase().contains(lower) ||
                        v.getVehicleSpecification().getBrand().toLowerCase().contains(lower) ||
                        v.getVehicleSpecification().getModel().toLowerCase().contains(lower))
                .toList();
        grid.setItems(filtered);
    }

    private String formatPrice(Vehicle v) {
        long cents = v.getMonthlyLeasePrice().getAmountInCents();
        String currency = v.getMonthlyLeasePrice().getCurrency().name();
        return String.format("%s %.2f", currency, cents / 100.0);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        loadData();
    }
}
