package com.citizenrequestsystem;

import com.citizenrequestsystem.model.report.Authority;
import com.citizenrequestsystem.model.report.Attachment;
import com.citizenrequestsystem.model.report.Movement;
import com.citizenrequestsystem.model.report.Report;
import com.citizenrequestsystem.model.report.ReportStatus;
import com.citizenrequestsystem.model.report.ReportType;
import com.citizenrequestsystem.model.request.Category;
import com.citizenrequestsystem.model.request.Priority;
import com.citizenrequestsystem.model.request.Request;
import com.citizenrequestsystem.model.request.Sector;
import com.citizenrequestsystem.model.request.Status;
import com.citizenrequestsystem.model.user.Role;
import com.citizenrequestsystem.model.user.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;

public class CitizenRequestSystemApplication {

    private static final Scanner scanner = new Scanner(System.in);
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private static final Map<String, AuthAccount> accountsByEmail = new LinkedHashMap<>();
    private static final List<RequestRecord> requestRecords = new ArrayList<>();
    private static final List<ReportRecord> reportRecords = new ArrayList<>();

    private static AuthAccount loggedAccount;

    public static void main(String[] args) {
        Locale.setDefault(new Locale("pt", "BR"));
        seedAuthoritiesExample();
        showWelcomeScreen();
        authenticationFlow();
    }

    private static void showWelcomeScreen() {
        printLine();
        System.out.println("PORTAL DO CIDADÃO - SOLICITAÇÕES E DENÚNCIAS");
        printLine();
        System.out.println("Bem-vindo(a)! Este canal permite registrar solicitações de serviços");
        System.out.println("públicos e denúncias de forma simples, clara e segura.");
        System.out.println();
    }

    private static void authenticationFlow() {
        while (true) {
            printAuthMenu();
            int option = readOption(1, 3);

            switch (option) {
                case 1 -> registerUser();
                case 2 -> loginUser();
                case 3 -> {
                    System.out.println();
                    System.out.println("Encerrando o sistema. Até logo!");
                    return;
                }
            }
        }
    }

    private static void printAuthMenu() {
        printLine();
        System.out.println("ACESSO AO SISTEMA");
        printLine();
        System.out.println("1 - Fazer cadastro");
        System.out.println("2 - Fazer login");
        System.out.println("3 - Sair");
        System.out.print("Escolha uma opção: ");
    }

    private static void registerUser() {
        printLine();
        System.out.println("CADASTRO DE USUÁRIO");
        printLine();

        String name = readRequiredText("Nome completo: ");
        String email = readEmail();

        if (accountsByEmail.containsKey(email.toLowerCase())) {
            System.out.println();
            System.out.println("Já existe um cadastro com este e-mail.");
            return;
        }

        String password = readRequiredText("Crie uma senha: ");
        User user = new User(UUID.randomUUID().toString(), name, email, Role.CIDADAO);
        AuthAccount account = new AuthAccount(user, password);
        accountsByEmail.put(email.toLowerCase(), account);

        System.out.println();
        System.out.println("Cadastro realizado com sucesso.");
        System.out.println("Agora você já pode fazer login.");
    }

    private static void loginUser() {
        printLine();
        System.out.println("LOGIN");
        printLine();

        String email = readRequiredText("E-mail: ").toLowerCase();
        String password = readRequiredText("Senha: ");

        AuthAccount account = accountsByEmail.get(email);
        if (account == null || !account.password().equals(password)) {
            System.out.println();
            System.out.println("E-mail ou senha inválidos.");
            return;
        }

        loggedAccount = account;
        System.out.println();
        System.out.println("Login realizado com sucesso. Bem-vindo(a), " + loggedAccount.user().getName() + "!");
        mainMenu();
    }

    private static void mainMenu() {
        while (loggedAccount != null) {
            printMainMenu();
            int option = readOption(1, 4);

            switch (option) {
                case 1 -> createNewOccurrence();
                case 2 -> listMyOccurrences();
                case 3 -> searchByProtocol();
                case 4 -> logout();
            }
        }
    }

    private static void printMainMenu() {
        System.out.println();
        printLine();
        System.out.println("MENU PRINCIPAL");
        printLine();
        System.out.println("Usuário logado: " + loggedAccount.user().getName());
        System.out.println();
        System.out.println("1 - Nova solicitação ou denúncia");
        System.out.println("2 - Minhas solicitações e denúncias");
        System.out.println("3 - Buscar por número de protocolo");
        System.out.println("4 - Sair da conta");
        System.out.print("Escolha uma opção: ");
    }

