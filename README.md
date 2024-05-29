Esto es un ejemplo de cómo implementar servicios en android en segundo plano en una aplicación React con Capacitor. No necesitamos encapsular el código nativo en un plugin si no vamos a llamar a los métodos de android directamente desde React. En este proyecto llamaremos a un api de meteorología cada 15 segundos para comprobar si la temperatura en Madrid es superior a 20º y en caso afirmativo mostraremos una notificación al usuario.

	1. En primer lugar ejecutamos "npx create-react-app react_capacitor_background_service" para crear un proyecto React desde 0.
	
	2. Instalamos las dependencias con "npm install @capacitor/core @capacitor/cli @capacitor/android". Inicializamos Capacitor con "npx cap init" y añadimos android con "npx cap add android". Hacemos el build con "npm run build" y sincronizamos con "npx cap sync". Cuando queramos ejecutar el proyecto en android usaremos el comando "npx cap run android".

	3. Para poder hacer llamadas a la api "https://api.open-meteo.com/" necesitamos usar Retrofit. Para ello añadimos las dependencias en el fichero build.gradle del módulo app en nuestra plataforma Android: "implementation 'com.squareup.retrofit2:retrofit:2.9.0' implementation 'com.squareup.retrofit2:converter-gson:2.9.0'".

	4. Después creamos en el directorio donde se encuentra MainActivity.java una nueva clase "WeatherService" que hereda de Service. Aquí creamos un objeto Retrofit con baseUrl a la url de la API. 

	5. Creamos una interfaz "WeatherApiService" que utilizará Retrofit para hacer las llamadas a la API. Por cómo está estructurado el JSON que nos devuelve la API vamos a crear dos clases Java, una "WeatherResponse" para encapsular la respuesta y otra "CurrentData" para acceder al valor "temperature_2m". Usamos retrofit para crear un objeto WeatherApiService.

	6. Después creamos un método que crea el canal de notificaciones con NotificationManager y lo llamamos en el onCreate de WeatherService. También creamos un Handler para manejar el bucle de llamadas a la API a cada intervalo de tiempo. 

	7. A continuación creamos un método que compruebe los permisos muestre la notificación usando el canal creado previamente. Nos pedirá que añadamos el permiso "POST_NOTIFICATION" al Manifest. 

	8. Tras esto creamos un objeto Runnable que será el que ejecute las tareas en segundo plano. Hará la llamada a la API, procesará el resultado y en caso de cumplirse la condición llamará al método que muestra la notificación. Usaremos dentro el objeto handler para indicarle el intervalo de tiempo. También utilizaremos handler en el onStartCommand() de WeatherService para inicializar el runnable.

	9. Ahora vamos a MainActivity donde crearemos un Intent para arrancar el servicio.

	10. Por último debemos añadir una etiqueta <service> con el nombre de nuestro servicio en el Manifest dentro de <application>.

	11. Tras esto compilamos, sincronizamos y ya debería funcionar el servicio android en segundo plano.