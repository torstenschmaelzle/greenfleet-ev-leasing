package io.carlease.fleet_consulting.adapter.in.vaadin;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.router.Route;
import io.carlease.fleet_consulting.domain.*;
import io.carlease.shared.domain.Amount;
import io.carlease.shared.domain.Currency;
import io.carlease.shared.domain.VehicleType;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Route("fleet-consulting")
public class FleetConsultingView extends VerticalLayout {

    @Autowired
    private FleetConsultingVaadinController controller;

    // Form fields
    private final IntegerField dailyRouteKmField = new IntegerField();
    private final IntegerField numberOfDriversField = new IntegerField();
    private final Select<String> chargingTypeSelect = new Select<>();
    private final NumberField electricityRateField = new NumberField();
    private final NumberField budgetMonthlyField = new NumberField();
    private final Select<String> sizePreferenceSelect = new Select<>();

    // Scoring weight state (range, cost, co2)
    private final int[] weights = {40, 40, 20};
    private final Span[] weightLabels = new Span[3];
    private final Element[] sliderElements = new Element[3];
    private Div rangeBar;
    private Div costBar;
    private Div co2Bar;

    // Results
    private List<VehicleRecommendation> currentResults = List.of();
    private final Grid<VehicleRecommendation> resultsGrid = new Grid<>(VehicleRecommendation.class, false);
    private Paragraph emptyLabel;

    public FleetConsultingView() {
        setSizeFull();
        setPadding(false);
        setSpacing(false);
        getStyle()
                .set("background", "#f0f4f8")
                .set("min-height", "100vh");

        VerticalLayout content = new VerticalLayout();
        content.setSizeFull();
        content.setMaxWidth("1400px");
        content.setPadding(false);
        content.setSpacing(false);
        content.getStyle()
                .set("margin", "0 auto")
                .set("padding", "var(--lumo-space-l)")
                .set("gap", "var(--lumo-space-m)");

        content.add(buildPageHeader());

        HorizontalLayout body = new HorizontalLayout();
        body.setSizeFull();
        body.setAlignItems(FlexComponent.Alignment.START);
        body.getStyle().set("gap", "var(--lumo-space-m)");

        Div formCard = buildFormCard();
        formCard.getStyle().set("flex-shrink", "0").set("width", "380px");
        body.add(formCard);
        body.addAndExpand(buildResultsCard());

        content.add(body);
        setAlignItems(FlexComponent.Alignment.CENTER);
        add(content);
    }

    // ── Page header ──────────────────────────────────────────────────────────────

    private Div buildPageHeader() {
        Div header = new Div();
        header.getStyle()
                .set("background", "linear-gradient(135deg, #1b5e20 0%, #2e7d32 60%, #388e3c 100%)")
                .set("border-radius", "var(--lumo-border-radius-l)")
                .set("padding", "var(--lumo-space-l) var(--lumo-space-xl)")
                .set("width", "100%")
                .set("box-sizing", "border-box")
                .set("display", "flex")
                .set("align-items", "center");

        Div left = new Div();
        left.getStyle().set("display", "flex").set("align-items", "center").set("gap", "var(--lumo-space-m)");

        Div iconCircle = new Div();
        iconCircle.getStyle()
                .set("background", "rgba(255,255,255,0.15)")
                .set("border-radius", "50%")
                .set("width", "48px").set("height", "48px")
                .set("display", "flex").set("align-items", "center").set("justify-content", "center")
                .set("flex-shrink", "0");
        var icon = VaadinIcon.CAR.create();
        icon.setSize("24px");
        icon.getStyle().set("color", "white");
        iconCircle.add(icon);

        Div textBlock = new Div();
        H1 title = new H1("Fleet Consulting");
        title.getStyle()
                .set("color", "white").set("margin", "0 0 2px 0")
                .set("font-size", "var(--lumo-font-size-xl)").set("font-weight", "700").set("line-height", "1.2");
        Paragraph subtitle = new Paragraph("Enter your requirements and find the best EV for your fleet");
        subtitle.getStyle()
                .set("color", "rgba(255,255,255,0.70)").set("margin", "0")
                .set("font-size", "var(--lumo-font-size-s)");
        textBlock.add(title, subtitle);
        left.add(iconCircle, textBlock);

        header.add(left);
        return header;
    }

    // ── Form card ─────────────────────────────────────────────────────────────────

