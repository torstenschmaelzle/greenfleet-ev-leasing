package io.carlease.EV_Catalogue.adapter.in.vaadin;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import io.carlease.shared.domain.Currency;
import io.carlease.EV_Catalogue.domain.Vehicle;
import io.carlease.shared.domain.VehicleType;
import org.springframework.beans.factory.annotation.Autowired;

@Route("update-vehicle/:vin")
public class UpdateVehicleView extends VerticalLayout implements BeforeEnterObserver {

    @Autowired
    private EVCatalogueVaadinController controller;

    // Read-only
    private final TextField vinDisplay       = new TextField("VIN");

    // Vehicle Specification
    private final TextField brandField       = new TextField("Brand");
    private final TextField modelField       = new TextField("Model");
    private final IntegerField rangeKmField  = new IntegerField("Range");
    private final ComboBox<VehicleType> vehicleTypeComboBox = new ComboBox<>("Vehicle Type");
    private final NumberField consumptionField = new NumberField("Energy Consumption");

    // Battery Specification
    private final NumberField capacityField       = new NumberField("Battery Capacity");
    private final NumberField voltageField        = new NumberField("Voltage");
    private final NumberField maxChargeSpeedField = new NumberField("Max Charge Speed");

    // Performance (read-only — no domain update method)
    private final IntegerField horsePowerField   = new IntegerField("Horse Power");
    private final NumberField accelerationField  = new NumberField("0 – 100 km/h");
    private final IntegerField topSpeedField     = new IntegerField("Top Speed");

    // Pricing
    private final NumberField leasePriceField        = new NumberField("Monthly Lease Price");
    private final ComboBox<Currency> currencyComboBox = new ComboBox<>("Currency");

    // Battery Health (new measurement)
    private final NumberField stateOfHealthField  = new NumberField("State of Health");
    private final IntegerField measuredRangeField = new IntegerField("Measured Range");

    public UpdateVehicleView() {
        setSizeFull();
        setPadding(false);
        setSpacing(false);
        getStyle()
                .set("background", "#f0f4f8")
                .set("min-height", "100vh");

        VerticalLayout content = new VerticalLayout();
        content.setMaxWidth("880px");
        content.setWidthFull();
        content.setPadding(false);
        content.setSpacing(false);
        content.getStyle()
                .set("margin", "0 auto")
                .set("padding", "var(--lumo-space-l)")
                .set("gap", "var(--lumo-space-m)");

        configureFields();
        content.add(buildPageHeader());
        content.add(buildSection("Vehicle Identity", VaadinIcon.BARCODE, buildForm(1, vinDisplay)));
        content.add(buildSection("Vehicle Specification", VaadinIcon.CAR,
                buildForm(3, brandField, modelField, vehicleTypeComboBox, rangeKmField, consumptionField)));
        content.add(buildSection("Battery Specification", VaadinIcon.FLASH,
                buildForm(3, capacityField, voltageField, maxChargeSpeedField)));
        content.add(buildSection("Performance", VaadinIcon.DASHBOARD,
                buildForm(3, horsePowerField, accelerationField, topSpeedField)));
        content.add(buildSection("Pricing", VaadinIcon.MONEY,
                buildForm(2, leasePriceField, currencyComboBox)));
        content.add(buildSection("Battery Health", VaadinIcon.CHART_LINE,
                buildForm(2, stateOfHealthField, measuredRangeField)));
        content.add(buildSubmitRow());

        setAlignItems(FlexComponent.Alignment.CENTER);
        add(content);
    }

