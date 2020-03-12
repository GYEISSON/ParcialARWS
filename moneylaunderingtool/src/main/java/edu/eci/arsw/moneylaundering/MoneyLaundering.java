package edu.eci.arsw.moneylaundering;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MoneyLaundering
{
    private TransactionAnalyzer transactionAnalyzer;
    private TransactionReader transactionReader;
    private int amountOfFilesTotal;
    private AtomicInteger amountOfFilesProcessed;
    private static ArrayList<Thread> threads = new ArrayList<Thread>();
    private static Object monitor = new Object();
    private static boolean pause;

    public MoneyLaundering()
    {
        transactionAnalyzer = new TransactionAnalyzer();
        transactionReader = new TransactionReader();
        amountOfFilesProcessed = new AtomicInteger();
    }

    public void processTransactionData(int threadsNumber)
    {

        amountOfFilesProcessed.set(0);
        List<File> transactionFiles = getTransactionFileList();
        
        amountOfFilesTotal = transactionFiles.size();
        
        int numFile=1;
        for(File transactionFile : transactionFiles)
        {            
        	
            List<Transaction> transactions = transactionReader.readTransactionsFromFile(transactionFile);
            int delta=transactions.size()/threadsNumber;
            for(int thread=0;thread<threadsNumber;thread++){
            	int i=thread*delta;
            	int j=(thread+1)*delta;
            	threads.add( new Thread(() -> segmentTransactionFile(i,j,transactions)));
            	threads.get(thread).start();
            }
 
            try {
            	for(Thread t : threads) {
                	t.join();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
            threads.clear();
            amountOfFilesProcessed.incrementAndGet();
            System.out.println("File "+(numFile++)+" finished.");
        }
    }
    
    public void segmentTransactionFile(int i, int j,List<Transaction> transactions) {
    	for(int x=i;x<j;x++)
        {
    		synchronized(monitor) {
    			if(pause) {
    				try {
    					monitor.wait();
    				} catch (InterruptedException e) {
                        e.printStackTrace();
                    }
    			}
    			
    		}
    		
            transactionAnalyzer.addTransaction(transactions.get(x));
        }
    }

    public List<String> getOffendingAccounts()
    {
        return transactionAnalyzer.listOffendingAccounts();
    }
    public static void stop() {
    	for(Thread t: threads) {
    		try {
				t.wait();
			} catch (InterruptedException e) {

				e.printStackTrace();
			}
    	}
    	
    }

    private List<File> getTransactionFileList()
    {
        List<File> csvFiles = new ArrayList<>();
        try (Stream<Path> csvFilePaths = Files.walk(Paths.get("src/main/resources/")).filter(path -> path.getFileName().toString().endsWith(".csv"))) {
            csvFiles = csvFilePaths.map(Path::toFile).collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return csvFiles;
    }

    public static void main(String[] args)
    {
        MoneyLaundering moneyLaundering = new MoneyLaundering();
        //ingrese  N en processTransactionData( N ) donde N es el numero de hilos 
        Thread processingThread = new Thread(() -> moneyLaundering.processTransactionData(5));
        processingThread.start();
        pause=false;
        
        

        while(processingThread.getState()!=Thread.State.TERMINATED)
        {
        	System.out.println("Los hilos estan "+ ((pause)?"detenidos":"activos"));
            Scanner scanner = new Scanner(System.in);
            String line = scanner.nextLine();
            if(line.contains("exit"))
                break;
            
            if(pause) {
            	pause=false;
            	synchronized(monitor) {
            		monitor.notifyAll();
            	}
	            
            }else {
            	pause = true;
            	String message = "Processed %d out of %d files.\nFound %d suspect accounts:\n%s";
	            List<String> offendingAccounts = moneyLaundering.getOffendingAccounts();
	            String suspectAccounts = offendingAccounts.stream().reduce("", (s1, s2)-> s1 + "\n"+s2);
	            message = String.format(message, moneyLaundering.amountOfFilesProcessed.get(), moneyLaundering.amountOfFilesTotal, offendingAccounts.size(), suspectAccounts);
	            System.out.println(message);
            }
            
        }

    }


}