    private Div buildFormCard() {
        Div card = new Div();
        card.getStyle()
                .set("background", "var(--lumo-base-color)")
                .set("border-radius", "var(--lumo-border-radius-l)")
                .set("box-shadow", "0 1px 4px rgba(0,0,0,0.08), 0 0 0 1px rgba(0,0,0,0.04)")
                .set("padding", "var(--lumo-space-l)")
                .set("box-sizing", "border-box");

        VerticalLayout form = new VerticalLayout();
        form.setPadding(false);
        form.setSpacing(false);
        form.getStyle().set("gap", "var(--lumo-space-s)");

        H3 formTitle = new H3("Fleet Requirements");
        formTitle.getStyle().set("margin", "0 0 var(--lumo-space-s) 0").set("font-size", "var(--lumo-font-size-l)");

        dailyRouteKmField.setLabel("Daily route (km)");
        dailyRouteKmField.setMin(1);
        dailyRouteKmField.setValue(100);
        dailyRouteKmField.setStepButtonsVisible(true);
        dailyRouteKmField.setWidthFull();

        numberOfDriversField.setLabel("Number of drivers");
        numberOfDriversField.setMin(1);
        numberOfDriversField.setValue(3);
        numberOfDriversField.setStepButtonsVisible(true);
        numberOfDriversField.setWidthFull();

        chargingTypeSelect.setLabel("Charging type");
        chargingTypeSelect.setItems("COMPANY_CHARGER", "PUBLIC_STATION", "MIXED");
        chargingTypeSelect.setValue("COMPANY_CHARGER");
        chargingTypeSelect.setWidthFull();

        electricityRateField.setLabel("Electricity rate (CHF/kWh)");
        electricityRateField.setMin(0.01);
        electricityRateField.setStep(0.01);
        electricityRateField.setValue(0.28);
        electricityRateField.setWidthFull();
        electricityRateField.setSuffixComponent(new Span("CHF/kWh"));

        budgetMonthlyField.setLabel("Monthly budget (CHF)");
        budgetMonthlyField.setMin(1);
        budgetMonthlyField.setValue(1500.0);
        budgetMonthlyField.setWidthFull();
        budgetMonthlyField.setSuffixComponent(new Span("CHF/mo"));

        sizePreferenceSelect.setLabel("Size preference (optional)");
        sizePreferenceSelect.setItems("", "SUV", "SEDAN", "VAN", "COMPACT", "ESTATE");
        sizePreferenceSelect.setValue("");
        sizePreferenceSelect.setWidthFull();

        Button submitButton = new Button("Get Recommendations", VaadinIcon.SEARCH.create());
        submitButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        submitButton.setWidthFull();
        submitButton.getStyle()
                .set("background", "#2e7d32")
                .set("margin-top", "var(--lumo-space-s)");
        submitButton.addClickListener(e -> loadRecommendations());

        form.add(formTitle,
                dailyRouteKmField,
                numberOfDriversField,
                chargingTypeSelect,
                electricityRateField,
                budgetMonthlyField,
                sizePreferenceSelect,
                buildWeightSliders(),
                submitButton);

        card.add(form);
        return card;
    }

    // ── Weight sliders ────────────────────────────────────────────────────────────

    private Div buildWeightSliders() {
        Div section = new Div();
        section.getStyle()
                .set("border", "1px solid var(--lumo-contrast-10pct)")
                .set("border-radius", "var(--lumo-border-radius-m)")
                .set("padding", "var(--lumo-space-m)")
                .set("margin-top", "var(--lumo-space-xs)");

        Paragraph sectionTitle = new Paragraph("Scoring Weights");
        sectionTitle.getStyle()
                .set("font-weight", "600").set("margin", "0 0 var(--lumo-space-s) 0")
                .set("font-size", "var(--lumo-font-size-s)")
                .set("color", "var(--lumo-secondary-text-color)");

        String[] labels = {"Range", "Cost", "CO\u2082"};
        String[] colors = {"#1565c0", "#2e7d32", "#6a1b9a"};

        VerticalLayout sliders = new VerticalLayout();
        sliders.setPadding(false);
        sliders.setSpacing(false);
        sliders.getStyle().set("gap", "var(--lumo-space-s)");
        for (int i = 0; i < 3; i++) {
            sliders.add(buildSingleSlider(i, labels[i], colors[i]));
        }

        // Visual split bar
        Div barContainer = new Div();
        barContainer.getStyle()
                .set("display", "flex").set("height", "8px")
                .set("border-radius", "4px").set("overflow", "hidden")
                .set("margin-top", "var(--lumo-space-s)");

        rangeBar = new Div();
        rangeBar.getStyle().set("background", "#1565c0").set("width", "40%").set("transition", "width 0.1s");
        costBar = new Div();
        costBar.getStyle().set("background", "#2e7d32").set("width", "40%").set("transition", "width 0.1s");
        co2Bar = new Div();
        co2Bar.getStyle().set("background", "#6a1b9a").set("width", "20%").set("transition", "width 0.1s");
        barContainer.add(rangeBar, costBar, co2Bar);

        // Legend
        Div legend = new Div();
        legend.getStyle().set("display", "flex").set("gap", "var(--lumo-space-m)").set("margin-top", "4px");
        for (int i = 0; i < 3; i++) {
            Div dot = new Div();
            dot.getStyle()
                    .set("width", "8px").set("height", "8px").set("border-radius", "50%")
                    .set("background", colors[i]).set("flex-shrink", "0").set("align-self", "center");
            Span legendLabel = new Span(labels[i]);
            legendLabel.getStyle()
                    .set("font-size", "var(--lumo-font-size-xs)")
                    .set("color", "var(--lumo-secondary-text-color)");
            Div item = new Div(dot, legendLabel);
            item.getStyle().set("display", "flex").set("align-items", "center").set("gap", "4px");
            legend.add(item);
        }

        section.add(sectionTitle, sliders, barContainer, legend);
        return section;
    }

