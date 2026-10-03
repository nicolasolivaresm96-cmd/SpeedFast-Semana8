# SpeedFast - Certamen Semana 8

Proyecto desarrollado en Java para la gestión de pedidos, repartidores y entregas mediante una interfaz gráfica Swing y persistencia de datos en MySQL.

## Requisitos

- Java JDK 17
- MySQL Server
- Maven
- IntelliJ IDEA

## Base de datos

El proyecto incluye el archivo:

`speedfast_db.sql`

Este script crea la base de datos `speedfast_db` y las tablas necesarias:

- `pedidos`
- `repartidores`
- `entregas`

Antes de ejecutar la aplicación, ejecutar el archivo `speedfast_db.sql` en MySQL.

## Conexión a MySQL

La aplicación utiliza el usuario `root` y obtiene la contraseña de MySQL mediante una variable de entorno para evitar almacenar credenciales personales en el código fuente.

La variable requerida es:

`SPEEDFAST_DB_PASSWORD`

En IntelliJ IDEA puede configurarse desde:

Run → Edit Configurations → Environment variables

Ejemplo:

`SPEEDFAST_DB_PASSWORD=contraseña_mysql`

## Dependencia MySQL

El proyecto utiliza MySQL Connector/J mediante Maven.

La dependencia se encuentra declarada en `pom.xml`, por lo que Maven puede descargar automáticamente el conector necesario.

## Ejecución

1. Ejecutar `speedfast_db.sql` en MySQL.
2. Configurar la variable de entorno `SPEEDFAST_DB_PASSWORD`.
3. Abrir el proyecto con IntelliJ IDEA.
4. Esperar que Maven cargue las dependencias.
5. Ejecutar la clase `main.Main`.

## Funcionalidades

La aplicación permite realizar operaciones CRUD sobre:

- Pedidos
- Repartidores
- Entregas

Las operaciones se realizan mediante clases DAO utilizando JDBC, `PreparedStatement` y `ResultSet`.

La interfaz gráfica permite crear, consultar, actualizar y eliminar registros, mostrando los datos almacenados en MySQL mediante tablas.

En la gestión de entregas, los pedidos y repartidores disponibles se cargan dinámicamente desde la base de datos mediante `JComboBox`.

## Autor

Nicolás Olivares