package com.ESI.FastFoodESI.ui.views.admin;

import com.vaadin.flow.component.select.Select;
import com.ESI.FastFoodESI.model.Producto;
import com.ESI.FastFoodESI.service.admin.EstadisticasService;
import com.ESI.FastFoodESI.dto.EstadisticaDTO;
import com.ESI.FastFoodESI.dto.RankingItemDTO;
import com.ESI.FastFoodESI.ui.layouts.admin.PropietarioMainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.combobox.ComboBox; 
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

import java.util.List;

@Route(value = "admin/estadisticas", layout = PropietarioMainLayout.class)
@PageTitle("Estadísticas | FastFood ESI")
@RolesAllowed("PROPIETARIO") 
public class EstadisticasView extends VerticalLayout {

    private final EstadisticasService service;
    
    private Grid<RankingItemDTO> gridEmpleados;
    private Grid<RankingItemDTO> gridNegocios;
    private Grid<RankingItemDTO> gridProductos;
    private ComboBox<String> periodoSelector;

    public EstadisticasView(EstadisticasService service) {
        this.service = service;
        
        addClassName("estadisticas-view");
        
        setWidthFull(); 
        setHeight(null);   
        setPadding(true);
        setSpacing(true);

        add(new H2("Panel de Control - Pedidos y Stock"));

        EstadisticaDTO stats = service.obtenerEstadisticasPedidos();
        add(createKpiSection(stats));

        add(new H4("🏆 Rankings Top 5"));
        add(createRankingFilter());      
        add(createRankingsLayout());     
        
        updateRankings("HISTORICO");

        add(new H4("⚠️ Alerta: Productos con Stock Bajo (< 20 unidades)"));
        add(createLowStockGrid());
        
        Div spacer = new Div();
        spacer.setHeight("50px");
        add(spacer);
    }


    private Component createRankingFilter() {
        Select<String> select = new Select<>();
        select.setLabel("Periodo de Análisis");
        
        select.setItems("DIA", "MES", "ANNO", "HISTORICO");
        select.setItemLabelGenerator(item -> {
            switch(item) {
                case "DIA": return "Hoy";
                case "MES": return "Este Mes";
                case "ANNO": return "Este Año";
                default: return "Histórico Completo";
            }
        });
        
        select.setValue("HISTORICO"); 
        select.addValueChangeListener(e -> updateRankings(e.getValue()));
        
        return select;
    }

    private Component createRankingsLayout() {
        FlexLayout layout = new FlexLayout();
        layout.setFlexWrap(FlexLayout.FlexWrap.WRAP);
        layout.getStyle().set("gap", "20px");
        layout.setWidthFull();

        gridEmpleados = createSmallGrid("Top Empleados (Ventas)", true);
        gridNegocios = createSmallGrid("Top Negocios (Facturación)", true);
        gridProductos = createSmallGrid("Top Productos (Cantidad)", false);

        layout.add(gridEmpleados, gridNegocios, gridProductos);
        return layout;
    }

    private Grid<RankingItemDTO> createSmallGrid(String titulo, boolean isMoney) {
        Grid<RankingItemDTO> grid = new Grid<>(RankingItemDTO.class, false);
        
        grid.addColumn(RankingItemDTO::getNombre)
            .setHeader(titulo)
            .setAutoWidth(true)
            .setFlexGrow(1);
            
        grid.addColumn(item -> item.getValor() + (isMoney ? " €" : ""))
            .setHeader("Total")
            .setAutoWidth(true)
            .setFlexGrow(0);
        
        grid.addThemeVariants(GridVariant.LUMO_COMPACT, GridVariant.LUMO_ROW_STRIPES);
        grid.setHeight("250px");
        
        grid.getStyle().set("flex", "1 1 300px"); 
        
        return grid;
    }

    private void updateRankings(String periodo) {
        if (periodo == null) return;
        gridEmpleados.setItems(service.getRankingEmpleados(periodo));
        gridNegocios.setItems(service.getRankingNegocios(periodo));
        gridProductos.setItems(service.getRankingProductos(periodo));
    }


    private Component createKpiSection(EstadisticaDTO stats) {
        FlexLayout layout = new FlexLayout();
        layout.setFlexWrap(FlexLayout.FlexWrap.WRAP);
        layout.getStyle().set("gap", "20px"); 
        layout.setWidthFull();

        layout.add(createCard("Pedidos Hoy", String.valueOf(stats.getnPedidosDia()), VaadinIcon.TIMER, "green"));
        layout.add(createCard("Pedidos Este Mes", String.valueOf(stats.getnPedidosMes()), VaadinIcon.CALENDAR, "blue"));
        layout.add(createCard("Pedidos Este Año", String.valueOf(stats.getnPedidosAnno()), VaadinIcon.CHART, "orange"));
        layout.add(createCard("Total Histórico", String.valueOf(stats.getnPedidosTotal()), VaadinIcon.ARCHIVE, "purple"));

        return layout;
    }

    private Grid<Producto> createLowStockGrid() {
        Grid<Producto> grid = new Grid<>(Producto.class, false);
        
        grid.addColumn(Producto::getNombre)
            .setHeader("Producto")
            .setAutoWidth(true)
            .setFlexGrow(1); 

        grid.addColumn(p -> p.getImporte() + " €")
            .setHeader("Precio")
            .setAutoWidth(true)
            .setFlexGrow(0);
        
        grid.addComponentColumn(producto -> {
            Span stockSpan = new Span(String.valueOf(producto.getStock()));
            stockSpan.getElement().getThemeList().add("badge error"); 
            return stockSpan;
        }).setHeader("Stock Actual")
          .setAutoWidth(true)
          .setFlexGrow(0);

        List<Producto> productosAlert = service.obtenerProductosBajoStock(20);
        grid.setItems(productosAlert);
        
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_COMPACT);
        
        grid.setWidthFull(); 
        grid.setHeight("300px"); 
        
        grid.getStyle().set("flex-shrink", "0"); 
        
        return grid;
    }

    private Component createCard(String title, String value, VaadinIcon icon, String color) {
        Div card = new Div();
        card.addClassNames("bg-base", "shadow-s", "rounded-m", "p-l");
        card.getStyle().set("min-width", "200px"); 
        card.getStyle().set("display", "flex");
        card.getStyle().set("align-items", "center");
        card.getStyle().set("gap", "15px");
        card.getStyle().set("border-left", "5px solid " + color); 

        Icon i = icon.create();
        i.setSize("40px");
        i.setColor(color);

        VerticalLayout textLayout = new VerticalLayout();
        textLayout.setPadding(false);
        textLayout.setSpacing(false);
        
        Span titleSpan = new Span(title);
        titleSpan.getStyle().set("color", "gray");
        titleSpan.getStyle().set("font-size", "0.9em");

        Span valueSpan = new Span(value);
        valueSpan.getStyle().set("font-weight", "bold");
        valueSpan.getStyle().set("font-size", "1.5em");

        textLayout.add(valueSpan, titleSpan);
        card.add(i, textLayout);
        return card;
    }
}