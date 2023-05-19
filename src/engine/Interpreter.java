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
        operatingSystem.reSchedule();

        int processId = operatingSystem.getReadyQueue().getFirst();
        int processLocation = operatingSystem.getProcessesLocations().indexOf(processId);
        Process currentProcess = operatingSystem.getMemory().get(processLocation);

        String[] instruction = currentProcess.getInstructions().get( currentProcess.getPcb().getPC() ).split(" ");

        if(instruction[0].equals("assign")){
            if(instruction.length == 3){
                operatingSystem.writeToMemory(instruction[1],processLocation,false,"");
//                operatingSystem.reSchedule();
            }
            else{
                operatingSystem.writeToMemory(instruction[1],processLocation,true,instruction[3]);
//                operatingSystem.reSchedule();
            }

        }
        else if(instruction[0].equals("print")){

        }
        else if(instruction[0].equals("printFromTo")){

        }
        else if(instruction[0].equals("writeFile")){

        }
        else if(instruction[0].equals("readFile")){

        }
        else if(instruction[0].equals("semWait")){

        }
        else if(instruction[0].equals("semSignal")){

        }

        System.out.println(operatingSystem.toString());
        if(!operatingSystem.checkAllProcessesFinished())
            execute();
    }



    public static void main(String[] args) {
        Interpreter interpreter = new Interpreter();

        for (int i = 1 ; i <= 3 ; i++){
            interpreter.getOs().createProcess("src/Program_"+i+".txt");
        }
        interpreter.execute();
//        System.out.println(interpreter.getOs().toString());
    }

}