    private void configureFields() {
        vinDisplay.setReadOnly(true);
        vinDisplay.setPrefixComponent(VaadinIcon.BARCODE.create());
        vinDisplay.setWidthFull();

        brandField.setPlaceholder("e.g. Tesla");
        brandField.setWidthFull();

        modelField.setPlaceholder("e.g. Model 3");
        modelField.setWidthFull();

        rangeKmField.setSuffixComponent(new Span("km"));
        rangeKmField.setMin(1);
        rangeKmField.setWidthFull();

        vehicleTypeComboBox.setItems(VehicleType.values());
        vehicleTypeComboBox.setWidthFull();

        consumptionField.setSuffixComponent(new Span("kWh/100km"));
        consumptionField.setMin(0.1);
        consumptionField.setWidthFull();

        capacityField.setSuffixComponent(new Span("kWh"));
        capacityField.setMin(0.1);
        capacityField.setWidthFull();

        voltageField.setSuffixComponent(new Span("V"));
        voltageField.setMin(0.1);
        voltageField.setWidthFull();

        maxChargeSpeedField.setSuffixComponent(new Span("kW"));
        maxChargeSpeedField.setMin(0.1);
        maxChargeSpeedField.setWidthFull();

        horsePowerField.setSuffixComponent(new Span("HP"));
        horsePowerField.setReadOnly(true);
        horsePowerField.setWidthFull();

        accelerationField.setSuffixComponent(new Span("sec"));
        accelerationField.setReadOnly(true);
        accelerationField.setWidthFull();

        topSpeedField.setSuffixComponent(new Span("km/h"));
        topSpeedField.setReadOnly(true);
        topSpeedField.setWidthFull();

        leasePriceField.setSuffixComponent(new Span("cts"));
        leasePriceField.setHelperText("In cents — e.g. 49900 = 499.00");
        leasePriceField.setMin(1);
        leasePriceField.setWidthFull();

        currencyComboBox.setItems(Currency.values());
        currencyComboBox.setWidthFull();

        stateOfHealthField.setSuffixComponent(new Span("%"));
        stateOfHealthField.setHelperText("New measurement (0–100)");
        stateOfHealthField.setMin(0);
        stateOfHealthField.setMax(100);
        stateOfHealthField.setWidthFull();

        measuredRangeField.setSuffixComponent(new Span("km"));
        measuredRangeField.setHelperText("Measured range at current battery condition");
        measuredRangeField.setMin(1);
        measuredRangeField.setWidthFull();
    }

    // ── Header ──────────────────────────────────────────────────────────────────

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
                .set("width", "48px").set("height", "48px")
                .set("display", "flex")
                .set("align-items", "center")
                .set("justify-content", "center")
                .set("flex-shrink", "0");
        Icon icon = VaadinIcon.EDIT.create();
        icon.setSize("22px");
        icon.getStyle().set("color", "white");
        iconCircle.add(icon);

        Div textBlock = new Div();
        H1 title = new H1("Update Vehicle");
        title.getStyle()
                .set("color", "white")
                .set("margin", "0 0 2px 0")
                .set("font-size", "var(--lumo-font-size-xl)")
                .set("font-weight", "700")
                .set("line-height", "1.2");
        Paragraph subtitle = new Paragraph("Edit vehicle details in the GreenFleet EV Catalogue");
        subtitle.getStyle()
                .set("color", "rgba(255,255,255,0.70)")
                .set("margin", "0")
                .set("font-size", "var(--lumo-font-size-s)");
        textBlock.add(title, subtitle);
        left.add(iconCircle, textBlock);

        Button backButton = new Button("Back to Catalogue", VaadinIcon.ARROW_LEFT.create());
        backButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        backButton.getStyle()
                .set("background", "rgba(255,255,255,0.15)")
                .set("color", "white")
                .set("border", "1px solid rgba(255,255,255,0.3)")
                .set("font-weight", "600");
        backButton.addClickListener(e ->
                backButton.getUI().ifPresent(ui -> ui.navigate("catalogue")));

