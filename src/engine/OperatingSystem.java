package engine;

import java.io.File;
import java.util.*;

public class OperatingSystem {

    private ArrayList<Process> memory;
    private Hashtable<Integer,Integer> processesLocations;
    private Hashtable<Integer,Integer> completedInstructions;
    private Hashtable<Integer,Integer> processBlockSize;

    private int availableMemorySpace;
    private int numberOfProcesses;

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
        processesLocations = new Hashtable<Integer,Integer>();
        completedInstructions = new Hashtable<Integer,Integer>();
        processBlockSize = new Hashtable<Integer,Integer>();

        maximumInstructionsPerSlice = 2;
    }

    public ArrayList<Process> getMemory() {
        return memory;
    }

    public Hashtable<Integer,Integer> getProcessesLocations() {
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



    public Hashtable<Integer, Integer> getCompletedInstructions() {
        return completedInstructions;
    }


    public Hashtable<Integer, Integer> getProcessBlockSize() {
        return processBlockSize;
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

        int oldNumOfProcesses = numberOfProcesses;
        PCB pcb = new PCB(++numberOfProcesses,ProcessState.READY,0,0,instructions.size()-1);
        Process process = new Process(a,b,c,pcb,instructions);

        availableMemorySpace -= process.getProcessBlockSize();

        memory.add(process);
        processesLocations.put(process.getPcb().getProcessID(),oldNumOfProcesses);
        completedInstructions.put(process.getPcb().getProcessID(),0);
        processBlockSize.put(process.getPcb().getProcessID(),8+instructions.size());
        readyQueue.add(process.getPcb().getProcessID());

    }


    public void reSchedule(){
        int processId = readyQueue.getFirst();
        int processLocation = processesLocations.get(processId);
        Process currentProcess = memory.get(processLocation);

        currentProcess.setCompletedInstructions(currentProcess.getCompletedInstructions() + 1);
        currentProcess.getPcb().setState(ProcessState.RUNNING);
        currentProcess.getPcb().setPC(currentProcess.getPcb().getPC()+1);
        if(currentProcess.getPcb().getPC() == currentProcess.getInstructions().size()){
            currentProcess.getPcb().setState(ProcessState.COMPLETED);
            readyQueue.remove(0);
        }

        if(currentProcess.getCompletedInstructions() >= maximumInstructionsPerSlice){
            currentProcess.getPcb().setState(ProcessState.READY);
            if(readyQueue.isEmpty())
                return;
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

        reSchedule();
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
    }




    public void displayMemoryContent(){
        int memoryLocation = -1;
        for (Process process : memory){
            System.out.println("===================================Process "+process.getPcb().getProcessID()+"==========================");
            System.out.println("Executed "+process.getCompletedInstructions()+" instructions");
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
                    System.out.println(++memoryLocation+" : instruction = "+process.getInstructions().get(i) + "   <<<<<<<<<<< PC");
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