    private Div buildSingleSlider(int idx, String label, String color) {
        Div row = new Div();
        row.getStyle().set("display", "flex").set("flex-direction", "column").set("gap", "2px");

        Div labelRow = new Div();
        labelRow.getStyle()
                .set("display", "flex").set("justify-content", "space-between").set("align-items", "center");
        Span nameLabel = new Span(label);
        nameLabel.getStyle().set("font-size", "var(--lumo-font-size-s)").set("font-weight", "500");
        weightLabels[idx] = new Span(weights[idx] + "%");
        weightLabels[idx].getStyle()
                .set("font-size", "var(--lumo-font-size-s)").set("font-weight", "700").set("color", color);
        labelRow.add(nameLabel, weightLabels[idx]);

        Element slider = new Element("input");
        slider.setAttribute("type", "range");
        slider.setAttribute("min", "0");
        slider.setAttribute("max", "100");
        slider.setAttribute("step", "1");
        slider.setAttribute("value", String.valueOf(weights[idx]));
        slider.getStyle()
                .set("width", "100%")
                .set("accent-color", color)
                .set("cursor", "pointer");
        sliderElements[idx] = slider;

        slider.addEventListener("input", event -> {
            int newVal = (int) Math.round(
                    Double.parseDouble(event.getEventData().getString("event.target.value")));
            redistributeWeights(idx, newVal);
            updateSliderUI();
        }).addEventData("event.target.value");

        Div sliderWrapper = new Div();
        sliderWrapper.getElement().appendChild(slider);
        sliderWrapper.getStyle().set("width", "100%");

        row.add(labelRow, sliderWrapper);
        return row;
    }

    private void redistributeWeights(int movedIdx, int newValue) {
        newValue = Math.max(0, Math.min(100, newValue));
        int j = (movedIdx + 1) % 3;
        int k = (movedIdx + 2) % 3;
        int remaining = 100 - newValue;
        int sumJK = weights[j] + weights[k];
        if (sumJK == 0) {
            weights[j] = remaining / 2;
            weights[k] = remaining - weights[j];
        } else {
            weights[j] = Math.round(remaining * weights[j] / (float) sumJK);
            weights[k] = remaining - weights[j];
        }
        weights[movedIdx] = newValue;
    }

    private void updateSliderUI() {
        for (int i = 0; i < 3; i++) {
            sliderElements[i].executeJs("this.value = $0", weights[i]);
            weightLabels[i].setText(weights[i] + "%");
        }
        rangeBar.getStyle().set("width", weights[0] + "%");
        costBar.getStyle().set("width", weights[1] + "%");
        co2Bar.getStyle().set("width", weights[2] + "%");
    }

    // ── Results card ──────────────────────────────────────────────────────────────

    private Div buildResultsCard() {
        resultsGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_NO_BORDER);
        resultsGrid.setHeightFull();

        resultsGrid.addColumn(rec -> "#" + (currentResults.indexOf(rec) + 1))
                .setHeader("Rank").setWidth("65px").setFlexGrow(0);

        resultsGrid.addComponentColumn(rec -> {
            int score = rec.getSuitabilityScore().getScore();
            Span badge = new Span(String.valueOf(score));
            badge.getStyle()
                    .set("font-weight", "700").set("font-size", "var(--lumo-font-size-l)")
                    .set("color", scoreColor(score));
            return badge;
        }).setHeader("Score").setWidth("75px").setFlexGrow(0);

        resultsGrid.addColumn(rec -> rec.getBrand() + " " + rec.getModel())
                .setHeader("Vehicle").setAutoWidth(true).setSortable(true);

        resultsGrid.addComponentColumn(rec -> buildTypeBadge(rec.getVehicleType()))
                .setHeader("Type").setAutoWidth(true);

        resultsGrid.addColumn(rec -> rec.getRangeKm().getValue() + " km")
                .setHeader("Range").setAutoWidth(true).setSortable(true);

        resultsGrid.addComponentColumn(rec -> buildRangeBadge(rec.getRangeFit().isSufficient()))
                .setHeader("Range OK").setAutoWidth(true);

