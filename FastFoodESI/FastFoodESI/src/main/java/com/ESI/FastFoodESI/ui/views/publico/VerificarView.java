package com.ESI.FastFoodESI.ui.views.publico;

import com.ESI.FastFoodESI.model.Cliente;
import com.ESI.FastFoodESI.repository.ClienteRepository;
import com.ESI.FastFoodESI.ui.layouts.MainLayout; // O LoginView si prefieres
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Route("verificar")
@AnonymousAllowed
public class VerificarView extends VerticalLayout implements BeforeEnterObserver {

    private final ClienteRepository clienteRepository;

    public VerificarView(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Map<String, List<String>> params = event.getLocation().getQueryParameters().getParameters();

        if (params.containsKey("code")) {
            String codigo = params.get("code").get(0);
            verificarCodigo(codigo);
        } else {
            add(new H1("❌ Enlace inválido"));
        }
    }

    private void verificarCodigo(String codigo) {
        Optional<Cliente> c = clienteRepository.findAll().stream()
                .filter(cli -> codigo.equals(cli.getCodigoVerificacion()))
                .findFirst();

        if (c.isPresent()) {
            Cliente cliente = c.get();
            if (cliente.isVerificado()) {
                add(new H1("⚠️ Esta cuenta ya estaba verificada"));
            } else {
                cliente.setVerificado(true);
                cliente.setCodigoVerificacion(null);
                clienteRepository.save(cliente);
                add(new H1("✅ ¡Cuenta verificada con éxito!"),
                        new Paragraph("Ya puedes iniciar sesión."));
            }
        } else {
            add(new H1("❌ Código de verificación incorrecto o caducado"));
        }
    }
}