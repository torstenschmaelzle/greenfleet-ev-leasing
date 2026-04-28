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
import com.vaadin.flow.router.Route;
import io.carlease.shared.domain.Currency;
import io.carlease.shared.domain.VehicleType;
import org.springframework.beans.factory.annotation.Autowired;

@Route("add-vehicle")
public class AddVehicleView extends VerticalLayout {

    @Autowired
    private EVCatalogueVaadinController controller;

    public AddVehicleView() {
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

        // ── Header ──────────────────────────────────────────────────────────────
        content.add(buildPageHeader());

        // ── Fields ──────────────────────────────────────────────────────────────
        TextField vinField = new TextField("VIN");
        vinField.setPlaceholder("e.g. WBA3A5G59DNP26082");
        vinField.setHelperText("Exactly 17 alphanumeric characters");
        vinField.setPrefixComponent(VaadinIcon.BARCODE.create());
        vinField.setWidthFull();

        TextField brandField = new TextField("Brand");
        brandField.setPlaceholder("e.g. Tesla");
        brandField.setWidthFull();

        TextField modelField = new TextField("Model");
        modelField.setPlaceholder("e.g. Model 3");
        modelField.setWidthFull();

        IntegerField rangeKmField = new IntegerField("Range");
        rangeKmField.setSuffixComponent(new Span("km"));
        rangeKmField.setMin(1);
        rangeKmField.setWidthFull();

        ComboBox<VehicleType> vehicleTypeComboBox = new ComboBox<>("Vehicle Type");
        vehicleTypeComboBox.setItems(VehicleType.values());
        vehicleTypeComboBox.setWidthFull();

        NumberField consumptionField = new NumberField("Energy Consumption");
        consumptionField.setSuffixComponent(new Span("kWh/100km"));
        consumptionField.setMin(0.1);
        consumptionField.setWidthFull();

        NumberField capacityField = new NumberField("Battery Capacity");
        capacityField.setSuffixComponent(new Span("kWh"));
        capacityField.setMin(0.1);
        capacityField.setWidthFull();

        NumberField voltageField = new NumberField("Voltage");
        voltageField.setSuffixComponent(new Span("V"));
        voltageField.setMin(0.1);
        voltageField.setWidthFull();

        NumberField maxChargeSpeedField = new NumberField("Max Charge Speed");
        maxChargeSpeedField.setSuffixComponent(new Span("kW"));
        maxChargeSpeedField.setMin(0.1);
        maxChargeSpeedField.setWidthFull();

        IntegerField horsePowerField = new IntegerField("Horse Power");
        horsePowerField.setSuffixComponent(new Span("HP"));
        horsePowerField.setMin(1);
        horsePowerField.setWidthFull();

        NumberField accelerationField = new NumberField("0 – 100 km/h");
        accelerationField.setSuffixComponent(new Span("sec"));
        accelerationField.setMin(0.1);
        accelerationField.setWidthFull();

        IntegerField topSpeedField = new IntegerField("Top Speed");
        topSpeedField.setSuffixComponent(new Span("km/h"));
        topSpeedField.setMin(1);
        topSpeedField.setWidthFull();

        NumberField leasePriceField = new NumberField("Monthly Lease Price");
        leasePriceField.setSuffixComponent(new Span("cts"));
        leasePriceField.setHelperText("In cents — e.g. 49900 = 499.00");
        leasePriceField.setMin(1);
        leasePriceField.setWidthFull();

        ComboBox<Currency> currencyComboBox = new ComboBox<>("Currency");
        currencyComboBox.setItems(Currency.values());
        currencyComboBox.setWidthFull();

        NumberField stateOfHealthField = new NumberField("State of Health");
        stateOfHealthField.setSuffixComponent(new Span("%"));
        stateOfHealthField.setHelperText("Current battery condition (0–100)");
        stateOfHealthField.setMin(0);
        stateOfHealthField.setMax(100);
        stateOfHealthField.setWidthFull();

        IntegerField initialRangeField = new IntegerField("Measured Range");
        initialRangeField.setSuffixComponent(new Span("km"));
        initialRangeField.setMin(1);
        initialRangeField.setWidthFull();

        // ── Form sections ────────────────────────────────────────────────────────
        content.add(buildSection("Vehicle Identity", VaadinIcon.BARCODE,
                buildForm(1, vinField)));

        content.add(buildSection("Vehicle Specification", VaadinIcon.CAR,
                buildForm(3, brandField, modelField, vehicleTypeComboBox, rangeKmField, consumptionField)));

        content.add(buildSection("Battery Specification", VaadinIcon.FLASH,
                buildForm(3, capacityField, voltageField, maxChargeSpeedField)));

        content.add(buildSection("Performance", VaadinIcon.DASHBOARD,
                buildForm(3, horsePowerField, accelerationField, topSpeedField)));

        content.add(buildSection("Pricing", VaadinIcon.MONEY,
                buildForm(2, leasePriceField, currencyComboBox)));

        content.add(buildSection("Battery Health", VaadinIcon.CHART_LINE,
                buildForm(2, stateOfHealthField, initialRangeField)));

        // ── Submit ───────────────────────────────────────────────────────────────
        Button submitButton = new Button("Add Vehicle to Catalogue", VaadinIcon.PLUS_CIRCLE_O.create());
        submitButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_LARGE);
        submitButton.setWidthFull();
        submitButton.getStyle()
                .set("margin-top", "var(--lumo-space-s)")
                .set("height", "3.5rem")
                .set("font-size", "var(--lumo-font-size-m)")
                .set("font-weight", "600");

