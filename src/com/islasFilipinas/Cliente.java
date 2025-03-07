package com.islasFilipinas; 

import java.io.*; 
import java.net.*; 

public class Cliente { 
	
	//constantes
	
    private static final String SERVER_ADDRESS = "localhost" ;//"192.168.30.193"; 
    private static final int SERVER_PORT = 5000; 

    public static void main(String[] args) { 
    	//Establece una conexion
    	
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true); // Crear un PrintWriter para enviar datos al servidor
             BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in))) { // Crear un BufferedReader para leer datos del usuario
        	//envia el alias del usuario
            System.out.print("Ingresa tu alias: "); 
            String alias = userInput.readLine(); 
            out.println(alias); 
            System.out.println(in.readLine()); 

            // Crear un hilo para manejar la entrada del usuario
            Thread userInputThread = new Thread(() -> {
                try {
                    String userMessage;
                    while ((userMessage = userInput.readLine()) != null) { // Leer mensajes del usuario
                        if (userMessage.equalsIgnoreCase("salir")) { // Si el usuario escribe "salir"
                            out.println("salir"); // Enviar el comando "salir" al servidor
                            break; 
                        }
                        out.println(userMessage); // Enviar el mensaje del usuario al servidor
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            userInputThread.start(); 

            String message;
            while ((message = in.readLine()) != null) { // Leer mensajes del servidor
                System.out.println(message); // Mostrar mensajes del servidor
            }

        } catch (IOException e) {
            e.printStackTrace(); 
        }
    }
}
