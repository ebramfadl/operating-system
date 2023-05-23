package engine;

import java.io.IOException;

public class Interpreter {

    private OperatingSystem operatingSystem ;

    public Interpreter(){
        operatingSystem = new OperatingSystem();
    }

    public OperatingSystem getOs(){
        return operatingSystem;
    }

    public void execute() throws IOException {
        while (! operatingSystem.checkAllProcessesFinished()) {

            Process currentProcess = operatingSystem.chooseProcess();
            int processId = currentProcess.getPcb().getProcessID();

            String line = currentProcess.getInstructions().get(currentProcess.getPcb().getPC());
            String[] instruction = line.split(" ");

            System.out.println("Executing instruction [ "+line+" ] From Process "+currentProcess.getPcb().getProcessID());
            if (instruction[0].equals("assign")) {
                if (instruction.length == 3) {

                    if(operatingSystem.getProcessesInput().containsKey(processId)){
                        System.out.println("Process "+processId+" is assignning "+instruction[1]+" to input");
                        operatingSystem.writeToMemory(instruction[1], processId,operatingSystem.getProcessesInput().get(processId));
                    }
                    else {
                        System.out.println("Process "+processId+" is taking a user input ");
                        Object input = operatingSystem.takeUserInput();
                        operatingSystem.getProcessesInput().put(processId,input);
                    }
                }
                else {
                    if(operatingSystem.getProcessesInput().containsKey(processId)){
                        System.out.println("Process "+processId+" is assignning "+instruction[1]+" to data in file "+instruction[3]);
                        operatingSystem.writeToMemory(instruction[1],processId,operatingSystem.getProcessesInput().get(processId));
                    }
                    else {
                        System.out.println("Process "+processId+" is reading file "+instruction[3]);
                        Object fileData = operatingSystem.readFile(instruction[3]);
                        operatingSystem.getProcessesInput().put(processId,fileData);
                    }
                }

            }
            else if (instruction[0].equals("print")) {
                operatingSystem.print(instruction[1],processId);
            }
            else if (instruction[0].equals("printFromTo")) {
                operatingSystem.printFromTo(instruction[1],instruction[2],processId);
            }

            else if (instruction[0].equals("writeFile")) {
                operatingSystem.writeFile(instruction[1],instruction[2],processId);
            }
            else if (instruction[0].equals("semWait")) {
                operatingSystem.semWait(processId,instruction[1]);
            }
            else if (instruction[0].equals("semSignal")) {
                operatingSystem.semSignal(processId,instruction[1]);
            }
            System.out.println(operatingSystem);
        }

    }



    public static void main(String[] args) throws IOException {
        Interpreter interpreter = new Interpreter();

        for (int i = 1 ; i <= 3 ; i++){
            interpreter.getOs().createProcess("src/Program_"+i+".txt");
        }
        interpreter.execute();
    }

}