        header.add(left, backButton);
        return header;
    }

    // ── Submit row ───────────────────────────────────────────────────────────────

    private HorizontalLayout buildSubmitRow() {
        Button saveButton = new Button("Save Changes", VaadinIcon.CHECK.create());
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_LARGE);
        saveButton.getStyle()
                .set("flex", "1")
                .set("height", "3.5rem")
                .set("font-size", "var(--lumo-font-size-m)")
                .set("font-weight", "600");

        saveButton.addClickListener(click -> {
            try {
                controller.updateVehicle(
                        vinDisplay.getValue(),
                        brandField.getValue(),
                        modelField.getValue(),
                        rangeKmField.getValue(),
                        vehicleTypeComboBox.getValue().name(),
                        consumptionField.getValue(),
                        capacityField.getValue(),
                        voltageField.getValue(),
                        maxChargeSpeedField.getValue(),
                        leasePriceField.getValue().longValue(),
                        currencyComboBox.getValue().name(),
                        stateOfHealthField.getValue(),
                        measuredRangeField.getValue()
                );
                Notification success = Notification.show(
                        "Vehicle updated successfully", 3000, Notification.Position.BOTTOM_END);
                success.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                saveButton.getUI().ifPresent(ui -> ui.navigate("catalogue"));
            } catch (Exception e) {
                Notification error = Notification.show(
                        e.getMessage(), 5000, Notification.Position.BOTTOM_END);
                error.addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });

        HorizontalLayout row = new HorizontalLayout(saveButton);
        row.setWidthFull();
        row.getStyle().set("margin-top", "var(--lumo-space-s)");
        return row;
    }

    // ── Builder helpers ──────────────────────────────────────────────────────────

    private Div buildSection(String title, VaadinIcon iconType, Component form) {
        Div card = new Div();
        card.getStyle()
                .set("background", "var(--lumo-base-color)")
                .set("border-radius", "var(--lumo-border-radius-l)")
                .set("padding", "var(--lumo-space-l)")
                .set("box-shadow", "0 1px 4px rgba(0,0,0,0.08), 0 0 0 1px rgba(0,0,0,0.04)")
                .set("width", "100%")
                .set("box-sizing", "border-box");

        HorizontalLayout sectionHeader = new HorizontalLayout();
        sectionHeader.setAlignItems(FlexComponent.Alignment.CENTER);
        sectionHeader.setSpacing(true);
        sectionHeader.getStyle().set("margin-bottom", "var(--lumo-space-m)");

        Div iconBadge = new Div();
        iconBadge.getStyle()
                .set("background", "var(--lumo-primary-color-10pct)")
                .set("border-radius", "var(--lumo-border-radius-m)")
                .set("width", "36px").set("height", "36px")
                .set("display", "flex")
                .set("align-items", "center")
                .set("justify-content", "center")
                .set("flex-shrink", "0");
        Icon sectionIcon = iconType.create();
        sectionIcon.setSize("18px");
        sectionIcon.getStyle().set("color", "var(--lumo-primary-color)");
        iconBadge.add(sectionIcon);

        H3 sectionTitle = new H3(title);
        sectionTitle.getStyle()
                .set("margin", "0")
                .set("font-size", "var(--lumo-font-size-m)")
                .set("font-weight", "600")
                .set("color", "var(--lumo-header-text-color)");

        sectionHeader.add(iconBadge, sectionTitle);

        Hr divider = new Hr();
        divider.getStyle()
                .set("margin", "0 0 var(--lumo-space-m) 0")
                .set("border", "none")
                .set("border-top", "1px solid var(--lumo-contrast-10pct)");

        card.add(sectionHeader, divider, form);
        return card;
    }

    private FormLayout buildForm(int maxColumns, Component... fields) {
        FormLayout form = new FormLayout(fields);
        form.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("500px", Math.min(maxColumns, 2)),
                new FormLayout.ResponsiveStep("700px", maxColumns)
        );
        return form;
    }

    // ── Data loading ─────────────────────────────────────────────────────────────

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        String vin = event.getRouteParameters().get("vin")
                .orElseThrow(() -> new IllegalArgumentException("Missing VIN parameter"));
        try {
            Vehicle vehicle = controller.getVehicle(vin);
            prefillFields(vehicle);
        } catch (Exception e) {
            Notification error = Notification.show(
                    "Vehicle not found: " + vin, 4000, Notification.Position.MIDDLE);
            error.addThemeVariants(NotificationVariant.LUMO_ERROR);
            event.forwardTo("catalogue");
        }
    }

    private void prefillFields(Vehicle v) {
        vinDisplay.setValue(v.getVehicleId().getVin());
        brandField.setValue(v.getVehicleSpecification().getBrand());
        modelField.setValue(v.getVehicleSpecification().getModel());
        rangeKmField.setValue(v.getVehicleSpecification().getRangeKm().getValue());
        vehicleTypeComboBox.setValue(v.getVehicleSpecification().getVehicleType());
        consumptionField.setValue(v.getVehicleSpecification().getConsumptionKwhPer100km().getValue().doubleValue());
        capacityField.setValue(v.getBatterySpecification().getCapacityKwh().doubleValue());
        voltageField.setValue(v.getBatterySpecification().getVoltageV().doubleValue());
        maxChargeSpeedField.setValue(v.getBatterySpecification().getMaxChargeSpeedKw().doubleValue());
        horsePowerField.setValue(v.getPerformance().getHorsePowerHp());
        accelerationField.setValue(v.getPerformance().getAccelerationSec().doubleValue());
        topSpeedField.setValue(v.getPerformance().getTopSpeedKmh());
        leasePriceField.setValue((double) v.getMonthlyLeasePrice().getAmountInCents());
        currencyComboBox.setValue(v.getMonthlyLeasePrice().getCurrency());

        stateOfHealthField.setValue(v.getLatestStateOfHealth().getValue().doubleValue());
        measuredRangeField.setValue(v.getLatestMeasuredRangeKm().getValue());
    }
}