        resultsGrid.addColumn(rec -> formatAmount(rec.getMonthlyLeasePrice()))
                .setHeader("Lease/mo").setAutoWidth(true).setSortable(true);

        resultsGrid.addColumn(rec -> formatAmount(rec.getTco().getMonthlyTotal()))
                .setHeader("TCO/mo").setAutoWidth(true).setSortable(true);

        resultsGrid.addColumn(rec -> formatAmount(rec.getTco().getAnnualTotal()))
                .setHeader("TCO/yr").setAutoWidth(true).setSortable(true);

        resultsGrid.addColumn(rec -> String.format("%.0f kg", rec.getCo2Impact().getAnnualSavingsKg()))
                .setHeader("CO\u2082 saved/yr").setAutoWidth(true).setSortable(true);

        emptyLabel = new Paragraph(
                "Enter your fleet requirements and click \"Get Recommendations\" to see results.");
        emptyLabel.getStyle()
                .set("color", "var(--lumo-secondary-text-color)")
                .set("text-align", "center")
                .set("padding", "var(--lumo-space-xl)");

        resultsGrid.setVisible(false);

        H3 resultsTitle = new H3("Recommendations");
        resultsTitle.getStyle()
                .set("margin", "var(--lumo-space-m) var(--lumo-space-l) 0")
                .set("font-size", "var(--lumo-font-size-l)");

        Div card = new Div();
        card.getStyle()
                .set("background", "var(--lumo-base-color)")
                .set("border-radius", "var(--lumo-border-radius-l)")
                .set("box-shadow", "0 1px 4px rgba(0,0,0,0.08), 0 0 0 1px rgba(0,0,0,0.04)")
                .set("overflow", "hidden")
                .set("height", "100%")
                .set("display", "flex")
                .set("flex-direction", "column");
        card.add(resultsTitle, emptyLabel, resultsGrid);
        return card;
    }

    // ── Data loading ──────────────────────────────────────────────────────────────

    private void loadRecommendations() {
        try {
            String sizeStr = sizePreferenceSelect.getValue();
            VehicleType sizePreference = (sizeStr == null || sizeStr.isBlank())
                    ? null : VehicleType.valueOf(sizeStr);

            long electricityRateCents = Math.round(electricityRateField.getValue() * 100);
            long budgetCents = Math.round(budgetMonthlyField.getValue() * 100);

            FleetRequirements requirements = FleetRequirements.of(
                    Kilometre.of(dailyRouteKmField.getValue()),
                    NumberOfDrivers.of(numberOfDriversField.getValue()),
                    ChargingSetup.of(
                            ChargingType.valueOf(chargingTypeSelect.getValue()),
                            ElectricityRate.of(Amount.ofCents(electricityRateCents, Currency.CHF))
                    ),
                    Budget.of(Amount.ofCents(budgetCents, Currency.CHF)),
                    sizePreference,
                    ScoringWeights.of(weights[0], weights[1], weights[2])
            );

            currentResults = controller.recommend(requirements);
            resultsGrid.setItems(currentResults);
            boolean hasResults = !currentResults.isEmpty();
            resultsGrid.setVisible(hasResults);
            emptyLabel.setVisible(!hasResults);
            if (!hasResults) {
                emptyLabel.setText("No vehicles matched your requirements.");
            }
        } catch (Exception e) {
            Notification notification = Notification.show(
                    e.getMessage(), 5000, Notification.Position.BOTTOM_END);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────────

    private String scoreColor(int score) {
        if (score >= 70) return "#2e7d32";
        if (score >= 40) return "#e65100";
        return "#c62828";
    }

    private Span buildTypeBadge(VehicleType type) {
        Span badge = new Span(type.name());
        badge.getStyle()
                .set("padding", "3px 10px").set("border-radius", "12px")
                .set("font-size", "var(--lumo-font-size-xs)").set("font-weight", "500")
                .set("background", "var(--lumo-contrast-10pct)")
                .set("color", "var(--lumo-secondary-text-color)");
        return badge;
    }

    private Span buildRangeBadge(boolean sufficient) {
        Span badge = new Span(sufficient ? "Yes" : "No");
        badge.getStyle()
                .set("padding", "3px 10px").set("border-radius", "12px")
                .set("font-size", "var(--lumo-font-size-xs)").set("font-weight", "600");
        if (sufficient) {
            badge.getStyle().set("background", "#e8f5e9").set("color", "#2e7d32");
        } else {
            badge.getStyle().set("background", "#ffebee").set("color", "#c62828");
        }
        return badge;
    }

    private String formatAmount(Amount amount) {
        return String.format("%s %.2f", amount.getCurrency().name(), amount.getAmountInCents() / 100.0);
    }
}
