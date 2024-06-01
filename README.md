# Juego Recetario Bribri

Esta aplicación esta desarrollada en el lenguaje de programación Kotlin, se seleccionó este lenguaje tomando en cuenta varias de sus características como que es conciso y seguro, además de ser el lenguaje oficial adoptado por Google para el desarrollo de aplicaciones Android desde 2017. También porque está estrechamente relacionado con JAVA y este último es uno de los lenguajes enseñados y utilizados en los cursos de la carrera de computación en la UCR.

En la pantalla principal de la aplicación se pueden ver las categorias de palabras disponibles para jugar, hay una opción de aleatorio que muestra palabras de cualquier categoría. Después de seleccionar alguna de las categorías, se entra al juego, donde en la parte superior aparece una palabra en bribri con su respectivo sonido, debajo aparecen 4 imágenes y se debe seleccionar la que está relacionada con la palabra de arriba. Aparecerán entre 5 y 10 palabras antes de mostrar los resultados de la ronda y volver a la pantalla inicial. Hay una barra de progreso de la ronda.

En la parte inferior hay un menú con 3 opciones: Jugar que es el mencionado anteriormente, Créditos donde aparecen los créditos de la aplicación, y Aprende donde se puede ver y estudiar todo el vocabulario ya que se encuentran todas las imágenes con sus respectivas escrituras y audios.

## Base de datos

### SQLite:

    Es una base de datos relacional liviana y embebida que se incluye de forma predeterminada en la mayoría de los dispositivos Android, ideal para aplicaciones móviles que necesitan almacenar datos localmente de forma rápida y eficiente. Se puede acceder a SQLite desde Kotlin utilizando la biblioteca estándar de Kotlin o mediante un ORM (Object Relational Mapper) como Room. En AndroidStudio se puede ver en forma de tablas la información almacenada en la base de datos con la herramienta App Inspection.

    Para mapear los datos a la base de datos se utiliza un archivo JSON ubicado en la carpeta assets, donde se utiliza el nombre de las imágenes y audios almacenados en la carpeta res, en drawable para las imagenes y raw para los audios. El campo "nombre" en este archivo JSON, es el nombre del objeto en lenguaje Bribri y es utilizado en distintas partes de la aplicación, se podría añadir otro campo como "nombre-espanol" para almacenar su traducción al español de ser necesario.

    Con solo agregar los archivos en las carpetas mencionadas y escribir los respectivos nombres siguiendo el formato en el archivo JSON, se actualiza la base de datos de la aplicación. Por lo cual a futuro se le podrían agregar palabras e incluso nuevas categorias de palabras de esta manera y debería verse reflejado en la aplicación. De igual forma para que no se muestre algún objeto en la aplicación solo es necesario borrar la referencia de ese objeto en el JSON, pero si no se va a utilizar más se recomienda también borrar la imagen y el audio para no aumentar el tamaño total de la aplicación innecesariamente.

    Hay dos tablas principales que son Palabra y Categoria. Categoria: IdCategoria, NombreCategoria, ImagenCategoria, PorcentajeCategoria. Palabra: IdPalabra, NombrePalabra, RutaAudio, RutaImagen, Aprendido, NombreCategoria.
