package engine;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

public class OperatingSystem {

    //<p1,p2,p3>
    // 0  1  2
    private ArrayList<Object[]> memory;
    private int availableMemorySpace;
    private int numberOfProcesses;
    private Queue<Integer> readyQueue;
    private Queue<Integer> blockedQueue;

    private boolean fileMutex;
    private boolean inputMutex;
    private boolean outputMutex;

    public OperatingSystem(){
        memory = new ArrayList<>();
        availableMemorySpace = 40;
        numberOfProcesses = 0;
        readyQueue = new LinkedList<>();
        blockedQueue = new LinkedList<>();
    }

    public void createProcess(String filePath){

        File file = new File(filePath);
        ArrayList<String> instructions = new ArrayList<>();

        try {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNext()){
                String line = scanner.nextLine();
                instructions.add(line);
            }
            scanner.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }

        Object[] arr = new Object[8+instructions.size()];
        availableMemorySpace -= arr.length;

        arr[0] = null;//a
        arr[1] = null;//b
        arr[2] = null;//c
        arr[3] = ++numberOfProcesses;//PID
        arr[4] = 8;//PC
        arr[5] = ProcessState.CREATED;//State
        arr[6] = 8;//Instructions Start
        arr[7] = 7+instructions.size();//Instructions End

        int j = 8;
        for ( int i = 0 ; i < instructions.size() ; i++){
            arr[j] = instructions.get(i);
            j++;
        }

        memory.add(arr);
        readyQueue.add((int)arr[3]);

    }

    public void displayMemoryContent(){

        for (Object[] process : memory){
            System.out.println("======================================Process "+process[3]+"====================================");
            System.out.println("0 : a = "+process[0]);
            System.out.println("1 : b = "+process[1]);
            System.out.println("2 : c = "+process[2]);
            System.out.println("3 : ID = "+process[3]);
            System.out.println("4 : PC = "+process[4]);
            System.out.println("5 : State = "+process[5]);
            System.out.println("6 : Instruction Start = "+process[6]);
            System.out.println("7 : Instruction End = "+process[7]);

            for (int i = 8 ; i < process.length ; i++){
                if ((int)process[4] == i){
                    System.out.println(i + " : instruction = "+process[i] + "     <<<<<<<<<<< PC");
                }
                System.out.println(i + " : instruction = "+process[i]);
            }
            System.out.println("=====================================================================");
            System.out.println();

        }

    }

    public String toString(){
        String str = "";
        str += "Number of processes = "+numberOfProcesses+"\n"+
        "Available Memory Space = "+availableMemorySpace+"\n"+
        "Ready Queue [ ";

        for (Integer pid : readyQueue){
            str += pid + " , ";
        }
        str += "]"+"\n";

        str += "Blocked Queue [ ";
        for (Integer pid : blockedQueue){
            str += pid + " , ";
        }
        str += "]"+"\n";

        return str;
    }

    public static void main(String[] args) {

        OperatingSystem os = new OperatingSystem();

        for (int i = 1 ; i <= 3 ; i++){
            os.createProcess("src/Program_"+i+".txt");
        }
        System.out.println(os);
        os.displayMemoryContent();
    }

}
