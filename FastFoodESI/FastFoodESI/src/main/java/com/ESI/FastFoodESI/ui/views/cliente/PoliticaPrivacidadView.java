package com.ESI.FastFoodESI.ui.views.cliente;

import com.ESI.FastFoodESI.ui.layouts.MainLayout;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route(value = "politica-privacidad", layout = MainLayout.class)
@PageTitle("Política de Privacidad | FastFood ESI")
@AnonymousAllowed
public class PoliticaPrivacidadView extends VerticalLayout {

    public PoliticaPrivacidadView() {
        setSizeFull();
        setPadding(false);
        setAlignItems(Alignment.CENTER);
        getStyle().set("background-color", "#f5f5f5");
        getStyle().set("overflow", "auto");


        VerticalLayout card = new VerticalLayout();
        card.setWidth("100%");
        card.setMaxWidth("800px");
        card.setPadding(true);
        card.setSpacing(true);
        card.getStyle().set("background-color", "white");
        card.getStyle().set("border-radius", "12px");
        card.getStyle().set("box-shadow", "0 4px 12px rgba(0,0,0,0.1)");
        card.getStyle().set("margin-top", "40px");
        card.getStyle().set("margin-bottom", "40px");


        H1 titulo = new H1("Política de Privacidad y Protección de Datos");
        titulo.getStyle().set("font-size", "24px");
        titulo.getStyle().set("text-align", "center");

        card.add(titulo);

        card.add(crearSeccion("1. Responsable del Tratamiento",
                "El responsable del tratamiento de sus datos es FastFood ESI (Proyecto Académico), con domicilio en la Escuela Superior de Informática."));

        card.add(crearSeccion("2. Finalidad del Tratamiento",
                "Sus datos personales (nombre, correo, teléfono, dirección) serán utilizados exclusivamente para gestionar su cuenta de usuario, tramitar los pedidos realizados a través de la plataforma y enviar notificaciones relacionadas con el servicio."));

        card.add(crearSeccion("3. Legitimación",
                "La base legal para el tratamiento de sus datos es la ejecución del contrato de prestación de servicios (realización de pedidos) y el consentimiento expreso del usuario al registrarse."));

        card.add(crearSeccion("4. Destinatarios",
                "Sus datos no se cederán a terceros, salvo obligación legal o cuando sea estrictamente necesario para la prestación del servicio (ej. restaurantes asociados para la entrega del pedido)."));

        card.add(crearSeccion("5. Derechos del Usuario",
                "Usted tiene derecho a acceder, rectificar y suprimir sus datos (derecho al olvido), así como a limitar u oponerse a su tratamiento. Puede ejercer estos derechos a través de su perfil de usuario o contactando con nosotros."));

        card.add(new Hr());

        Paragraph notaFinal = new Paragraph("Última actualización: Enero 2026. Esta aplicación es un proyecto académico sin fines comerciales reales.");
        notaFinal.getStyle().set("font-size", "0.8em");
        notaFinal.getStyle().set("color", "gray");
        notaFinal.getStyle().set("text-align", "center");
        notaFinal.setWidthFull();

        card.add(notaFinal);

        add(card);
    }

    private VerticalLayout crearSeccion(String titulo, String contenido) {
        H3 h3 = new H3(titulo);
        h3.getStyle().set("font-size", "18px");
        h3.getStyle().set("margin-bottom", "5px");
        h3.getStyle().set("color", "#2c3e50");

        Paragraph p = new Paragraph(contenido);
        p.getStyle().set("text-align", "justify");

        VerticalLayout seccion = new VerticalLayout(h3, p);
        seccion.setPadding(false);
        seccion.setSpacing(false);
        return seccion;
    }
}