    private static void createNewOccurrence() {
        System.out.println();
        printLine();
        System.out.println("NOVA SOLICITAÇÃO OU DENÚNCIA");
        printLine();
        System.out.println("1 - Solicitação de serviço público (identificada)");
        System.out.println("2 - Denúncia / solicitação anônima");
        System.out.println("3 - Voltar ao menu principal");
        System.out.print("Escolha uma opção: ");

        int option = readOption(1, 3);

        switch (option) {
            case 1 -> createRequest();
            case 2 -> createReport();
            case 3 -> System.out.println("Retornando ao menu principal...");
        }
    }

    private static void createRequest() {
        System.out.println();
        System.out.println("Você escolheu: Solicitação de serviço público.");

        Category category = chooseCategory();
        Sector sector = chooseSector();
        String location = readRequiredText("Informe o local do problema: ");
        String neighborhood = readRequiredText("Informe o bairro: ");
        String description = readRequiredText("Descreva a solicitação: ");

        List<com.citizenrequestsystem.model.request.Attachment> attachments = askRequestAttachments();

        System.out.println();
        System.out.println("Resumo da solicitação:");
        System.out.println("Categoria: " + formatEnum(category));
        System.out.println("Setor responsável: " + formatEnum(sector));
        System.out.println("Local: " + location);
        System.out.println("Bairro: " + neighborhood);
        System.out.println("Descrição: " + description);
        System.out.println("Anexos informados: " + (attachments.isEmpty() ? "Nenhum" : attachments.size()));
        System.out.println();
        System.out.println("1 - Finalizar envio");
        System.out.println("2 - Cancelar");
        System.out.print("Escolha uma opção: ");

        int confirmation = readOption(1, 2);
        if (confirmation == 2) {
            System.out.println();
            System.out.println("Operação cancelada. Nenhuma solicitação foi enviada.");
            return;
        }

        String protocol = generateProtocol("REQ");
        LocalDateTime now = LocalDateTime.now();

        Request request = new Request();
        request.setProtocol(protocol);
        request.setCategory(category);
        request.setSector(sector);
        request.setLocation(location);
        request.setNeighborhood(neighborhood);
        request.setDescription(description);
        request.setStatus(Status.ABERTO);
        request.setPriority(Priority.MEDIA);
        request.setCreatedAt(now);
        request.setUpdatedAt(now);
        request.setSlaDeadline(now.plusDays(7));
        request.setDelayJustification(null);
        request.setAnonymous(false);
        request.setUser(loggedAccount.user());
        request.setAttachments(attachments);

        List<com.citizenrequestsystem.model.request.Movement> movements = new ArrayList<>();
        movements.add(new com.citizenrequestsystem.model.request.Movement(
                "Solicitação registrada no portal e encaminhada para triagem.",
                now,
                loggedAccount.user()
        ));
        request.setMovements(movements);

        requestRecords.add(new RequestRecord(loggedAccount.user().getId(), request));

        System.out.println();
        System.out.println("Solicitação encaminhada com sucesso aos responsáveis.");
        System.out.println("Protocolo gerado: " + protocol);
        System.out.println("Situação atual: " + formatEnum(request.getStatus()));
    }

    private static void createReport() {
        System.out.println();
        System.out.println("Você escolheu: Denúncia / solicitação anônima.");
        System.out.println("Para preservar sua privacidade, serão solicitados apenas dados mínimos.");

        ReportType reportType = chooseReportType();
        Authority authority = chooseAuthority();
        String location = readRequiredText("Informe o local da ocorrência: ");
        String description = readRequiredText("Descreva a denúncia / solicitação: ");

        List<Attachment> attachments = askReportAttachments();

        System.out.println();
        System.out.println("Resumo da denúncia:");
        System.out.println("Categoria: " + formatEnum(reportType));
        System.out.println("Autoridade: " + authority.getName());
        System.out.println("Local: " + location);
        System.out.println("Descrição: " + description);
        System.out.println("Anexos informados: " + (attachments.isEmpty() ? "Nenhum" : attachments.size()));
        System.out.println();
        System.out.println("1 - Finalizar envio");
        System.out.println("2 - Cancelar");
        System.out.print("Escolha uma opção: ");

        int confirmation = readOption(1, 2);
        if (confirmation == 2) {
            System.out.println();
            System.out.println("Operação cancelada. Nenhuma denúncia foi enviada.");
            return;
        }

        String protocol = generateProtocol("REP");
        LocalDateTime now = LocalDateTime.now();

        List<Movement> movements = new ArrayList<>();
        movements.add(new Movement(
                UUID.randomUUID().toString(),
                "Denúncia registrada no portal e encaminhada para análise.",
                now
        ));

        Report report = new Report(
                UUID.randomUUID().toString(),
                description,
                ReportStatus.RECEBIDO,
                null,
                protocol,
                reportType,
                location,
                true,
                now,
                authority,
                attachments,
                movements
        );

        reportRecords.add(new ReportRecord(loggedAccount.user().getId(), report));

        System.out.println();
        System.out.println("Denúncia encaminhada com sucesso aos responsáveis.");
        System.out.println("Protocolo gerado: " + protocol);
        System.out.println("Situação atual: " + formatEnum(report.getStatus()));
    }

