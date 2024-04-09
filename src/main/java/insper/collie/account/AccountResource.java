package insper.collie.account;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;


@RestController
@Tag(name = "Account", description = "API de Contas")
public class AccountResource implements AccountController {

    @Autowired
    private AccountService accountService;


    @GetMapping("/accounts/info")
    @Operation(summary = "Obter informações do sistema", description = "Retorna informações básicas do sistema e do ambiente de execução.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Informações obtidas com sucesso", content = @Content(schema = @Schema(implementation = Map.class)))
        })
    public ResponseEntity<Map<String, String>> info() {
        return new ResponseEntity<Map<String, String>>(
            Map.ofEntries(
                Map.entry("microservice.name", AccountApplication.class.getSimpleName()),
                Map.entry("os.arch", System.getProperty("os.arch")),
                Map.entry("os.name", System.getProperty("os.name")),
                Map.entry("os.version", System.getProperty("os.version")),
                Map.entry("file.separator", System.getProperty("file.separator")),
                Map.entry("java.class.path", System.getProperty("java.class.path")),
                Map.entry("java.home", System.getProperty("java.home")),
                Map.entry("java.vendor", System.getProperty("java.vendor")),
                Map.entry("java.vendor.url", System.getProperty("java.vendor.url")),
                Map.entry("java.version", System.getProperty("java.version")),
                Map.entry("line.separator", System.getProperty("line.separator")),
                Map.entry("path.separator", System.getProperty("path.separator")),
                Map.entry("user.dir", System.getProperty("user.dir")),
                Map.entry("user.home", System.getProperty("user.home")),
                Map.entry("user.name", System.getProperty("user.name")),
                Map.entry("jar", new java.io.File(
                    AccountApplication.class.getProtectionDomain()
                        .getCodeSource()
                        .getLocation()
                        .getPath()
                    ).toString())
            ), HttpStatus.OK
        );
    }

    
    @Override
    @Operation(summary = "Criar uma nova conta", description = "Cria uma nova conta de usuário e retorna o objeto criado com seu ID.",
        responses = {
            @ApiResponse(responseCode = "201", description = "Conta criada com sucesso", content = @Content(schema = @Schema(implementation = AccountOut.class))),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos")
        })
    public ResponseEntity<AccountOut> create(AccountIn in) {
        // parser
        Account account = AccountParser.to(in);
        // insert using service
        account = accountService.create(account);
        // return
        return ResponseEntity.created(
            ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(account.id())
                .toUri())
            .body(AccountParser.to(account));
    }

    @Override
    @Operation(summary = "Atualizar conta", description = "Atualiza informações de uma conta de usuário existente.",
    responses = {
        @ApiResponse(responseCode = "200", description = "Conta atualizada com sucesso", content = @Content(schema = @Schema(implementation = AccountOut.class))),
        @ApiResponse(responseCode = "404", description = "Conta não encontrada"),
        @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos")
    })
    public ResponseEntity<AccountOut> update(String idUser, String id, AccountIn in) {

        Account account = AccountParser.to(in);
        account = accountService.update(idUser, id, account);
        if(account == null){
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(AccountParser.to(account));
    }

    @Override
    @Operation(summary = "Login de usuário", description = "Autentica um usuário com base no email e senha fornecidos.",
    responses = {
        @ApiResponse(responseCode = "200", description = "Usuário autenticado com sucesso", content = @Content(schema = @Schema(implementation = AccountOut.class))),
        @ApiResponse(responseCode = "403", description = "Credenciais inválidas ou acesso negado")
    })
    public ResponseEntity<AccountOut> login(LoginIn in) {
        Account account = accountService.login(in.email(), in.password());
        if (account == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(AccountParser.to(account));
    }

    @Override
    @Operation(summary = "Obter detalhes da conta", description = "Retorna detalhes de uma conta de usuário específica com base no ID do usuário.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Detalhes da conta retornados com sucesso", content = @Content(schema = @Schema(implementation = AccountOut.class))),
            @ApiResponse(responseCode = "404", description = "Conta não encontrada")
        })
    public ResponseEntity<AccountOut> read(String idUser, String roleUser) {
        final AccountOut account = AccountOut.builder()
            .id(idUser)
            .name(roleUser)
            .build();
        return ResponseEntity.ok(account);
    }

    @Override
    public ResponseEntity<AccountOut> getAccount(String idUser) {

        Account account = accountService.read(idUser);
        if(account == null){
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(AccountParser.to(account));
    }

    @Override
    public ResponseEntity<Boolean> isAccount(String id){

        Boolean isAccount = accountService.isAccount(id);

        return ResponseEntity.ok(isAccount);
    }
    
}
