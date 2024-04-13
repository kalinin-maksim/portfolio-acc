package edu.kalinin.acc.dumb;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import edu.kalinin.acc.repository.AccountRepository;
import edu.kalinin.acc.repository.BalanceRepository;
import edu.kalinin.acc.repository.EntryRepository;
import edu.kalinin.acc.repository.PostingRepository;

@RestController
@RequiredArgsConstructor
public class DeleteController {

    private final AccountRepository accountRepository;

    private final EntryRepository entryRepository;

    private final PostingRepository postingRepository;

    private final BalanceRepository balanceRepository;

    @DeleteMapping("/delete/{table}")
    public ResponseEntity<String> deleteTable (@PathVariable("table") String tableName) {

        switch (tableName) {
            case "acc_account_balance":
                balanceRepository.deleteAll();
                break;
            case "acc_account_entry":
                entryRepository.deleteAll();
                break;
            case "acc_account_posting":
                postingRepository.deleteAll();
                break;
            case "acc_account":
                accountRepository.deleteAll();
                break;
            default:
                return ResponseEntity.badRequest().body("Table not found");
        }

        return ResponseEntity.ok("Deleted table " + tableName);
    }
}