    private static void listMyOccurrences() {
        System.out.println();
        printLine();
        System.out.println("MINHAS SOLICITAÇÕES E DENÚNCIAS");
        printLine();

        List<ProtocolItem> items = new ArrayList<>();

        for (RequestRecord record : requestRecords) {
            if (record.ownerUserId().equals(loggedAccount.user().getId())) {
                items.add(ProtocolItem.fromRequest(record.request()));
            }
        }

        for (ReportRecord record : reportRecords) {
            if (record.ownerUserId().equals(loggedAccount.user().getId())) {
                items.add(ProtocolItem.fromReport(record.report()));
            }
        }

        if (items.isEmpty()) {
            System.out.println("Você ainda não possui solicitações ou denúncias cadastradas.");
            return;
        }

        items.sort(Comparator.comparing(ProtocolItem::createdAt).reversed());

        for (ProtocolItem item : items) {
            System.out.println("Protocolo: " + item.protocol());
            System.out.println("Tipo: " + item.type());
            System.out.println("Categoria: " + item.category());
            System.out.println("Status: " + item.status());
            System.out.println("Data: " + DATE_TIME_FORMATTER.format(item.createdAt()));
            System.out.println("Resumo: " + item.description());
            printLine();
        }
    }

    private static void searchByProtocol() {
        System.out.println();
        printLine();
        System.out.println("BUSCA POR PROTOCOLO");
        printLine();
        String protocol = readRequiredText("Digite o número do protocolo: ").toUpperCase();

        Request foundRequest = findRequestByProtocol(protocol, loggedAccount.user().getId());
        if (foundRequest != null) {
            showRequestDetails(foundRequest);
            return;
        }

        Report foundReport = findReportByProtocol(protocol, loggedAccount.user().getId());
        if (foundReport != null) {
            showReportDetails(foundReport);
            return;
        }

        System.out.println();
        System.out.println("Nenhum protocolo foi encontrado para a sua conta.");
    }

    private static void showRequestDetails(Request request) {
        System.out.println();
        printLine();
        System.out.println("DETALHES DA SOLICITAÇÃO");
        printLine();
        System.out.println("Protocolo: " + request.getProtocol());
        System.out.println("Tipo: Solicitação de serviço público");
        System.out.println("Categoria: " + formatEnum(request.getCategory()));
        System.out.println("Setor responsável: " + formatEnum(request.getSector()));
        System.out.println("Status atual: " + formatEnum(request.getStatus()));
        System.out.println("Prioridade: " + formatEnum(request.getPriority()));
        System.out.println("Local: " + request.getLocation());
        System.out.println("Bairro: " + request.getNeighborhood());
        System.out.println("Descrição: " + request.getDescription());
        System.out.println("Criado em: " + formatDateTime(request.getCreatedAt()));
        System.out.println("Última atualização: " + formatDateTime(request.getUpdatedAt()));
        System.out.println("Prazo previsto: " + formatDateTime(request.getSlaDeadline()));
        System.out.println("Anexos informados: " + request.getAttachments().size());
        System.out.println("Situação do atendimento: A solicitação está registrada e disponível para acompanhamento.");
    }

    private static void showReportDetails(Report report) {
        System.out.println();
        printLine();
        System.out.println("DETALHES DA DENÚNCIA / SOLICITAÇÃO ANÔNIMA");
        printLine();
        System.out.println("Protocolo: " + report.getProtocol());
        System.out.println("Tipo: Denúncia / solicitação anônima");
        System.out.println("Categoria: " + formatEnum(report.getType()));
        System.out.println("Autoridade responsável: " + report.getAuthority().getName());
        System.out.println("Status atual: " + formatEnum(report.getStatus()));
        System.out.println("Local: " + report.getLocation());
        System.out.println("Descrição: " + report.getDescription());
        System.out.println("Criado em: " + formatDateTime(report.getCreatedAt()));
        System.out.println("Anexos informados: " + report.getAttachments().size());
        System.out.println("Sigilo: Manifestação anônima");
        System.out.println("Situação do atendimento: A denúncia foi registrada e encaminhada para análise.");
    }

