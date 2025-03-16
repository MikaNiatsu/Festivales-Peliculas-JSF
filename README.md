# 🎬 Gestión de Festivales y Películas con JSF

Aplicación web desarrollada en Java utilizando JSF (Jakarta Faces), que permite gestionar información sobre festivales de cine, películas, premios y certámenes. Incluye integración con bases de datos MySQL, generación de reportes en PDF y consumo de servicios web mediante HTTP.

---

## 🚀 Características Principales

- CRUD completo para entidades como Festivales, Películas, Certámenes y Premios.
- Interfaz web dinámica con JSF.
- Persistencia de datos con MySQL.
- Generación de reportes en PDF con iTextPDF.
- Consumo de APIs externas mediante OkHttp.
- Serialización y deserialización de datos JSON con Gson.

---

## 🛠️ Tecnologías Utilizadas

| Herramienta         | Versión     |
|---------------------|-------------|
| Java                | 11          |
| Jakarta Faces (JSF) | 3.0.0       |
| MySQL Connector/J   | 8.3.0       |
| iTextPDF            | 5.5.13.3    |
| Gson                | 2.10.1      |
| OkHttp              | 4.12.0      |
| Apache Commons IO   | 2.11.0      |
| Maven               | Wrapper incluido |

---

## 📂 Estructura del Proyecto

```
Festivales-Peliculas-JSF-master/
├── src/main/java/co/edu/unbosque/beans/       # Managed Beans (JSF)
├── src/main/webapp/                           # Vistas JSF (.xhtml)
├── pom.xml                                    # Configuración Maven
├── mvnw / mvnw.cmd                            # Wrapper Maven
└── .idea/, .mvn/                              # Configuración IDE y Maven
```

---

## ⚙️ Instalación y Ejecución

1. **Clonar el repositorio:**
   ```bash
   git clone https://github.com/usuario/Festivales-Peliculas-JSF.git
   cd Festivales-Peliculas-JSF
   ```

2. **Compilar el proyecto con Maven:**
   ```bash
   ./mvnw clean package
   ```

3. **Desplegar el archivo WAR en un servidor compatible (ej. Apache Tomcat):**
   - Copiar el archivo `target/VideosPeli.war` al directorio `webapps/` de Tomcat.
   - Iniciar Tomcat y acceder vía navegador: `http://localhost:8080/VideosPeli`

4. **Configuración de la base de datos:**
   - Crear una base de datos MySQL.
   - Configurar las credenciales y URL de conexión en el archivo correspondiente (posiblemente en un archivo `persistence.xml` o similar si se usa JPA/Hibernate).

---

## 📄 Reportes PDF

La aplicación permite generar reportes en formato PDF utilizando la biblioteca iTextPDF. Estos reportes pueden incluir listados de festivales, películas, premios, entre otros.
