package engine;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

public class OperatingSystem {

    //<p1,p2,p3>
    // 0  1  2
    private ArrayList<Process> memory;
    private ArrayList<Integer> processesLocations;
//    private ArrayList<Integer> processesInstructionsPerSlice;

    private int availableMemorySpace;
    private int numberOfProcesses;
    private int completedProcesses;

    private LinkedList<Integer> readyQueue;
    private LinkedList<Integer> blockedQueue;

    private boolean fileMutex;
    private boolean inputMutex;
    private boolean outputMutex;

    private int maximumInstructionsPerSlice;

    public OperatingSystem(){
        memory = new ArrayList<>();
        availableMemorySpace = 40;
        numberOfProcesses = 0;
        readyQueue = new LinkedList<>();
        blockedQueue = new LinkedList<>();
        processesLocations = new ArrayList<Integer>();
//        processesInstructionsPerSlice = new ArrayList<Integer>();

        maximumInstructionsPerSlice = 2;
        completedProcesses = 0;
    }

    public ArrayList<Process> getMemory() {
        return memory;
    }

    public ArrayList<Integer> getProcessesLocations() {
        return processesLocations;
    }

    public int getAvailableMemorySpace() {
        return availableMemorySpace;
    }

    public void setAvailableMemorySpace(int availableMemorySpace) {
        this.availableMemorySpace = availableMemorySpace;
    }

    public int getNumberOfProcesses() {
        return numberOfProcesses;
    }

    public void setNumberOfProcesses(int numberOfProcesses) {
        this.numberOfProcesses = numberOfProcesses;
    }

    public LinkedList<Integer> getReadyQueue() {
        return readyQueue;
    }

    public LinkedList<Integer> getBlockedQueue() {
        return blockedQueue;
    }

    public boolean getFileMutex() {
        return fileMutex;
    }

    public void setFileMutex(boolean fileMutex) {
        this.fileMutex = fileMutex;
    }

    public boolean getInputMutex() {
        return inputMutex;
    }

    public void setInputMutex(boolean inputMutex) {
        this.inputMutex = inputMutex;
    }

    public boolean getOutputMutex() {
        return outputMutex;
    }

    public void setOutputMutex(boolean outputMutex) {
        this.outputMutex = outputMutex;
    }

    public int getCompletedProcesses() {
        return completedProcesses;
    }

    public void setCompletedProcesses(int completedProcesses) {
        this.completedProcesses = completedProcesses;
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


        Integer a = null;
        Integer b = null;
        Integer c = null;

        PCB pcb = new PCB(++numberOfProcesses,ProcessState.CREATED,0,0,instructions.size()-1);
        Process process = new Process(a,b,c,pcb,instructions);

        availableMemorySpace -= process.getProcessBlockSize();

        memory.add(process);
        processesLocations.add(process.getPcb().getProcessID());
//        processesInstructionsPerSlice.add(0);
        readyQueue.add(process.getPcb().getProcessID());

    }


    public void reSchedule(){
        int processId = readyQueue.getFirst();
        int processLocation = processesLocations.indexOf(processId);
        Process currentProcess = memory.get(processId);

        if(currentProcess.getCompletedInstructions() >= maximumInstructionsPerSlice){
            currentProcess.getPcb().setState(ProcessState.READY);
            readyQueue.add(readyQueue.remove());
        }

    }

    public Object takeUserInput(){
        Scanner scanner = new Scanner(System.in);

        if(scanner.hasNextInt()){
            return scanner.nextInt();
        }
        else if(scanner.hasNextDouble()){
            return scanner.nextDouble();
        }
        return scanner.nextLine();

    }

    public void writeToMemory(String var, int processLocation,boolean isReadFile,String filePathVar){
        Process process = memory.get(processLocation);
        Object filePath = "";
        switch (filePathVar){
            case "a" : filePath = process.getA();break;
            case "b" : filePath = process.getB();break;
            case "c" : filePath = process.getC();break;
        }
        Object value;
        if(isReadFile)
            value = readFile((String) filePath);
        else
            value = takeUserInput();

        switch (var){
            case "a" : process.setA(value);break;
            case "b" : process.setB(value);break;
            case "c" : process.setC(value);break;
        }

        process.getPcb().setPC(process.getPcb().getPC()+1);
        if(process.getPcb().getPC() == process.getInstructions().size()){
            process.getPcb().setState(ProcessState.COMPLETED);
            readyQueue.remove(0);
//            completedProcesses++;
        }
        process.setCompletedInstructions(process.getCompletedInstructions()+1);
        process.getPcb().setState(ProcessState.RUNNING);
//        processesInstructionsPerSlice.set(processLocation,completedInstructions+1);
    }


    public Object readFile(String filePath){
        File file = new File("src/"+filePath);
        String str = "";
        try {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNext()){
                str += scanner.nextLine();
            }
            scanner.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return str;
    }

    public boolean checkAllProcessesFinished(){
        return readyQueue.isEmpty();
        //return completedProcesses == numberOfProcesses;
    }




    public void displayMemoryContent(){
        int memoryLocation = -1;
        for (Process process : memory){
            System.out.println("======================================Process "+process.getPcb().getProcessID()+"====================================");
            System.out.println("Executed "+process.getCompletedInstructions()+"/"+maximumInstructionsPerSlice);
            System.out.println(++memoryLocation+" : a = "+process.getA());
            System.out.println(++memoryLocation+" : b = "+process.getB());
            System.out.println(++memoryLocation+" : c = "+process.getC());
            System.out.println(++memoryLocation+" : ID = "+process.getPcb().getProcessID());
            System.out.println(++memoryLocation+" : PC = "+process.getPcb().getPC());
            System.out.println(++memoryLocation+" : State = "+process.getPcb().getState());
            System.out.println(++memoryLocation+" : Instruction Start = "+process.getPcb().getStart());
            System.out.println(++memoryLocation+" : Instruction End = "+process.getPcb().getEnd());

            for (int i = 0 ; i < process.getInstructions().size() ; i++){
                if (process.getPcb().getPC() == i){
                    System.out.println(++memoryLocation+" : instruction = "+process.getInstructions().get(i) + "     <<<<<<<<<<< PC");
                }
                System.out.println(++memoryLocation+" : instruction = "+process.getInstructions().get(i));
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


        System.out.println(str);
        System.out.println(processesLocations);
        displayMemoryContent();
        return "";
    }

    public static void main(String[] args) {


    }

}
