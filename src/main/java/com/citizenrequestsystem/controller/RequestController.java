package com.citizenrequestsystem.controller;

import com.citizenrequestsystem.model.request.Request;
import com.citizenrequestsystem.model.request.Status;
import com.citizenrequestsystem.model.request.Priority;
import com.citizenrequestsystem.model.user.User;
import com.citizenrequestsystem.service.RequestService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

public class RequestController {

    private final RequestService requestService;

    public RequestController(RequestService service) {
        this.requestService = service;
    }

    public void menu(User loggedUser) {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("\n1 - Create Request");
            System.out.println("2 - List Requests");
            System.out.println("0 - Exit");
            System.out.print("Choose option: ");
            int option = sc.nextInt();
            sc.nextLine(); // consume newline

            switch (option) {
                case 1 -> createRequest(sc, loggedUser);
                case 2 -> listRequests();
                case 0 -> { System.out.println("Exiting..."); return; }
                default -> System.out.println("Invalid option!");
            }
        }
    }

    private void createRequest(Scanner sc, User loggedUser) {
        System.out.print("Enter protocol: ");
        String protocol = sc.nextLine();
        System.out.print("Enter description: ");
        String description = sc.nextLine();

        // Usando construtor vazio + setters
        Request req = new Request();
        req.setProtocol(protocol);
        req.setDescription(description);
        req.setUser(loggedUser);           // vincula o usuário logado
        req.setStatus(Status.ABERTO);      // status inicial
        req.setPriority(Priority.MEDIA);   // prioridade padrão
        req.setCreatedAt(LocalDateTime.now());
        req.setUpdatedAt(LocalDateTime.now());

        requestService.createRequest(req);
        System.out.println("Request created!");
    }

    private void listRequests() {
        List<Request> requests = requestService.getAllRequests();
        if (requests.isEmpty()) {
            System.out.println("No requests found!");
            return;
        }

        for (Request r : requests) {
            System.out.println("Protocol: " + r.getProtocol() +
                    ", Description: " + r.getDescription() +
                    ", Status: " + r.getStatus() +
                    ", Priority: " + r.getPriority());
        }
    }
}