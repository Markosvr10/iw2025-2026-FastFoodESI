package com.ESI.FastFoodESI.ui.layouts.admin;

import com.ESI.FastFoodESI.security.SecurityService;
import com.ESI.FastFoodESI.ui.views.admin.EmpleadosView;
import com.ESI.FastFoodESI.ui.views.admin.EstadisticasView;
import com.ESI.FastFoodESI.ui.views.admin.NegociosView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.HighlightConditions;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.auth.AccessAnnotationChecker;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.springframework.security.core.userdetails.UserDetails;

public class PropietarioMainLayout extends AppLayout {

    private final AccessAnnotationChecker checker;
    private final SecurityService securityService;

    public PropietarioMainLayout(AccessAnnotationChecker checker, SecurityService securityService) {
        this.checker = checker;
        this.securityService = securityService;
        
        createHeader();
        createDrawer();
    }

    private void createHeader() {
        Image logo = new Image("images/LogoFastFoodESI.png", "FastFood ESI Logo");
        logo.setHeight("70px");
        logo.addClassNames(LumoUtility.Margin.Right.MEDIUM);

        HorizontalLayout header = new HorizontalLayout(new DrawerToggle(), logo);
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.setWidthFull();
        header.addClassNames(LumoUtility.Padding.Vertical.NONE, LumoUtility.Padding.Horizontal.MEDIUM);
        
        addToNavbar(header);
    }

    private void createDrawer() {
        Span appName = new Span("Panel Propietario");
        appName.addClassNames(LumoUtility.FontWeight.BOLD, LumoUtility.FontSize.LARGE, LumoUtility.Padding.LARGE);

        VerticalLayout navList = new VerticalLayout();
        navList.setSpacing(false);
        navList.setPadding(true);

        navList.add(createLink(VaadinIcon.SHOP, "Mis Negocios", NegociosView.class));
        navList.add(createLink(VaadinIcon.USERS, "Empleados", EmpleadosView.class));
        navList.add(createLink(VaadinIcon.CHART, "Estadísticas", EstadisticasView.class));

        Div spacer = new Div();
        spacer.addClassName(LumoUtility.Flex.GROW);

        VerticalLayout footer = createFooter();

        VerticalLayout drawerContent = new VerticalLayout(appName, navList, spacer, footer);
        drawerContent.setSizeFull();
        drawerContent.setPadding(false);
        drawerContent.setSpacing(false);
        
        addToDrawer(drawerContent);
    }

    private RouterLink createLink(VaadinIcon icon, String viewName, Class<? extends com.vaadin.flow.component.Component> viewClass) {
        RouterLink link = new RouterLink();
        link.setRoute(viewClass);

        Icon i = icon.create();
        i.addClassName(LumoUtility.Margin.Right.SMALL);
        i.setSize("20px");
        Span text = new Span(viewName);
        text.addClassName(LumoUtility.FontWeight.MEDIUM);

        HorizontalLayout layout = new HorizontalLayout(i, text);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        layout.setSpacing(false);
        link.add(layout);

        link.addClassNames(
                LumoUtility.Display.FLEX,
                LumoUtility.AlignItems.CENTER,
                LumoUtility.Padding.Horizontal.MEDIUM,
                LumoUtility.Padding.Vertical.MEDIUM,
                LumoUtility.TextColor.BODY,
                LumoUtility.BorderRadius.MEDIUM
        );
        
        link.setHighlightCondition(HighlightConditions.sameLocation());

        return link;
    }

    private VerticalLayout createFooter() {
        VerticalLayout footer = new VerticalLayout();
        footer.setPadding(true);
        footer.setSpacing(true);
        footer.addClassName(LumoUtility.Background.CONTRAST_5);

        UserDetails user = securityService.getAuthenticatedUser();
        String username = (user != null) ? user.getUsername() : "Propietario";

        Avatar avatar = new Avatar(username);
        avatar.addThemeVariants(com.vaadin.flow.component.avatar.AvatarVariant.LUMO_XSMALL);
        Span nameSpan = new Span(username);
        nameSpan.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.FontWeight.BOLD);
        
        HorizontalLayout userInfo = new HorizontalLayout(avatar, nameSpan);
        userInfo.setAlignItems(FlexComponent.Alignment.CENTER);

        Button logoutBtn = new Button("Cerrar Sesión", VaadinIcon.SIGN_OUT.create());
        logoutBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR);
        logoutBtn.setWidthFull();
        
        logoutBtn.addClickListener(e -> UI.getCurrent().getPage().setLocation("/logout"));
        footer.add(userInfo, logoutBtn);
        
        return footer;
    }
}