    private static void logout() {
        loggedAccount = null;
        System.out.println();
        System.out.println("Você saiu da conta com sucesso.");
    }

    private static Category chooseCategory() {
        System.out.println();
        System.out.println("Escolha a categoria do problema:");
        Category[] values = Category.values();
        for (int i = 0; i < values.length; i++) {
            System.out.println((i + 1) + " - " + formatEnum(values[i]));
        }
        System.out.print("Opção: ");
        int option = readOption(1, values.length);
        return values[option - 1];
    }

    private static Sector chooseSector() {
        System.out.println();
        System.out.println("Escolha o setor responsável:");
        Sector[] values = Sector.values();
        for (int i = 0; i < values.length; i++) {
            System.out.println((i + 1) + " - " + formatEnum(values[i]));
        }
        System.out.print("Opção: ");
        int option = readOption(1, values.length);
        return values[option - 1];
    }

    private static ReportType chooseReportType() {
        System.out.println();
        System.out.println("Escolha a categoria da denúncia:");
        ReportType[] values = ReportType.values();
        for (int i = 0; i < values.length; i++) {
            System.out.println((i + 1) + " - " + formatEnum(values[i]));
        }
        System.out.print("Opção: ");
        int option = readOption(1, values.length);
        return values[option - 1];
    }

    private static Authority chooseAuthority() {
        System.out.println();
        System.out.println("Escolha a autoridade responsável:");

        List<Authority> authorities = buildAuthorities();
        for (int i = 0; i < authorities.size(); i++) {
            System.out.println((i + 1) + " - " + authorities.get(i).getName());
        }
        System.out.print("Opção: ");

        int option = readOption(1, authorities.size());
        return authorities.get(option - 1);
    }

    private static List<com.citizenrequestsystem.model.request.Attachment> askRequestAttachments() {
        List<com.citizenrequestsystem.model.request.Attachment> attachments = new ArrayList<>();

        System.out.println();
        System.out.print("Deseja anexar Foto, Vídeo ou PDF? (sim/não): ");
        String answer = readRequiredText(null).toLowerCase();
        if (!answer.equals("sim")) {
            return attachments;
        }

        while (true) {
            String attachmentType = readAttachmentType();
            attachments.add(new com.citizenrequestsystem.model.request.Attachment(
                    attachmentType.toLowerCase() + "-" + (attachments.size() + 1),
                    attachmentType,
                    "anexo-informado-na-cli",
                    LocalDateTime.now()
            ));

            System.out.print("Deseja informar outro anexo? (sim/não): ");
            String continueAnswer = readRequiredText(null).toLowerCase();
            if (!continueAnswer.equals("sim")) {
                break;
            }
        }

        return attachments;
    }

    private static List<Attachment> askReportAttachments() {
        List<Attachment> attachments = new ArrayList<>();

        System.out.println();
        System.out.print("Deseja anexar Foto, Vídeo ou PDF? (sim/não): ");
        String answer = readRequiredText(null).toLowerCase();
        if (!answer.equals("sim")) {
            return attachments;
        }

        while (true) {
            String attachmentType = readAttachmentType();
            attachments.add(new Attachment(
                    UUID.randomUUID().toString(),
                    attachmentType.toLowerCase() + "-" + (attachments.size() + 1),
                    attachmentType
            ));

            System.out.print("Deseja informar outro anexo? (sim/não): ");
            String continueAnswer = readRequiredText(null).toLowerCase();
            if (!continueAnswer.equals("sim")) {
                break;
            }
        }

        return attachments;
    }

    private static String readAttachmentType() {
        while (true) {
            System.out.print("Digite o tipo do anexo (Foto, Vídeo ou PDF): ");
            String value = readRequiredText(null);
            String normalized = normalize(value);

            if (normalized.equals("foto")) {
                return "Foto";
            }
            if (normalized.equals("video") || normalized.equals("vídeo")) {
                return "Vídeo";
            }
            if (normalized.equals("pdf")) {
                return "PDF";
            }

            System.out.println("Opção inválida. Digite apenas: Foto, Vídeo ou PDF.");
        }
    }

    private static String readEmail() {
        while (true) {
            String email = readRequiredText("E-mail: ");
            if (email.contains("@") && email.contains(".")) {
                return email;
            }
            System.out.println("E-mail inválido. Tente novamente.");
        }
    }

