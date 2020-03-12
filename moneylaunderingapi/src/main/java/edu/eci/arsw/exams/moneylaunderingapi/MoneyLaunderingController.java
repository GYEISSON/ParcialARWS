package edu.eci.arsw.exams.moneylaunderingapi;


import edu.eci.arsw.exams.moneylaunderingapi.model.SuspectAccount;
import edu.eci.arsw.exams.moneylaunderingapi.service.MoneyLaunderingService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*
 * @Author yeisson Gualdron
 * 
 */
@RestController
public class MoneyLaunderingController
{
	
	@Autowired
    MoneyLaunderingService moneyLaunderingService;

    @RequestMapping( value = "/fraud-bank-accounts")
    public List<SuspectAccount> offendingAccounts() {
        return moneyLaunderingService.getSuspectAccounts();
    }
    
    @PostMapping(value = "/fraud-bank-accounts")
    public ResponseEntity<?> addSuspectAccount(@RequestBody SuspectAccount suspectAccount ){
    	moneyLaunderingService.addSuspectAccount(suspectAccount);
    	return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @PutMapping(value = "/fraud-bank-accounts/{accountId}")
    public ResponseEntity<?> updateAccountStatus(@RequestBody SuspectAccount suspectAccount) {
    	moneyLaunderingService.updateAccountStatus(suspectAccount);
    	return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
    
    @GetMapping(value = "/fraud-bank-accounts/{accountId}")
    public List<SuspectAccount> getAccountStatus(@PathVariable String accountId) {
        return moneyLaunderingService.getSuspectAccounts();
    }

    
}
