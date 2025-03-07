package com.islasFilipinas; 

import java.io.*; 
import java.net.*; 
import java.util.*;

public class Servidor { 
	//Declaracion de constantes
    private static final int SERVER_PORT = 5000; 
    private static Set<PrintWriter> clientWriters = Collections.synchronizedSet(new HashSet<>()); 

    
    //Inicia el servidor
    public static void main(String[] args) { 
        System.out.println("El servidor está corriendo..."); // Mostrar mensaje de inicio del servidor

        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) { // Crear un ServerSocket para escuchar conexiones en el puerto especificado
            while (true) { 
   /*hilo*/        new ClientHandler(serverSocket.accept()).start(); // Aceptar una conexión entrante y manejarla en un nuevo hilo
            }
        } catch (IOException e) {
            e.printStackTrace(); 
        }
    }
    //Hilo que maneja clientes
    private static class ClientHandler extends Thread { 
        private Socket socket; 
        private String alias; 
        private PrintWriter out; 

        public ClientHandler(Socket socket) { 
            this.socket = socket;
        }

        public void run() { 
            try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream())); 
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) { 

                this.out = out;
                //syncronice  
                
                clientWriters.add(out); // Agregar el flujo de salida del cliente al conjunto

                alias = in.readLine(); 
                System.out.println(alias + " se ha unido al chat."); // Mostrar mensaje en el servidor
                broadcastMessage("Servidor: " + alias + " se ha unido al chat."); // Enviar mensaje a todos los clientes

                String message;
                while ((message = in.readLine()) != null) { 
                    if (message.equalsIgnoreCase("salir")) { // Si el cliente escribe "salir"
                        break; 
                    }
                    broadcastMessage(alias + ": " + message); // Enviar mensaje a todos los clientes
                }

            } catch (IOException e) {
                e.printStackTrace(); 
            } finally {
                closeConnection(); 
            }
        }

        private void broadcastMessage(String message) { 
            synchronized (clientWriters) { 
                for (PrintWriter writer : clientWriters) { 
                    writer.println(message); 
                }
                System.out.println(message);
            }
        }

        private void closeConnection() { 
            try {
                if (out != null) {
                    clientWriters.remove(out); 
                }
                if (alias != null) {
                    System.out.println(alias + " se ha desconectado."); 
                    broadcastMessage("Servidor: " + alias + " se ha desconectado."); 
                }
                socket.close(); 
            } catch (IOException e) {
                e.printStackTrace(); 
            }
        }
    }
}