        submitButton.addClickListener(click -> {
            try {
                controller.addVehicle(
                        vinField.getValue(),
                        brandField.getValue(),
                        modelField.getValue(),
                        rangeKmField.getValue(),
                        vehicleTypeComboBox.getValue().name(),
                        consumptionField.getValue(),
                        capacityField.getValue(),
                        voltageField.getValue(),
                        maxChargeSpeedField.getValue(),
                        horsePowerField.getValue(),
                        accelerationField.getValue(),
                        topSpeedField.getValue(),
                        leasePriceField.getValue().longValue(),
                        currencyComboBox.getValue().name(),
                        stateOfHealthField.getValue(),
                        initialRangeField.getValue()
                );

                Notification success = Notification.show(
                        "Vehicle successfully added to the EV Catalogue",
                        3000, Notification.Position.BOTTOM_END);
                success.addThemeVariants(NotificationVariant.LUMO_SUCCESS);

                clearForm(vinField, brandField, modelField, rangeKmField, vehicleTypeComboBox,
                        consumptionField, capacityField, voltageField, maxChargeSpeedField,
                        horsePowerField, accelerationField, topSpeedField,
                        leasePriceField, currencyComboBox, stateOfHealthField, initialRangeField);

            } catch (Exception e) {
                Notification error = Notification.show(
                        e.getMessage(), 5000, Notification.Position.BOTTOM_END);
                error.addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });

        content.add(submitButton);

        setAlignItems(FlexComponent.Alignment.CENTER);
        add(content);
    }

    // ── Builder helpers ──────────────────────────────────────────────────────────

    private Component buildPageHeader() {
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

        Div iconCircle = new Div();
        iconCircle.getStyle()
                .set("background", "rgba(255,255,255,0.15)")
                .set("border-radius", "50%")
                .set("width", "56px")
                .set("height", "56px")
                .set("display", "flex")
                .set("align-items", "center")
                .set("justify-content", "center")
                .set("flex-shrink", "0");
        Icon bolt = VaadinIcon.FLASH.create();
        bolt.setSize("28px");
        bolt.getStyle().set("color", "#fff");
        iconCircle.add(bolt);

        Div textBlock = new Div();
        H1 title = new H1("Add New Vehicle");
        title.getStyle()
                .set("color", "white")
                .set("margin", "0 0 var(--lumo-space-xs) 0")
                .set("font-size", "var(--lumo-font-size-xxl)")
                .set("font-weight", "700")
                .set("line-height", "1.2");
        Paragraph subtitle = new Paragraph("Register an electric vehicle in the GreenFleet EV Catalogue");
        subtitle.getStyle()
                .set("color", "rgba(255,255,255,0.75)")
                .set("margin", "0")
                .set("font-size", "var(--lumo-font-size-s)");
        textBlock.add(title, subtitle);

        Div left = new Div();
        left.getStyle()
                .set("display", "flex")
                .set("align-items", "center")
                .set("gap", "var(--lumo-space-l)");
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
                .set("width", "36px")
                .set("height", "36px")
                .set("display", "flex")
                .set("align-items", "center")
                .set("justify-content", "center")
                .set("flex-shrink", "0");
        Icon icon = iconType.create();
        icon.setSize("18px");
        icon.getStyle().set("color", "var(--lumo-primary-color)");
        iconBadge.add(icon);

        Div titleBlock = new Div();
        H3 sectionTitle = new H3(title);
        sectionTitle.getStyle()
                .set("margin", "0")
                .set("font-size", "var(--lumo-font-size-m)")
                .set("font-weight", "600")
                .set("color", "var(--lumo-header-text-color)");
        titleBlock.add(sectionTitle);

        sectionHeader.add(iconBadge, titleBlock);

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

    private void clearForm(TextField vin, TextField brand, TextField model,
                           IntegerField range, ComboBox<VehicleType> type,
                           NumberField consumption, NumberField capacity,
                           NumberField voltage, NumberField chargeSpeed,
                           IntegerField hp, NumberField accel, IntegerField topSpeed,
                           NumberField price, ComboBox<Currency> currency,
                           NumberField soh, IntegerField initRange) {
        vin.clear(); brand.clear(); model.clear(); range.clear(); type.clear();
        consumption.clear(); capacity.clear(); voltage.clear(); chargeSpeed.clear();
        hp.clear(); accel.clear(); topSpeed.clear(); price.clear(); currency.clear();
        soh.clear(); initRange.clear();
    }
}
