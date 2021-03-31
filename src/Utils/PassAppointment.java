package Utils;

/**
 * PassAppointment class is used as a container to pass Appointments between windows easily.
 */
public class PassAppointment {

    private static Appointment appointment;

    public PassAppointment(Appointment appointment){
        this.appointment = appointment;
    }

    /**
     * @return The appointment to be passed.
     */
    public static Appointment getAppointment(){
        return appointment;
    }
}
