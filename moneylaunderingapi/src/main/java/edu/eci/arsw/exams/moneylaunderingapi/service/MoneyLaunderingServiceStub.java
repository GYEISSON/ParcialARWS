package edu.eci.arsw.exams.moneylaunderingapi.service;

import edu.eci.arsw.exams.moneylaunderingapi.model.SuspectAccount;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;


@Service
public class MoneyLaunderingServiceStub implements MoneyLaunderingService {
	
	public ArrayList<SuspectAccount> suspectAccounts= new ArrayList<>();
	
    @Override
    public void updateAccountStatus(SuspectAccount suspectAccount) {
    	suspectAccounts.forEach(suspect -> {
    		if(suspect.getAccountId().equals(suspectAccount.getAccountId())) {
    			suspect.setAmountOfSmallTransactions(suspectAccount.getAmountOfSmallTransactions()); 
    		}
    	});
    }

    @Override
    public SuspectAccount getAccountStatus(String accountId) {
    	
    	for(SuspectAccount suspect : suspectAccounts){
    		if(suspect.getAccountId().equals(accountId)) {
    			return suspect;
    		}
    	}
    	return null;
    	
    }

    @Override
    public List<SuspectAccount> getSuspectAccounts() {
        return suspectAccounts;
    }

	@Override
	public void addSuspectAccount(SuspectAccount suspectAccount) {
		suspectAccounts.add(suspectAccount);
	}
}
