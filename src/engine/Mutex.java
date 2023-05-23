package engine;

import java.util.ArrayList;

public class Mutex {

    private boolean status;
    ArrayList<Integer> waitingProcesses;

    public Mutex(){
        status = true;
        waitingProcesses = new ArrayList<>();
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public ArrayList<Integer> getWaitingProcesses() {
        return waitingProcesses;
    }

    public boolean semWait(int processId){
        if (status == true){
            status = false;
            return true;
        }
        else {
            waitingProcesses.add(processId);
            return false;
        }
    }

    public ArrayList<Integer> semSignal(){
        status = true;
        return waitingProcesses;
    }

    public void clearWaitingProcesses(){
        waitingProcesses.clear();
    }
}
