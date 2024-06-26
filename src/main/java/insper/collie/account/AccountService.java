package insper.collie.account;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import insper.collie.account.exceptions.AccountNotFoundException;
import insper.collie.account.exceptions.EmailAlreadyExistsException;
import insper.collie.account.exceptions.EmailOrPasswordIncorrectException;
import lombok.NonNull;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    


    public Account create(Account in) {
        if (accountRepository.existsByEmail(in.email())) throw new EmailAlreadyExistsException(in.email());
        in.hash(calculateHash(in.password()));
        in.password(null);
        return accountRepository.save(new AccountModel(in)).to();
    }

    public Account read(@NonNull String id) {
        Account account = accountRepository.findById(id).map(AccountModel::to).orElse(null);
        if (account == null) throw new AccountNotFoundException(id);
        return account;
    }

    public Account login(String email, String password) {
        String hash = calculateHash(password);
        Account account = accountRepository.findByEmailAndHash(email, hash).map(AccountModel::to).orElse(null);
        if (account == null) throw new EmailOrPasswordIncorrectException();
        return account;
    }

    public Account update(String idUser, Account in) {



        AccountModel account = accountRepository.findById(idUser).orElse(null);
        if (account == null) throw new AccountNotFoundException(idUser);

        if (in.name() != null) {
            account.name(in.name());
        }
        if (in.email() != null) {
            account.email(in.email());
        }
        if (in.password() != null) {
            account.hash(calculateHash(in.password()));
            in.password(null);
        }

        return accountRepository.save(account).to();
    }

    private String calculateHash(String text) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(text.getBytes(StandardCharsets.UTF_8));
            byte[] encoded = Base64.getEncoder().encode(hash);
            return new String(encoded);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }


    public Boolean isAccount(String id) {
        Optional<AccountModel> account = accountRepository.findById(id);
        if (account.isPresent()){
            return true;
        }
        return false; 
    }
    
}
