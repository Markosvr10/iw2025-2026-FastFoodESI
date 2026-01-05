# 🍔 FastFoodESI – Plataforma de Gestión de Restaurantes

**FastFoodESI** es una aplicación web integral diseñada para la gestión y digitalización de cadenas de comida rápida (hamburgueserías, pizzerías, etc.).

Este proyecto ha sido desarrollado como parte de la asignatura **Ingeniería Web** (UCA · Curso 2025-2026), aplicando metodologías ágiles y un ciclo completo de **DevOps**.

---

## 🚀 Descripción del Proyecto

El sistema permite la administración centralizada de múltiples locales y tipos de cocina bajo una misma plataforma tecnológica.

### Funcionalidades principales:
* **Gestión de Carta:** Soporte para diversos tipos de productos (Hamburguesas, Pizzas, Bebidas, Menús y Ofertas).
* **Roles de Usuario:**
    * 👨‍💼 **Propietarios:** Gestión de negocios y empleados.
    * 👨‍🍳 **Empleados:** Gestión de pedidos y turnos (Mañana, Tarde, Noche).
    * 😋 **Clientes:** Realización de pedidos y seguimiento de estado.
* **Gestión de Pedidos:** Flujo de estados desde *Pendiente* hasta *Entregado* o *Cancelado*.

---

## 🛠️ Tecnologías (Stack Tecnológico)

El proyecto utiliza una arquitectura moderna basada en Java y servicios en la nube:

| Componente | Tecnología | Descripción |
| :--- | :--- | :--- |
| **Backend** | ![Java](https://img.shields.io/badge/Java-21-orange) ![Spring](https://img.shields.io/badge/Spring%20Boot-3-green) | Lógica de negocio, Repositorios JPA y Seguridad. |
| **Frontend** | ![Vaadin](https://img.shields.io/badge/Vaadin-24-blue) | Interfaz de usuario reactiva y componentes web. |
| **Base de Datos** | ![MySQL](https://img.shields.io/badge/MySQL-Production-00618a) ![H2](https://img.shields.io/badge/H2-Dev-gray) | Persistencia de datos (H2 en memoria para tests). |
| **Build Tool** | ![Maven](https://img.shields.io/badge/Maven-3.9-C71A36) | Gestión de dependencias y empaquetado. |
| **Calidad** | ![SonarCloud](https://img.shields.io/badge/SonarCloud-Quality-f3702a) | Análisis estático de código y deuda técnica. |
| **Despliegue** | ![AWS](https://img.shields.io/badge/AWS-Elastic%20Beanstalk-232F3E) | PaaS para el despliegue automático en la nube. |

---

## 🏗️ Arquitectura y DevOps

El proyecto implementa un flujo de **Integración Continua y Despliegue Continuo (CI/CD)** robusto mediante **GitHub Actions**.

### 🔄 Pipeline de CI/CD
Cada vez que se realiza un *push* a la rama `main`, se activan los siguientes procesos automáticamente:

1.  **Build & Test:** Compilación del proyecto con Maven y ejecución de pruebas unitarias.
2.  **Análisis de Calidad (SonarCloud):**
    * Detección de *Code Smells*, Bugs y Vulnerabilidades.
    * Verificación de cobertura de código.
    * *Quality Gate* para asegurar la mantenibilidad.
3.  **Empaquetado:** Generación del artefacto `.jar`.
4.  **Despliegue (CD):** Subida automática del binario a **AWS Elastic Beanstalk** (Entorno de Producción).

---

## ⚙️ Instalación y Ejecución Local

Si deseas levantar el proyecto en tu máquina local para desarrollo:

### Prerrequisitos
* Java JDK 21
* Maven instalado
* IDE (IntelliJ IDEA recomendado)

### Pasos
1.  **Clonar el repositorio:**
    ```bash
    git clone [https://github.com/tu-usuario/FastFoodESI.git](https://github.com/tu-usuario/FastFoodESI.git)
    ```

2.  **Acceder al directorio del proyecto:**
    ⚠️ Importante: El código fuente se encuentra en una subcarpeta.
    ```bash
    cd FastFoodESI/FastFoodESI
    ```

3.  **Configurar Base de Datos:**
    El proyecto está configurado por defecto para usar **H2 (Base de datos en memoria)** en el perfil `dev`, por lo que no necesitas instalar MySQL localmente para probarlo.
    * Los datos de prueba (Hamburguesas, Pizzas, Usuarios) se cargan automáticamente al iniciar (`data.sql`).

4.  **Ejecutar la aplicación:**
    ```bash
    mvn spring-boot:run
    ```

5.  **Acceso:**
    * Web: `http://localhost:8080`
    * Consola H2: `http://localhost:8080/h2-console`

---

## 🌐 Despliegue en la Nube

La versión de producción está desplegada en la infraestructura de Amazon Web Services.

🔗 **URL del Proyecto:** [http://fastfoodesi-env.eba-xxxx.us-east-1.elasticbeanstalk.com](http://fastfoodesi-env.eba-xxxx.us-east-1.elasticbeanstalk.com) 

> **Nota:** El despliegue se realiza sobre una instancia EC2 gestionada por Elastic Beanstalk, conectada a una base de datos RDS (MySQL).

---

## 👥 Equipo de Desarrollo

* **GRIÑÓN MARTÍNEZ, ÁLVARO**
* **MORENO MENDOZA, SALVADOR**
* **TARACENA NARANJO, LUIS**
* **VICENTE RÍOS, MARCOS**

---
*Ingeniería Web 2025-2026 - Universidad de Cádiz*

