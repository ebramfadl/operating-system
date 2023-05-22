package engine;

public class Interpreter {

    private OperatingSystem operatingSystem ;

    public Interpreter(){
        operatingSystem = new OperatingSystem();
    }

    public OperatingSystem getOs(){
        return operatingSystem;
    }

    public void execute(){
        while (! operatingSystem.checkAllProcessesFinished()) {

            int processId = operatingSystem.getReadyQueue().getFirst();
            int processLocation = operatingSystem.getProcessesLocations().get(processId);
            Process currentProcess = operatingSystem.getMemory().get(processLocation);

            String line = currentProcess.getInstructions().get(currentProcess.getPcb().getPC());
            String[] instruction = line.split(" ");

            System.out.println("Executing instruction [ "+line+" ] From Process "+currentProcess.getPcb().getProcessID());
            if (instruction[0].equals("assign")) {
                if (instruction.length == 3) {
                    System.out.println("Enter the value of variable : "+instruction[1]);
                    operatingSystem.writeToMemory(instruction[1], processLocation, false, "");
                } else {
                    operatingSystem.writeToMemory(instruction[1], processLocation, true, instruction[3]);
                }

            }
            else if (instruction[0].equals("print")) {

            }
            else if (instruction[0].equals("printFromTo")) {

            }
            else if (instruction[0].equals("writeFile")) {

            }
            else if (instruction[0].equals("semWait")) {

            }
            else if (instruction[0].equals("semSignal")) {

            }
            System.out.println(operatingSystem);
        }

    }



    public static void main(String[] args) {
        Interpreter interpreter = new Interpreter();

        for (int i = 1 ; i <= 3 ; i++){
            interpreter.getOs().createProcess("src/Program_"+i+".txt");
        }
        interpreter.execute();
    }

}
