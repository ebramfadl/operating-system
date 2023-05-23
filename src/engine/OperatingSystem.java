package engine;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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

    public Process chooseProcess(){
        int processId = readyQueue.getFirst();
        int processLocation = processesLocations.get(processId);
        System.out.println("Process "+processId+" is chosen");
        Process process = memory.get(processLocation);
        process.getPcb().setState(ProcessState.RUNNING);
        return process;
    }

    public void reSchedule(){
        int processId = readyQueue.getFirst();
        int processLocation = processesLocations.get(processId);
        Process currentProcess = memory.get(processLocation);

        int oldInstructions = completedInstructions.get(processId);
        completedInstructions.put(processId,oldInstructions+1);

        currentProcess.getPcb().setState(ProcessState.RUNNING);
        currentProcess.getPcb().setPC(currentProcess.getPcb().getPC()+1);
        if(currentProcess.getPcb().getPC() == currentProcess.getInstructions().size()){
            currentProcess.getPcb().setState(ProcessState.COMPLETED);
            readyQueue.remove(0);
        }

        if(completedInstructions.get(processId) >= maximumInstructionsPerSlice){
            currentProcess.getPcb().setState(ProcessState.READY);
            if(readyQueue.isEmpty())
                return;
            readyQueue.add(readyQueue.remove());
            completedInstructions.put(processId,0);
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

    public void writeToMemory(String var, int processID,boolean isReadFile,String filePathVar){
        int processLocation = processesLocations.get(processID);
        Process process = memory.get(processLocation);
        Object filePath = "";
        switch (filePathVar){
            case "a" : filePath = process.getA();break;
            case "b" : filePath = process.getB();break;
            case "c" : filePath = process.getC();break;
        }
        Object value;
        if(isReadFile) {
            System.out.println("Process "+processID+" is reading file "+filePath);
            value = readFile((String) filePath);
        }
        else {
            System.out.println("Process "+processID+" is requesting a user input for "+var);
            value = takeUserInput();
        }
        switch (var){
            case "a" : process.setA(value);System.out.println("Process "+processID+" is setting a = "+process.getA());break;
            case "b" : process.setB(value);System.out.println("Process "+processID+" is setting b = "+process.getB());break;
            case "c" : process.setC(value);System.out.println("Process "+processID+" is setting c = "+process.getC());break;
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
    public void print(String var, int processId) {
        int processLocation = processesLocations.get(processId);
        Process process = memory.get(processLocation);
        switch (var){
            case "a":System.out.println("Process "+processId+" prints a = "+process.getA());break;
            case "b":System.out.println("Process "+processId+" prints b = "+process.getB());break;
            case "c":System.out.println("Process "+processId+" prints c = "+process.getC());break;
        }
        reSchedule();
    }

    public void printFromTo(String x, String y, int processId) {
        int processLocation = processesLocations.get(processId);
        Process process = memory.get(processLocation);
        int a = 0,b = 0;
        switch (x) {
            case "a": a = (int)process.getA();break;
            case "b": a = (int)process.getB();break;
            case "c": a = (int)process.getC();break;
        }
        switch (y) {
            case "a": b = (int)process.getA();break;
            case "b": b = (int)process.getB();break;
            case "c": b = (int)process.getC();break;
        }
        System.out.println("Process "+processId+" is printing numbers FROM "+a+" TO "+b);
        for (int i=a;i<=b;i++) {
                System.out.print(i + " , ");
        }
        reSchedule();
    }

    public void writeFile(String x, String y, int processId) throws IOException {
        int processLocation = processesLocations.get(processId);
        Process process = memory.get(processLocation);
        String filename = "src/";
        Object data = null;
        switch (x) {
            case "a":  filename += (String)process.getA();break;
            case "b":  filename += (String)process.getB();break;
            case "c":  filename += (String)process.getC();break;
        }
        switch (y) {
            case "a": data = (Object)process.getA();break;
            case "b": data = (Object)process.getB();break;
            case "c": data = (Object)process.getC();break;
        }
        System.out.println("Process "+processId+" is writing "+data+" to file "+filename);
        FileWriter fileWriter = new FileWriter(filename);
        fileWriter.write(data.toString());
        fileWriter.close();

        reSchedule();
    }

    public void semWait() {

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
                else
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
        System.out.println(completedInstructions);
        displayMemoryContent();
        return "";
    }

    public static void main(String[] args) {


    }

}