    private static String readRequiredText(String message) {
        while (true) {
            if (message != null) {
                System.out.print(message);
            }
            String value = scanner.nextLine().trim();
            if (!value.isBlank()) {
                return value;
            }
            System.out.println("Campo obrigatório. Tente novamente.");
        }
    }

    private static int readOption(int min, int max) {
        while (true) {
            String value = scanner.nextLine().trim();
            try {
                int option = Integer.parseInt(value);
                if (option >= min && option <= max) {
                    return option;
                }
            } catch (NumberFormatException ignored) {
            }
            System.out.print("Opção inválida. Digite um número entre " + min + " e " + max + ": ");
        }
    }

    private static String generateProtocol(String prefix) {
        return prefix + "-" + System.currentTimeMillis();
    }

    private static String formatEnum(Enum<?> value) {
        String[] parts = value.name().split("_");
        StringBuilder builder = new StringBuilder();
        for (String part : parts) {
            if (!builder.isEmpty()) {
                builder.append(' ');
            }
            builder.append(part.substring(0, 1)).append(part.substring(1).toLowerCase());
        }
        return builder.toString();
    }

    private static String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "Não informado";
        }
        return DATE_TIME_FORMATTER.format(dateTime);
    }

    private static String normalize(String value) {
        return value
                .trim()
                .toLowerCase()
                .replace("á", "a")
                .replace("à", "a")
                .replace("â", "a")
                .replace("ã", "a")
                .replace("é", "e")
                .replace("ê", "e")
                .replace("í", "i")
                .replace("ó", "o")
                .replace("ô", "o")
                .replace("õ", "o")
                .replace("ú", "u")
                .replace("ç", "c");
    }

    private static Request findRequestByProtocol(String protocol, String userId) {
        for (RequestRecord record : requestRecords) {
            if (record.ownerUserId().equals(userId) && record.request().getProtocol().equalsIgnoreCase(protocol)) {
                return record.request();
            }
        }
        return null;
    }

    private static Report findReportByProtocol(String protocol, String userId) {
        for (ReportRecord record : reportRecords) {
            if (record.ownerUserId().equals(userId) && record.report().getProtocol().equalsIgnoreCase(protocol)) {
                return record.report();
            }
        }
        return null;
    }

    private static void seedAuthoritiesExample() {
        AuthAccount demo = new AuthAccount(
                new User(UUID.randomUUID().toString(), "Usuário de Exemplo", "demo@portal.com", Role.CIDADAO),
                "123"
        );
        accountsByEmail.put(demo.user().getEmail().toLowerCase(), demo);
    }

    private static List<Authority> buildAuthorities() {
        List<Authority> authorities = new ArrayList<>();
        authorities.add(new Authority(UUID.randomUUID().toString(), "Polícia Militar", Sector.SEGURANCA));
        authorities.add(new Authority(UUID.randomUUID().toString(), "Polícia Civil", Sector.SEGURANCA));
        authorities.add(new Authority(UUID.randomUUID().toString(), "Guarda Municipal", Sector.SEGURANCA));
        authorities.add(new Authority(UUID.randomUUID().toString(), "Defesa Civil", Sector.SEGURANCA));
        authorities.add(new Authority(UUID.randomUUID().toString(), "Conselho Tutelar", Sector.EDUCACAO));
        authorities.add(new Authority(UUID.randomUUID().toString(), "Vigilância Sanitária", Sector.SAUDE));
        return authorities;
    }

    private static void printLine() {
        System.out.println("------------------------------------------------------------");
    }

    private record AuthAccount(User user, String password) {
    }

    private record RequestRecord(String ownerUserId, Request request) {
    }

    private record ReportRecord(String ownerUserId, Report report) {
    }

    private record ProtocolItem(String protocol, String type, String category, String status,
                                LocalDateTime createdAt, String description) {
        private static ProtocolItem fromRequest(Request request) {
            return new ProtocolItem(
                    request.getProtocol(),
                    "Solicitação de serviço público",
                    formatEnum(request.getCategory()),
                    formatEnum(request.getStatus()),
                    request.getCreatedAt(),
                    request.getDescription()
            );
        }

        private static ProtocolItem fromReport(Report report) {
            return new ProtocolItem(
                    report.getProtocol(),
                    "Denúncia / solicitação anônima",
                    formatEnum(report.getType()),
                    formatEnum(report.getStatus()),
                    report.getCreatedAt(),
                    report.getDescription()
            );
        }
    }
}