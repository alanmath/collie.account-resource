package insper.collie.account;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import lombok.NonNull;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    


    public Account create(Account in) {
        in.hash(calculateHash(in.password()));
        in.password(null);
        return accountRepository.save(new AccountModel(in)).to();
    }

    public Account read(@NonNull String id) {
        return accountRepository.findById(id).map(AccountModel::to).orElse(null);
    }

    public Account login(String email, String password) {
        String hash = calculateHash(password);
        return accountRepository.findByEmailAndHash(email, hash).map(AccountModel::to).orElse(null);
    }

    public Account update(String idUser, String id, Account in) {

        if(!idUser.equals(id)) return null;

        AccountModel account = accountRepository.findById(id).orElse(null);
        if (account == null) return null;

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
